package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public abstract class AbstractSecretPressurePlateBase extends SecretBaseBlock {

    // Pressure plates must retain vanilla plate geometry even when their visual
    // camouflage is a full cube. Delegating collision to the mirrored block
    // prevents entities from reaching the activation volume and can block movement.
    private static final VoxelShape PLATE_SHAPE = Block.box(1, 0, 1, 15, 1, 15);
    private static final VoxelShape PRESSED_SHAPE = Block.box(1, 0, 1, 15, 0.5, 15);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public AbstractSecretPressurePlateBase(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POWERED, false)
                .setValue(SOLID, false)
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        super.createBlockStateDefinition(builder);
    }

    protected abstract int getSignalStrength(Level level, BlockPos pos);

    protected abstract void playOnSound(Level level, BlockPos pos);

    protected abstract void playOffSound(Level level, BlockPos pos);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(POWERED) ? PRESSED_SHAPE : PLATE_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(POWERED) ? PRESSED_SHAPE : PLATE_SHAPE;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide) {
            int strength = this.getSignalForState(state);
            if (strength == 0) {
                this.checkPressed(level, pos, state, strength);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int strength = this.getSignalForState(state);
        if (strength > 0) {
            this.checkPressed(level, pos, state, strength);
        }
    }

    protected void checkPressed(Level level, BlockPos pos, BlockState state, int currentStrength) {
        int newStrength = this.getSignalStrength(level, pos);
        boolean wasPowered = currentStrength > 0;
        boolean isPowered = newStrength > 0;

        if (currentStrength != newStrength) {
            state = this.setSignalForState(state, newStrength);
            level.setBlock(pos, state, 3);
            this.updateNeighbours(level, pos);
            if (!isPowered && wasPowered) {
                this.playOffSound(level, pos);
                level.gameEvent(null, GameEvent.BLOCK_DEACTIVATE, pos);
            } else if (isPowered && !wasPowered) {
                this.playOnSound(level, pos);
                level.gameEvent(null, GameEvent.BLOCK_ACTIVATE, pos);
            }
        }

        if (isPowered) {
            level.scheduleTick(new BlockPos(pos), this, 20);
        }
    }

    protected int getSignalForState(BlockState state) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    protected BlockState setSignalForState(BlockState state, int strength) {
        return state.setValue(POWERED, strength > 0);
    }

    private void updateNeighbours(Level level, BlockPos pos) {
        // Notify the block above the plate and the supporting block below it;
        // this mirrors vanilla pressure-plate redstone propagation.
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.below(), this);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                         BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && state.getValue(POWERED)) {
            this.updateNeighbours(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return this.getSignalForState(state);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return side == Direction.UP ? this.getSignalForState(state) : 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    protected static AABB getTouchAABB(BlockPos pos) {
        return new AABB(pos.getX() + 0.125, pos.getY(), pos.getZ() + 0.125,
                pos.getX() + 0.875, pos.getY() + 0.25, pos.getZ() + 0.875);
    }
}
