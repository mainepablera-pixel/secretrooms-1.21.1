package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SecretTrapdoor extends SecretBaseBlock {

    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private final boolean iron;

    public SecretTrapdoor(BlockBehaviour.Properties properties) {
        this(properties, false);
    }

    public SecretTrapdoor(BlockBehaviour.Properties properties, boolean iron) {
        super(properties);
        this.iron = iron;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(HALF, Half.BOTTOM)
                .setValue(POWERED, false)
                .setValue(SOLID, false)
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, HALF, POWERED);
        super.createBlockStateDefinition(builder);
    }

    // Use the exact vanilla trapdoor state for both outline and collision
    // geometry so the panel edge follows the same facing/open/half mapping as
    // the baked model.
    private static BlockState vanillaTrapdoorState(BlockState state) {
        return Blocks.OAK_TRAPDOOR.defaultBlockState()
                .setValue(TrapDoorBlock.FACING, state.getValue(FACING))
                .setValue(TrapDoorBlock.OPEN, state.getValue(OPEN))
                .setValue(TrapDoorBlock.HALF, state.getValue(HALF))
                .setValue(TrapDoorBlock.POWERED, state.getValue(POWERED))
                .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return vanillaTrapdoorState(state).getShape(level, pos, ctx);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        return vanillaTrapdoorState(state).getCollisionShape(level, pos, context);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos,
                                     CollisionContext context) {
        return vanillaTrapdoorState(state).getVisualShape(level, pos, context);
    }

    private static void refreshModelData(Level level, BlockPos pos) {
        SecretBaseBlock.getTile(level, pos).ifPresent(te -> te.requestModelDataUpdate());
        if (!level.isClientSide) {
            BlockState current = level.getBlockState(pos);
            level.sendBlockUpdated(pos, current, current, 8);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hit) {
        if (this.iron) {
            return InteractionResult.PASS;
        }
        state = state.cycle(OPEN);
        level.setBlock(pos, state, 3);
        refreshModelData(level, pos);
        // Play the matching vanilla sound from the authoritative server so it
        // reaches nearby clients reliably, including the player using the trapdoor.
        if (!level.isClientSide) {
            level.playSound(null, pos, getTrapdoorSound(state.getValue(OPEN)), SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, state.getValue(OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            if (state.getValue(OPEN) != powered) {
                level.playSound(null, pos, getTrapdoorSound(powered), SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, powered ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }
            level.setBlock(pos, state.setValue(POWERED, powered).setValue(OPEN, powered), 3);
            refreshModelData(level, pos);
        }
    }

    private SoundEvent getTrapdoorSound(boolean opening) {
        if (iron) {
            return opening ? SoundEvents.IRON_TRAPDOOR_OPEN : SoundEvents.IRON_TRAPDOOR_CLOSE;
        }
        return opening ? SoundEvents.WOODEN_TRAPDOOR_OPEN : SoundEvents.WOODEN_TRAPDOOR_CLOSE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = this.defaultBlockState();
        Direction face = ctx.getClickedFace();
        if (!ctx.replacingClickedOnBlock() && face.getAxis().isHorizontal()) {
            state = state.setValue(FACING, face)
                         .setValue(HALF, ctx.getClickLocation().y - ctx.getClickedPos().getY() > 0.5 ? Half.TOP : Half.BOTTOM);
        } else {
            state = state.setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                         .setValue(HALF, face == Direction.UP ? Half.BOTTOM : Half.TOP);
        }
        if (ctx.getLevel().hasNeighborSignal(ctx.getClickedPos())) {
            state = state.setValue(OPEN, true).setValue(POWERED, true);
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
