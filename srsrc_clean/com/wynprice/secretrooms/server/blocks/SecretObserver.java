package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class SecretObserver extends SecretBaseBlock {
   public static final BooleanProperty POWERED;
   public static final DirectionProperty FACING;

   public SecretObserver(BlockBehaviour.Properties properties) {
      super(properties);
      this.m_49959_((BlockState)((BlockState)this.m_49966_().m_61124_(FACING, Direction.SOUTH)).m_61124_(POWERED, Boolean.FALSE));
   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{FACING, POWERED});
   }

   public void m_213897_(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
      if ((Boolean)state.m_61143_(POWERED)) {
         worldIn.m_7731_(pos, (BlockState)state.m_61124_(POWERED, false), 2);
      } else {
         worldIn.m_7731_(pos, (BlockState)state.m_61124_(POWERED, true), 2);
         worldIn.m_186460_(pos, this, 2);
      }

      this.updateNeighborsInFront(worldIn, pos, state);
   }

   public BlockState m_7417_(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.m_61143_(FACING) == facing && !(Boolean)stateIn.m_61143_(POWERED)) {
         this.startSignal(worldIn, currentPos);
      }

      return super.m_7417_(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   private void startSignal(LevelAccessor worldIn, BlockPos pos) {
      if (!worldIn.m_5776_() && !worldIn.m_183326_().m_183582_(pos, this)) {
         worldIn.m_186460_(pos, this, 2);
      }

   }

   private void updateNeighborsInFront(Level worldIn, BlockPos pos, BlockState state) {
      Direction direction = (Direction)state.m_61143_(FACING);
      BlockPos blockpos = pos.m_121945_(direction.m_122424_());
      worldIn.m_46586_(blockpos, this, pos);
      worldIn.m_46590_(blockpos, this, direction);
   }

   public boolean m_7899_(BlockState state) {
      return true;
   }

   public int m_6376_(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
      return blockState.m_60746_(blockAccess, pos, side);
   }

   public int m_6378_(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
      return (Boolean)blockState.m_61143_(POWERED) && blockState.m_61143_(FACING) == side ? 15 : 0;
   }

   public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
      return false;
   }

   public void m_6807_(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (state.m_60734_() != oldState.m_60734_() && !worldIn.m_5776_() && (Boolean)state.m_61143_(POWERED) && !worldIn.m_183326_().m_183582_(pos, this)) {
         BlockState blockstate = (BlockState)state.m_61124_(POWERED, false);
         worldIn.m_7731_(pos, blockstate, 18);
         this.updateNeighborsInFront(worldIn, pos, blockstate);
      }

   }

   public void m_6810_(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.m_60734_() != newState.m_60734_() && !worldIn.f_46443_ && (Boolean)state.m_61143_(POWERED) && worldIn.m_183326_().m_183582_(pos, this)) {
         this.updateNeighborsInFront(worldIn, pos, (BlockState)state.m_61124_(POWERED, false));
      }

   }

   public BlockState m_5573_(BlockPlaceContext context) {
      return (BlockState)this.m_49966_().m_61124_(FACING, context.m_7820_().m_122424_().m_122424_());
   }

   static {
      POWERED = BlockStateProperties.f_61448_;
      FACING = BlockStateProperties.f_61372_;
   }
}
