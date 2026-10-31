package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractSecretPressurePlateBase extends SecretBaseBlock {
   public AbstractSecretPressurePlateBase(BlockBehaviour.Properties properties) {
      super(properties);
   }

   public VoxelShape m_5939_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return super.m_5939_(state, worldIn, pos, context);
   }

   public int tickRate(LevelReader worldIn) {
      return 20;
   }

   public void m_213897_(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
      if (!worldIn.f_46443_) {
         int i = this.getRedstoneStrength(state);
         if (i > 0) {
            this.updateState(worldIn, pos, state, i);
         }
      }

   }

   public void m_141947_(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
      if (!worldIn.f_46443_) {
         int i = this.getRedstoneStrength(state);
         this.updateState(worldIn, pos, state, i);
      }

   }

   protected void updateState(Level worldIn, BlockPos pos, BlockState state, int oldRedstoneStrength) {
      int i = this.computeRedstoneStrength(worldIn, pos);
      boolean flag = oldRedstoneStrength > 0;
      boolean flag1 = i > 0;
      if (oldRedstoneStrength != i) {
         BlockState blockstate = this.setRedstoneStrength(state, i);
         worldIn.m_7731_(pos, blockstate, 2);
         this.updateNeighbors(worldIn, pos);
         worldIn.m_6550_(pos, state, blockstate);
      }

      if (!flag1 && flag) {
         this.playClickOffSound(worldIn, pos);
      } else if (flag1 && !flag) {
         this.playClickOnSound(worldIn, pos);
      }

      if (flag1) {
         worldIn.m_186460_(new BlockPos(pos), this, this.tickRate(worldIn));
      }

   }

   public int m_6378_(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
      return this.getRedstoneStrength(blockState);
   }

   public void m_6810_(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!isMoving && state.m_60734_() != newState.m_60734_()) {
         if (this.getRedstoneStrength(state) > 0) {
            this.updateNeighbors(worldIn, pos);
         }

         super.m_6810_(state, worldIn, pos, newState, isMoving);
      }

   }

   private void updateNeighbors(Level worldIn, BlockPos pos) {
      worldIn.m_46672_(pos, this);
      worldIn.m_46672_(pos.m_7495_(), this);
   }

   public boolean m_7899_(BlockState state) {
      return true;
   }

   public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
      return this.getRedstoneStrength(state) == 0;
   }

   protected abstract void playClickOnSound(LevelAccessor var1, BlockPos var2);

   protected abstract void playClickOffSound(LevelAccessor var1, BlockPos var2);

   protected abstract int computeRedstoneStrength(Level var1, BlockPos var2);

   protected abstract int getRedstoneStrength(BlockState var1);

   protected abstract BlockState setRedstoneStrength(BlockState var1, int var2);
}
