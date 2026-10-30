package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class TorchLever extends TorchBlock {
   private static final BooleanProperty POWERED;

   protected TorchLever(BlockBehaviour.Properties properties) {
      super(properties, ParticleTypes.f_123744_);
      this.m_49959_((BlockState)this.m_49966_().m_61124_(POWERED, false));
   }

   public InteractionResult m_6227_(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
      state = (BlockState)state.m_61122_(POWERED);
      boolean isPowered = (Boolean)state.m_61143_(POWERED);
      if (world.f_46443_) {
         return InteractionResult.SUCCESS;
      } else {
         world.m_7731_(pos, state, 3);
         float pitch = isPowered ? 0.6F : 0.5F;
         world.m_5594_((Player)null, pos, SoundEvents.f_12088_, SoundSource.BLOCKS, 0.3F, pitch);
         this.updateNeighbors(world, pos);
         return InteractionResult.SUCCESS;
      }
   }

   private void updateNeighbors(Level world, BlockPos pos) {
      world.m_46672_(pos, this);

      for(Direction direction : Direction.values()) {
         world.m_46672_(pos.m_121945_(direction), this);
      }

   }

   public int m_6378_(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
      return (Boolean)state.m_61143_(POWERED) ? 15 : 0;
   }

   public int m_6376_(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
      return (Boolean)blockState.m_61143_(POWERED) && side == Direction.UP ? 15 : 0;
   }

   public boolean m_7899_(BlockState p_149744_1_) {
      return true;
   }

   public void m_6810_(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!isMoving && state.m_60734_() != newState.m_60734_()) {
         if ((Boolean)state.m_61143_(POWERED)) {
            this.updateNeighbors(world, pos);
         }

         super.m_6810_(state, world, pos, newState, isMoving);
      }

   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{POWERED});
   }

   static {
      POWERED = BlockStateProperties.f_61448_;
   }
}
