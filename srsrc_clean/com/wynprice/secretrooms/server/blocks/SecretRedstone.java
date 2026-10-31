package com.wynprice.secretrooms.server.blocks;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class SecretRedstone extends SecretBaseBlock {
   private static final IntegerProperty POWER;
   private boolean blockedPower = false;
   private final Set<BlockPos> toUpdate = Sets.newHashSet();

   public SecretRedstone(BlockBehaviour.Properties properties) {
      super(properties);
   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{POWER});
   }

   private BlockState updateSurroundingRedstone(Level worldIn, BlockPos pos, BlockState state) {
      state = this.updateNeighbourRedone(worldIn, pos, state);
      List<BlockPos> list = Lists.newArrayList(this.toUpdate);
      this.toUpdate.clear();

      for(BlockPos blockpos : list) {
         worldIn.m_46672_(blockpos, this);
      }

      return state;
   }

   private BlockState updateNeighbourRedone(Level world, BlockPos pos, BlockState state) {
      BlockState blockstate = state;
      int power = (Integer)state.m_61143_(POWER);
      this.blockedPower = true;
      int neighborPower = world.m_46755_(pos);
      this.blockedPower = false;
      int maxPower = 0;
      if (neighborPower < 15) {
         for(Direction direction : Direction.values()) {
            maxPower = this.maxSignal(maxPower, world.m_8055_(pos.m_121945_(direction)));
         }
      }

      int stateMaxPower = maxPower - 1;
      if (neighborPower > stateMaxPower) {
         stateMaxPower = neighborPower;
      }

      if (power != stateMaxPower) {
         state = (BlockState)state.m_61124_(POWER, stateMaxPower);
         if (world.m_8055_(pos) == blockstate) {
            world.m_7731_(pos, state, 2);
         }

         this.toUpdate.add(pos);

         for(Direction direction : Direction.values()) {
            this.toUpdate.add(pos.m_121945_(direction));
         }
      }

      return state;
   }

   private void notifyWireNeighborsOfStateChange(Level worldIn, BlockPos pos) {
      if (worldIn.m_8055_(pos).m_60734_() == this) {
         worldIn.m_46672_(pos, this);

         for(Direction direction : Direction.values()) {
            worldIn.m_46672_(pos.m_121945_(direction), this);
         }
      }

   }

   public void m_6807_(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (oldState.m_60734_() != state.m_60734_() && !worldIn.f_46443_) {
         this.updateSurroundingRedstone(worldIn, pos, state);

         for(Direction direction : Plane.VERTICAL) {
            worldIn.m_46672_(pos.m_121945_(direction), this);
         }

         for(Direction direction1 : Plane.HORIZONTAL) {
            this.notifyWireNeighborsOfStateChange(worldIn, pos.m_121945_(direction1));
         }

         for(Direction direction2 : Plane.HORIZONTAL) {
            BlockPos blockpos = pos.m_121945_(direction2);
            if (worldIn.m_8055_(blockpos).m_60796_(worldIn, blockpos)) {
               this.notifyWireNeighborsOfStateChange(worldIn, blockpos.m_7494_());
            } else {
               this.notifyWireNeighborsOfStateChange(worldIn, blockpos.m_7495_());
            }
         }
      }

   }

   public void m_6810_(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!isMoving && state.m_60734_() != newState.m_60734_()) {
         super.m_6810_(state, worldIn, pos, newState, false);
         if (!worldIn.f_46443_) {
            for(Direction direction : Direction.values()) {
               worldIn.m_46672_(pos.m_121945_(direction), this);
            }

            this.updateSurroundingRedstone(worldIn, pos, state);

            for(Direction direction : Direction.values()) {
               this.notifyWireNeighborsOfStateChange(worldIn, pos.m_121945_(direction));
            }
         }
      }

   }

   private int maxSignal(int existingSignal, BlockState neighbor) {
      if (neighbor.m_60734_() != this && neighbor.m_60734_() != Blocks.f_50088_) {
         return existingSignal;
      } else {
         int i = (Integer)neighbor.m_61143_(POWER);
         return i > existingSignal ? i : existingSignal;
      }
   }

   public void m_6861_(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (!worldIn.f_46443_) {
         if (state.m_60710_(worldIn, pos)) {
            this.updateSurroundingRedstone(worldIn, pos, state);
         } else {
            m_49950_(state, worldIn, pos);
            worldIn.m_7471_(pos, false);
         }
      }

   }

   public int m_6378_(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
      return this.blockedPower ? 0 : (Integer)blockState.m_61143_(POWER);
   }

   public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
      return false;
   }

   public boolean m_7899_(BlockState state) {
      return !this.blockedPower;
   }

   static {
      POWER = BlockStateProperties.f_61426_;
   }
}
