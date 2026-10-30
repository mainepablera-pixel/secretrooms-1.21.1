package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SecretButton extends SecretBaseBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final net.minecraft.world.level.block.state.properties.DirectionProperty FACING = BlockStateProperties.FACING;

    private final boolean wooden;

    public SecretButton(BlockBehaviour.Properties properties, boolean wooden) {
        super(properties);
        this.wooden = wooden;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POWERED, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(SOLID, false)
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    private int getPressDuration() {
        return this.wooden ? 30 : 20;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hit) {
        if (state.getValue(POWERED)) {
            return InteractionResult.CONSUME;
        }
        this.press(state, level, pos, player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void press(BlockState state, Level level, BlockPos pos, @Nullable Player player) {
        level.setBlock(pos, state.setValue(POWERED, true), 3);
        this.updateNeighbours(state, level, pos);
        level.scheduleTick(pos, this, this.getPressDuration());
        // A null source broadcasts to the activating player as well as nearby
        // clients. Passing the player as source can exclude that player from
        // the server sound packet, while the client-side call is suppressed.
        if (!level.isClientSide) {
            level.playSound(null, pos, this.wooden ? SoundEvents.WOODEN_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_ON,
                    SoundSource.BLOCKS, 0.3F, 0.6F);
            level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(POWERED)) return;

        // Wooden buttons remain powered while an arrow is still embedded in
        // their activation volume, matching vanilla wooden-button behavior.
        if (this.wooden && this.hasArrow(level, state, pos)) {
            level.scheduleTick(pos, this, 10);
            return;
        }
        this.unpress(state, level, pos);
    }

    private void unpress(BlockState state, Level level, BlockPos pos) {
        level.setBlock(pos, state.setValue(POWERED, false), 3);
        this.updateNeighbours(state, level, pos);
        if (!level.isClientSide) {
            level.playSound(null, pos, this.wooden ? SoundEvents.WOODEN_BUTTON_CLICK_OFF : SoundEvents.STONE_BUTTON_CLICK_OFF,
                    SoundSource.BLOCKS, 0.3F, 0.5F);
            level.gameEvent(null, GameEvent.BLOCK_DEACTIVATE, pos);
        }
    }

    private void updateNeighbours(BlockState state, Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        if (!level.isClientSide && this.wooden && !state.getValue(POWERED)) {
            this.checkPressed(state, level, pos);
        }
    }

    private void checkPressed(BlockState state, Level level, BlockPos pos) {
        if (this.hasArrow(level, state, pos)) {
            this.press(state, level, pos, null);
        }
    }

    private boolean hasArrow(Level level, BlockState state, BlockPos pos) {
        return !level.getEntitiesOfClass(AbstractArrow.class,
                state.getShape(level, pos).bounds().move(pos)).isEmpty();
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return state.getValue(POWERED) && side == state.getValue(FACING) ? 15 : 0;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                         BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && state.getValue(POWERED)) {
            this.updateNeighbours(state, level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockState rotate(BlockState state, net.minecraft.world.level.block.Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, net.minecraft.world.level.block.Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }
}
