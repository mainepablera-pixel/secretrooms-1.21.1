package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SecretStairs extends SecretBaseBlock {
   public static final DirectionProperty FACING;
   public static final EnumProperty<Half> HALF;
   public static final EnumProperty<StairsShape> SHAPE;
   protected static final VoxelShape[] SLAB_TOP_SHAPES;
   protected static final VoxelShape[] SLAB_BOTTOM_SHAPES;
   private static final int[] idsToShape;

   public SecretStairs(BlockBehaviour.Properties properties) {
      super(properties);
   }

   public boolean m_7923_(BlockState state) {
      return true;
   }

   public VoxelShape m_5939_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return (state.m_61143_(HALF) == Half.TOP ? SLAB_TOP_SHAPES : SLAB_BOTTOM_SHAPES)[idsToShape[this.stateToId(state)]];
   }

   private int stateToId(BlockState state) {
      return ((StairsShape)state.m_61143_(SHAPE)).ordinal() * 4 + ((Direction)state.m_61143_(FACING)).m_122416_();
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      Direction direction = context.m_43719_();
      BlockPos pos = context.m_8083_();
      BlockState state = (BlockState)((BlockState)this.m_49966_().m_61124_(FACING, context.m_8125_())).m_61124_(HALF, direction == Direction.DOWN || direction != Direction.UP && !(context.m_43720_().f_82480_ - (double)pos.m_123342_() <= (double)0.5F) ? Half.TOP : Half.BOTTOM);
      return (BlockState)state.m_61124_(SHAPE, getShapeProperty(state, context.m_43725_(), pos));
   }

   public BlockState m_7417_(BlockState state, Direction direction, BlockState fromState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
      return direction.m_122434_().m_122479_() ? (BlockState)state.m_61124_(SHAPE, getShapeProperty(state, world, pos)) : super.m_7417_(state, direction, fromState, world, pos, posFrom);
   }

   private static StairsShape getShapeProperty(BlockState state, BlockGetter world, BlockPos position) {
      Direction direction = (Direction)state.m_61143_(FACING);
      BlockState oppositeState = world.m_8055_(position.m_121945_(direction));
      if (isBlockStairs(oppositeState) && state.m_61143_(HALF) == oppositeState.m_61143_(HALF)) {
         Direction oppositeDirection = (Direction)oppositeState.m_61143_(FACING);
         if (oppositeDirection.m_122434_() != ((Direction)state.m_61143_(FACING)).m_122434_() && isDifferentStairs(state, world, position, oppositeDirection.m_122424_())) {
            if (oppositeDirection == direction.m_122428_()) {
               return StairsShape.OUTER_LEFT;
            }

            return StairsShape.OUTER_RIGHT;
         }
      }

      BlockState othersideState = world.m_8055_(position.m_121945_(direction.m_122424_()));
      if (isBlockStairs(othersideState) && state.m_61143_(HALF) == othersideState.m_61143_(HALF)) {
         Direction othersideDirection = (Direction)othersideState.m_61143_(FACING);
         if (othersideDirection.m_122434_() != ((Direction)state.m_61143_(FACING)).m_122434_() && isDifferentStairs(state, world, position, othersideDirection)) {
            if (othersideDirection == direction.m_122428_()) {
               return StairsShape.INNER_LEFT;
            }

            return StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean isDifferentStairs(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
      BlockState offsetState = world.m_8055_(pos.m_121945_(direction));
      return !isBlockStairs(offsetState) || offsetState.m_61143_(FACING) != state.m_61143_(FACING) || offsetState.m_61143_(HALF) != state.m_61143_(HALF);
   }

   public static boolean isBlockStairs(BlockState state) {
      return state.m_60734_() instanceof StairBlock || state.m_60734_() instanceof SecretStairs;
   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{FACING, HALF, SHAPE});
   }

   public boolean m_7357_(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
      return false;
   }

   static {
      FACING = SecretStairs.StairsBlockAccess.f_56841_;
      HALF = SecretStairs.StairsBlockAccess.f_56842_;
      SHAPE = BlockStateProperties.f_61398_;
      SLAB_TOP_SHAPES = SecretStairs.StairsBlockAccess.SLAB_TOP_SHAPES;
      SLAB_BOTTOM_SHAPES = SecretStairs.StairsBlockAccess.SLAB_BOTTOM_SHAPES;
      idsToShape = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
   }

   private static class StairsBlockAccess extends StairBlock {
      protected static final VoxelShape[] SLAB_TOP_SHAPES;
      protected static final VoxelShape[] SLAB_BOTTOM_SHAPES;

      protected StairsBlockAccess(BlockState p_i48321_1_, BlockBehaviour.Properties p_i48321_2_) {
         super(p_i48321_1_, p_i48321_2_);
      }

      static {
         SLAB_TOP_SHAPES = StairBlock.f_56855_;
         SLAB_BOTTOM_SHAPES = StairBlock.f_56856_;
      }
   }
}
