package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * One-way glass: sides can be toggled by right-clicking with an empty hand.
 * Open sides show the mirrored block; closed sides act more like solid glass.
 */
public class OneWayGlass extends SecretBaseBlock {

    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST  = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST  = PipeBlock.WEST;
    public static final BooleanProperty UP    = PipeBlock.UP;
    public static final BooleanProperty DOWN  = PipeBlock.DOWN;

    public OneWayGlass(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, true)
                .setValue(EAST, true)
                .setValue(SOUTH, true)
                .setValue(WEST, true)
                .setValue(UP, true)
                .setValue(DOWN, true)
                .setValue(SOLID, false)
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
        super.createBlockStateDefinition(builder);
    }

    // Intentionally no click handler: the copied face chosen at placement is
    // constant and is never toggled, moved or hidden by clicking. Clicks fall
    // through to SecretBaseBlock's default behaviour.

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;

        // The copied face must face the player placing the block, not the
        // face of the support block that was clicked. BlockPlaceContext's
        // nearest looking direction points from the player into the world;
        // reverse it to get the outward-facing side toward the player.
        Direction placerSide = context.getNearestLookingDirection().getOpposite();
        BooleanProperty prop = PipeBlock.PROPERTY_BY_DIRECTION.get(placerSide);
        if (prop != null) {
            state = state.setValue(prop, false);
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        BlockEntity be = level.getBlockEntity(currentPos);
        if (be != null && level.isClientSide()) {
            be.requestModelDataUpdate();
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    /**
     * Rotate the six directional face flags without calling PipeBlock's
     * protected helper methods (which are not accessible from this subclass).
     */
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        BlockState result = state;
        for (Direction source : Direction.values()) {
            Direction target = rotation.rotate(source);
            BooleanProperty sourceProperty = PipeBlock.PROPERTY_BY_DIRECTION.get(source);
            BooleanProperty targetProperty = PipeBlock.PROPERTY_BY_DIRECTION.get(target);
            if (sourceProperty != null && targetProperty != null) {
                result = result.setValue(targetProperty, state.getValue(sourceProperty));
            }
        }
        return result;
    }

    /**
     * Mirror the six directional face flags using the public direction
     * transform, preserving each face's value on its transformed direction.
     */
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        BlockState result = state;
        for (Direction source : Direction.values()) {
            Direction target = mirror.mirror(source);
            BooleanProperty sourceProperty = PipeBlock.PROPERTY_BY_DIRECTION.get(source);
            BooleanProperty targetProperty = PipeBlock.PROPERTY_BY_DIRECTION.get(target);
            if (sourceProperty != null && targetProperty != null) {
                result = result.setValue(targetProperty, state.getValue(sourceProperty));
            }
        }
        return result;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }
}
