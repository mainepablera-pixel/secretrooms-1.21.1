package com.wynprice.secretrooms.server.blocks;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class SecretButton extends SecretBaseBlock {
   public static final BooleanProperty POWERED;
   private final boolean wooden;

   public SecretButton(BlockBehaviour.Properties properties, boolean wooden) {
      super(properties);
      this.wooden = wooden;
      this.m_49959_((BlockState)this.m_49966_().m_61124_(POWERED, false));
   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{POWERED});
   }

   public int tickRate(LevelReader worldIn) {
      return this.wooden ? 30 : 20;
   }

   public InteractionResult m_6227_(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if ((Boolean)state.m_61143_(POWERED)) {
         return InteractionResult.SUCCESS;
      } else {
         worldIn.m_7731_(pos, (BlockState)state.m_61124_(POWERED, Boolean.TRUE), 3);
         this.playSound(player, worldIn, pos, true);
         this.updateNeighbors(worldIn, pos);
         worldIn.m_186460_(pos, this, this.tickRate(worldIn));
         return InteractionResult.SUCCESS;
      }
   }

   private void playSound(@Nullable Player player, LevelAccessor world, BlockPos pos, boolean turnOn) {
      SoundEvent woodenEvent = turnOn ? SoundEvents.f_12632_ : SoundEvents.f_12631_;
      SoundEvent stoneEvent = turnOn ? SoundEvents.f_12444_ : SoundEvents.f_12443_;
      world.m_5594_(turnOn ? player : null, pos, this.wooden ? woodenEvent : stoneEvent, SoundSource.BLOCKS, 0.3F, turnOn ? 0.6F : 0.5F);
   }

   public void m_6810_(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!isMoving && state.m_60734_() != newState.m_60734_()) {
         if ((Boolean)state.m_61143_(POWERED)) {
            this.updateNeighbors(worldIn, pos);
         }

         super.m_6810_(state, worldIn, pos, newState, isMoving);
      }

   }

   public int m_6378_(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
      return (Boolean)blockState.m_61143_(POWERED) ? 15 : 0;
   }

   public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
      return !(Boolean)state.m_61143_(POWERED);
   }

   public boolean m_7899_(BlockState state) {
      return true;
   }

   public void m_213897_(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
      if (!worldIn.f_46443_ && (Boolean)state.m_61143_(POWERED)) {
         if (this.wooden) {
            this.checkPressed(state, worldIn, pos);
         } else {
            worldIn.m_7731_(pos, (BlockState)state.m_61124_(POWERED, Boolean.FALSE), 3);
            this.updateNeighbors(worldIn, pos);
            this.playSound((Player)null, worldIn, pos, false);
         }
      }

   }

   public void m_7892_(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
      if (!worldIn.f_46443_ && this.wooden && !(Boolean)state.m_61143_(POWERED)) {
         this.checkPressed(state, worldIn, pos);
      }

   }

   private void checkPressed(BlockState state, Level worldIn, BlockPos pos) {
      List<? extends Entity> list = worldIn.m_45976_(AbstractArrow.class, state.m_60808_(worldIn, pos).m_83215_().m_82338_(pos));
      boolean flag = !list.isEmpty();
      boolean flag1 = (Boolean)state.m_61143_(POWERED);
      if (flag != flag1) {
         worldIn.m_7731_(pos, (BlockState)state.m_61124_(POWERED, flag), 3);
         this.updateNeighbors(worldIn, pos);
         this.playSound((Player)null, worldIn, pos, flag);
      }

      if (flag) {
         worldIn.m_186460_(new BlockPos(pos), this, this.tickRate(worldIn));
      }

   }

   private void updateNeighbors(Level world, BlockPos pos) {
      world.m_46672_(pos, this);

      for(Direction value : Direction.values()) {
         world.m_46672_(pos.m_121945_(value.m_122424_()), this);
      }

   }

   static {
      POWERED = BlockStateProperties.f_61448_;
   }
}
