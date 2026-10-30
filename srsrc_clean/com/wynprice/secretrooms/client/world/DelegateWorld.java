package com.wynprice.secretrooms.client.world;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class DelegateWorld implements BlockAndTintGetter {
   private static final List<DelegateWorld> AVAILABLE = new ArrayList();
   private BlockAndTintGetter reader;
   private BlockGetter world;

   public static DelegateWorld getPooled(BlockGetter reader) {
      synchronized(AVAILABLE) {
         if (AVAILABLE.isEmpty()) {
            return new DelegateWorld(reader);
         } else {
            DelegateWorld world = (DelegateWorld)AVAILABLE.remove(0);
            world.use(reader);
            return world;
         }
      }
   }

   public static <T> Function<BlockState, T> createFunction(BlockGetter reader, BiFunction<DelegateWorld, BlockState, T> func) {
      return (mirror) -> {
         DelegateWorld pooled = getPooled(reader);
         T ret = (T)func.apply(pooled, mirror);
         pooled.release();
         return ret;
      };
   }

   public DelegateWorld(BlockGetter world) {
      this.use(world);
   }

   private void use(BlockGetter world) {
      this.world = world;
      if (this.world instanceof BlockAndTintGetter) {
         this.reader = (BlockAndTintGetter)this.world;
      } else {
         this.reader = null;
      }

   }

   public void release() {
      synchronized(AVAILABLE) {
         AVAILABLE.add(this);
         this.world = null;
         this.reader = null;
      }
   }

   @Nullable
   public BlockEntity m_7702_(BlockPos pos) {
      return (BlockEntity)SecretBaseBlock.getMirrorData(this.world, pos).map(SecretData::getTileEntityCache).orElseGet(() -> this.world.m_7702_(pos));
   }

   public BlockState m_8055_(BlockPos pos) {
      return (BlockState)SecretBaseBlock.getMirrorState(this.world, pos).orElseGet(() -> this.world.m_8055_(pos));
   }

   public FluidState m_6425_(BlockPos pos) {
      return this.world.m_6425_(pos);
   }

   public float m_7717_(Direction p_230487_1_, boolean p_230487_2_) {
      return 0.0F;
   }

   public LevelLightEngine m_5518_() {
      if (this.reader != null) {
         return this.reader.m_5518_();
      } else {
         throw new IllegalStateException("Invalid Call on Delegate World, resulting in a Invalid State. WORLD: " + this.world.getClass());
      }
   }

   public int m_6171_(BlockPos blockPosIn, ColorResolver colorResolverIn) {
      return this.reader == null ? 0 : this.reader.m_6171_(blockPosIn, colorResolverIn);
   }

   public int m_141928_() {
      return this.world.m_141928_();
   }

   public int m_141937_() {
      return this.world.m_141937_();
   }
}
