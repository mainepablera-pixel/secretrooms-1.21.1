package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SecretDoor extends SecretBaseBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private final boolean iron;

    public SecretDoor(BlockBehaviour.Properties properties) {
        this(properties, false);
    }

    public SecretDoor(BlockBehaviour.Properties properties, boolean iron) {
        super(properties);
        this.iron = iron;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(HINGE, DoorHingeSide.LEFT)
                .setValue(POWERED, false)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(SOLID, false)
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, HINGE, POWERED, HALF);
        super.createBlockStateDefinition(builder);
    }

    // Delegate collision and outline geometry to vanilla's door state. Keeping
    // this mapping identical to the baked door model prevents hinge-dependent
    // panels from being placed on the opposite edge.
    private static BlockState vanillaDoorState(BlockState state) {
        return Blocks.OAK_DOOR.defaultBlockState()
                .setValue(DoorBlock.FACING, state.getValue(FACING))
                .setValue(DoorBlock.OPEN, state.getValue(OPEN))
                .setValue(DoorBlock.HINGE, state.getValue(HINGE))
                .setValue(DoorBlock.POWERED, state.getValue(POWERED))
                .setValue(DoorBlock.HALF, state.getValue(HALF));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return vanillaDoorState(state).getShape(level, pos, ctx);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        return vanillaDoorState(state).getCollisionShape(level, pos, context);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos,
                                     CollisionContext context) {
        return vanillaDoorState(state).getVisualShape(level, pos, context);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hit) {
        if (this.iron) {
            return InteractionResult.PASS;
        }
        state = state.cycle(OPEN);
        level.setBlock(pos, state, 3);
        requestDoorModelUpdate(level, pos);
        // Play the matching vanilla sound from the authoritative server so it
        // reaches nearby clients reliably, including the player using the door.
        if (!level.isClientSide) {
            level.playSound(null, pos, getDoorSound(state.getValue(OPEN)), SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, state.getValue(OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        }
        // sync the other half
        BlockPos other = otherHalfPos(pos, state);
        BlockState otherState = level.getBlockState(other);
        if (otherState.is(this)) {
            level.setBlock(other, otherState.setValue(OPEN, state.getValue(OPEN)), 3);
            requestDoorModelUpdate(level, other);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
                                BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide || state.getBlock() != this) {
            return;
        }

        BlockPos otherPos = otherHalfPos(pos, state);
        boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(otherPos);
        BlockState otherState = level.getBlockState(otherPos);
        // Manual opening must not be undone by the neighbor update caused by setBlock.
        // Only a real redstone POWERED transition should force OPEN to follow power.
        boolean currentNeedsSync = state.getValue(POWERED) != powered;
        boolean otherNeedsSync = otherState.is(this) && otherState.getValue(POWERED) != powered;
        if (!currentNeedsSync && !otherNeedsSync) return;

        boolean wasOpen = state.getValue(OPEN);
        if (currentNeedsSync) {
            BlockState updated = state.setValue(POWERED, powered).setValue(OPEN, powered);
            level.setBlock(pos, updated, 3);
            if (wasOpen != powered) {
                // The first half to process the transition synchronizes the other
                // half, so this branch emits exactly one sound per door transition.
                level.playSound(null, pos, getDoorSound(powered), SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, powered ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }
            requestDoorModelUpdate(level, pos);
        }

        // A door is one logical block represented by two block states and two
        // block entities. Keep both halves' open/powered flags and model data
        // synchronized when redstone reaches either half.
        if (otherState.is(this)) {
            if (otherNeedsSync) {
                BlockState otherUpdated = otherState.setValue(POWERED, powered).setValue(OPEN, powered);
                level.setBlock(otherPos, otherUpdated, 3);
            }
            requestDoorModelUpdate(level, otherPos);
        }
    }

    private SoundEvent getDoorSound(boolean opening) {
        if (iron) {
            return opening ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE;
        }
        return opening ? SoundEvents.WOODEN_DOOR_OPEN : SoundEvents.WOODEN_DOOR_CLOSE;
    }

    private static BlockPos otherHalfPos(BlockPos pos, BlockState state) {
        return pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER
                ? Direction.UP : Direction.DOWN);
    }

    private static void requestDoorModelUpdate(Level level, BlockPos pos) {
        SecretBaseBlock.getTile(level, pos).ifPresent(te -> te.requestModelDataUpdate());
        if (!level.isClientSide) {
            BlockState current = level.getBlockState(pos);
            level.sendBlockUpdated(pos, current, current, 8);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(ctx)) {
            boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
            return this.defaultBlockState()
                    .setValue(FACING, ctx.getHorizontalDirection())
                    .setValue(HINGE, getHinge(ctx))
                    .setValue(POWERED, powered)
                    .setValue(OPEN, powered)
                    .setValue(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    private DoorHingeSide getHinge(BlockPlaceContext ctx) {
        // Simplified hinge logic
        Direction dir = ctx.getHorizontalDirection();
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        Direction right = dir.getClockWise();
        BlockPos rightPos = pos.relative(right);
        BlockState rightState = level.getBlockState(rightPos);
        Direction left = dir.getCounterClockWise();
        BlockPos leftPos = pos.relative(left);
        BlockState leftState = level.getBlockState(leftPos);

        int solid = (rightState.isCollisionShapeFullBlock(level, rightPos) ? -1 : 0)
                  + (leftState.isCollisionShapeFullBlock(level, leftPos) ? 1 : 0);
        boolean rightDoor = rightState.is(this) && rightState.getValue(HALF) == DoubleBlockHalf.LOWER;
        boolean leftDoor  = leftState.is(this)  && leftState.getValue(HALF)  == DoubleBlockHalf.LOWER;
        if ((!rightDoor || leftDoor) && solid <= 0) {
            if ((!leftDoor || rightDoor) && solid >= 0) {
                double hx = ctx.getClickLocation().x - pos.getX();
                double hz = ctx.getClickLocation().z - pos.getZ();
                return (dir == Direction.NORTH && hz < 0.5) || (dir == Direction.SOUTH && hz > 0.5)
                        || (dir == Direction.WEST && hx > 0.5) || (dir == Direction.EAST && hx < 0.5)
                        ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
            }
            return DoorHingeSide.LEFT;
        }
        return DoorHingeSide.RIGHT;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockPos upperPos = pos.above();
        level.setBlock(upperPos, state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
        super.setPlacedBy(level, pos, state, placer, stack);

        // Both halves need the same mirrored state. Otherwise the upper half
        // remains the tile entity's default stone state and looks mismatched.
        if (!level.isClientSide) {
            SecretBaseBlock.getMirrorState(level, pos).ifPresent(mirror ->
                    SecretBaseBlock.getTile(level, upperPos).ifPresent(upper -> upper.setMirrorState(mirror)));
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (facing.getAxis() == Direction.Axis.Y
                && half == DoubleBlockHalf.LOWER == (facing == Direction.UP)
                && (!facingState.is(this) || facingState.getValue(HALF) == half)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) {
            return belowState.is(this);
        }
        // Leaves (and other full-height blocks such as glass) have a full-cube
        // collision box but are not "sturdy" on their top face, so vanilla's
        // isFaceSturdy check rejected them and a secret door could not be placed
        // on leaf blocks. Accept sturdy tops, leaves, and anything whose collision
        // box reaches the full block height.
        return belowState.isFaceSturdy(level, below, Direction.UP)
                || belowState.is(BlockTags.LEAVES)
                || belowState.getCollisionShape(level, below).max(Direction.Axis.Y) >= 1.0D;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(FACING))).cycle(HINGE);
    }
}
