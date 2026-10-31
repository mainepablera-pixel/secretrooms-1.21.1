package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Secret stairs with vanilla stair geometry and neighbor-dependent corner shapes. */
public class SecretStairs extends SecretBaseBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
    private static final int[] SHAPE_INDEX = {
            12, 5, 3, 10, 14, 13, 7, 11, 13, 7,
            11, 14, 8, 4, 1, 2, 4, 1, 2, 8
    };
    private static final VoxelShape[] VANILLA_TOP_SHAPES = StairShapeAccess.topShapes();
    private static final VoxelShape[] VANILLA_BOTTOM_SHAPES = StairShapeAccess.bottomShapes();

    public SecretStairs(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, Half.BOTTOM)
                .setValue(SHAPE, StairsShape.STRAIGHT)
                .setValue(SOLID, false)
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, SHAPE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return stairShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return stairShape(state);
    }

    private static VoxelShape stairShape(BlockState state) {
        int stateIndex = state.getValue(SHAPE).ordinal() * 4 + state.getValue(FACING).get2DDataValue();
        int shapeIndex = SHAPE_INDEX[stateIndex];
        return state.getValue(HALF) == Half.TOP ? VANILLA_TOP_SHAPES[shapeIndex] : VANILLA_BOTTOM_SHAPES[shapeIndex];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Half half = clickedFace == Direction.DOWN
                || (clickedFace != Direction.UP && context.getClickLocation().y - pos.getY() > 0.5)
                ? Half.TOP : Half.BOTTOM;
        BlockState state = defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(HALF, half);
        return state.setValue(SHAPE, getShapeProperty(state, context.getLevel(), pos));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis().isHorizontal()) {
            return state.setValue(SHAPE, getShapeProperty(state, level, pos));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    private static StairsShape getShapeProperty(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockState front = level.getBlockState(pos.relative(facing));
        if (isStair(front) && front.getValue(HALF) == state.getValue(HALF)) {
            Direction frontFacing = front.getValue(FACING);
            if (frontFacing.getAxis() != facing.getAxis()
                    && isDifferentStairs(state, level, pos, frontFacing.getOpposite())) {
                return frontFacing == facing.getCounterClockWise()
                        ? StairsShape.OUTER_LEFT : StairsShape.OUTER_RIGHT;
            }
        }

        BlockState back = level.getBlockState(pos.relative(facing.getOpposite()));
        if (isStair(back) && back.getValue(HALF) == state.getValue(HALF)) {
            Direction backFacing = back.getValue(FACING);
            if (backFacing.getAxis() != facing.getAxis()
                    && isDifferentStairs(state, level, pos, backFacing)) {
                return backFacing == facing.getCounterClockWise()
                        ? StairsShape.INNER_LEFT : StairsShape.INNER_RIGHT;
            }
        }
        return StairsShape.STRAIGHT;
    }

    private static boolean isDifferentStairs(BlockState state, BlockGetter level,
                                             BlockPos pos, Direction direction) {
        BlockState neighbor = level.getBlockState(pos.relative(direction));
        return !isStair(neighbor)
                || neighbor.getValue(FACING) != state.getValue(FACING)
                || neighbor.getValue(HALF) != state.getValue(HALF);
    }

    private static boolean isStair(BlockState state) {
        return state.getBlock() instanceof StairBlock || state.getBlock() instanceof SecretStairs;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        Direction direction = state.getValue(FACING);
        StairsShape shape = state.getValue(SHAPE);
        if (mirror == Mirror.LEFT_RIGHT && direction.getAxis() == Direction.Axis.Z
                || mirror == Mirror.FRONT_BACK && direction.getAxis() == Direction.Axis.X) {
            return switch (shape) {
                case INNER_LEFT -> state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                case INNER_RIGHT -> state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                case OUTER_LEFT -> state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                case OUTER_RIGHT -> state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                default -> state.rotate(Rotation.CLOCKWISE_180);
            };
        }
        return super.mirror(state, mirror);
    }

    /** Exposes vanilla's precomputed shape arrays without reimplementing geometry. */
    private static final class StairShapeAccess extends StairBlock {
        private StairShapeAccess() {
            super(Blocks.OAK_STAIRS.defaultBlockState(), BlockBehaviour.Properties.of());
        }

        private static VoxelShape[] topShapes() {
            return TOP_SHAPES;
        }

        private static VoxelShape[] bottomShapes() {
            return BOTTOM_SHAPES;
        }
    }
}
