package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

/**
 * Redstone-operated camouflage gate. When powered, it extends up to nine
 * camouflage dummy blocks in its facing direction and copies its mirror state
 * into them. The line is removed when power is lost or the gate is broken.
 */
public class SecretGateBlock extends SecretBaseBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final int MAX_LENGTH = 10;

    public SecretGateBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(OPEN, false)
                .setValue(SOLID, false)
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, OPEN);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
                                BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;
        boolean powered = level.hasNeighborSignal(pos);
        if (powered == state.getValue(POWERED)) return;

        if (powered) {
            buildGate(level, pos, state);
        } else {
            destroyGate(level, pos, state, true);
        }
        level.setBlock(pos, state.setValue(POWERED, powered).setValue(OPEN, powered), 3);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && state.getValue(POWERED)) {
            buildGate(level, pos, state);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(OPEN)) buildGate(level, pos, state);
        else destroyGate(level, pos, state, true);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                         BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide) {
            destroyGate(level, pos, state, false);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private void buildGate(Level level, BlockPos pos, BlockState gateState) {
        BlockState mirror = getMirrorState(level, pos).orElse(Blocks.STONE.defaultBlockState());
        Direction direction = gateState.getValue(FACING);
        Block dummyBlock = SecretBlocks.SECRET_DUMMY_BLOCK.get();

        for (int distance = 1; distance <= MAX_LENGTH; distance++) {
            BlockPos targetPos = pos.relative(direction, distance);
            BlockState target = level.getBlockState(targetPos);

            // Only extend into air (or an existing dummy segment). General
            // replaceability includes grass, flowers, snow and other real
            // blocks; overwriting those and later replacing them with air would
            // permanently destroy world content when the gate retracts.
            if (!target.isAir() && !target.is(dummyBlock)) {
                if (target.is(this)) {
                    level.setBlock(targetPos, target.setValue(OPEN, true), 3);
                    level.scheduleTick(targetPos, this, 3);
                }
                // If an obstruction appeared after the gate extended, remove
                // only this gate's stale segments beyond it. Never touch
                // segments owned by another gate or unowned legacy segments.
                cleanupOwnedSegments(level, pos, direction, distance + 1, dummyBlock);
                return;
            }
            if (target.is(dummyBlock)) {
                BlockPos owner = SecretBaseBlock.getTile(level, targetPos)
                        .map(com.wynprice.secretrooms.server.tileentity.SecretTileEntity::getGateOwner)
                        .orElse(null);
                // A dummy segment without ownership metadata may belong to an
                // older save or another system. Never claim it implicitly.
                if (owner == null || !owner.equals(pos)) {
                    cleanupOwnedSegments(level, pos, direction, distance + 1, dummyBlock);
                    return;
                }
            }
            if (distance == MAX_LENGTH) return;

            // Gate blocks touching the side of this line participate in the
            // same extension, matching the original cross-gate behavior.
            for (Direction side : Direction.values()) {
                if (side.getAxis() == direction.getAxis()) continue;
                BlockPos sidePos = targetPos.relative(side);
                BlockState sideState = level.getBlockState(sidePos);
                if (sideState.is(this) && !sideState.getValue(OPEN)) {
                    level.setBlock(sidePos, sideState.setValue(OPEN, true), 3);
                    level.scheduleTick(sidePos, this, 3);
                }
            }

            if (!target.is(dummyBlock)) {
                level.setBlock(targetPos, dummyBlock.defaultBlockState(), 3);
            }
            getTile(level, targetPos).ifPresent(tile -> {
                tile.setMirrorState(mirror);
                tile.setGateOwner(pos);
            });
        }
    }

    private void destroyGate(Level level, BlockPos pos, BlockState gateState, boolean recursive) {
        Direction direction = gateState.getValue(FACING);
        Block dummyBlock = SecretBlocks.SECRET_DUMMY_BLOCK.get();

        // Scan the entire short gate range instead of stopping at the first
        // obstruction. If a block was placed through an extended gate, owned
        // segments farther down the line must still be removed. Ownership is
        // checked for every segment so adjacent/overlapping gates are protected.
        for (int distance = 1; distance <= MAX_LENGTH; distance++) {
            BlockPos targetPos = pos.relative(direction, distance);
            BlockState target = level.getBlockState(targetPos);
            if (target.is(dummyBlock)) {
                BlockPos owner = SecretBaseBlock.getTile(level, targetPos)
                        .map(com.wynprice.secretrooms.server.tileentity.SecretTileEntity::getGateOwner)
                        .orElse(null);
                if (pos.equals(owner)) {
                    level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
                }
                continue;
            }

            if (recursive && target.is(this) && target.getValue(OPEN)) {
                level.setBlock(targetPos, target.setValue(OPEN, false), 3);
                level.scheduleTick(targetPos, this, 3);
            }

            if (recursive) {
                for (Direction side : Direction.values()) {
                    if (side.getAxis() == direction.getAxis()) continue;
                    BlockPos sidePos = targetPos.relative(side);
                    BlockState sideState = level.getBlockState(sidePos);
                    if (sideState.is(this) && sideState.getValue(OPEN)) {
                        level.setBlock(sidePos, sideState.setValue(OPEN, false), 3);
                        level.scheduleTick(sidePos, this, 3);
                    }
                }
            }
        }
    }

    private void cleanupOwnedSegments(Level level, BlockPos ownerPos, Direction direction,
                                      int startDistance, Block dummyBlock) {
        for (int distance = startDistance; distance <= MAX_LENGTH; distance++) {
            BlockPos segmentPos = ownerPos.relative(direction, distance);
            if (!level.getBlockState(segmentPos).is(dummyBlock)) continue;
            BlockPos recordedOwner = SecretBaseBlock.getTile(level, segmentPos)
                    .map(com.wynprice.secretrooms.server.tileentity.SecretTileEntity::getGateOwner)
                    .orElse(null);
            if (ownerPos.equals(recordedOwner)) {
                level.setBlock(segmentPos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        boolean powered = ctx.getLevel().hasNeighborSignal(ctx.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, ctx.getClickedFace())
                .setValue(POWERED, powered)
                .setValue(OPEN, powered);
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
