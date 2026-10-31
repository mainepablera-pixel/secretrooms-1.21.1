package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Redstone-wire-like secret block that carries and propagates a 0–15 signal. */
public class SecretRedstone extends SecretBaseBlock {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    private boolean suppressPowerRead;

    public SecretRedstone(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWER);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos,
                        BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && !oldState.is(state.getBlock())) {
            updateSurrounding(level, pos, state);
            notifyAround(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                         BlockState newState, boolean movedByPiston) {
        if (!movedByPiston && !state.is(newState.getBlock()) && !level.isClientSide) {
            super.onRemove(state, level, pos, newState, false);
            notifyAround(level, pos);
            updateSurrounding(level, pos, state);
            for (Direction direction : Direction.values()) {
                level.updateNeighborsAt(pos.relative(direction), this);
            }
            return;
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
                                BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        updateSurrounding(level, pos, state);
    }

    private void updateSurrounding(Level level, BlockPos pos, BlockState state) {
        int power = state.getValue(POWER);
        suppressPowerRead = true;
        int externalPower = level.getBestNeighborSignal(pos);
        suppressPowerRead = false;

        int adjacentWirePower = 0;
        if (externalPower < 15) {
            for (Direction direction : Direction.values()) {
                BlockState neighbor = level.getBlockState(pos.relative(direction));
                if (neighbor.is(this) || neighbor.is(Blocks.REDSTONE_WIRE)) {
                    adjacentWirePower = Math.max(adjacentWirePower, neighbor.getValue(POWER));
                }
            }
        }

        int nextPower = Math.max(externalPower, adjacentWirePower - 1);
        nextPower = Math.max(0, Math.min(15, nextPower));
        if (power != nextPower) {
            BlockState updated = state.setValue(POWER, nextPower);
            if (level.getBlockState(pos).getBlock() == this) {
                level.setBlock(pos, updated, 2);
            }
            notifyAround(level, pos);
        }
    }

    private void notifyAround(Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        for (Direction direction : Direction.values()) {
            level.updateNeighborsAt(pos.relative(direction), this);
        }
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return suppressPowerRead ? 0 : state.getValue(POWER);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return suppressPowerRead ? 0 : state.getValue(POWER);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return !suppressPowerRead;
    }
}
