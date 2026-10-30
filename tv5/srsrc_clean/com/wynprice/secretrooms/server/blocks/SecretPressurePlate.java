package com.wynprice.secretrooms.server.blocks;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;

public class SecretPressurePlate extends AbstractSecretPressurePlateBase {
   private static final BooleanProperty POWERED;
   private final Predicate<Entity> entityPredicate;

   public SecretPressurePlate(BlockBehaviour.Properties properties, Predicate<Entity> powerMapper) {
      super(properties);
      this.m_49959_((BlockState)this.m_49966_().m_61124_(POWERED, false));
      this.entityPredicate = powerMapper;
   }

   protected void playClickOnSound(LevelAccessor worldIn, BlockPos pos) {
      worldIn.m_5594_((Player)null, pos, SoundEvents.f_12449_, SoundSource.BLOCKS, 0.3F, 0.8F);
   }

   protected void playClickOffSound(LevelAccessor worldIn, BlockPos pos) {
      worldIn.m_5594_((Player)null, pos, SoundEvents.f_12636_, SoundSource.BLOCKS, 0.3F, 0.7F);
   }

   protected int computeRedstoneStrength(Level worldIn, BlockPos pos) {
      return worldIn.m_6443_(Entity.class, (new AABB(pos)).m_82363_((double)0.0F, (double)0.25F, (double)0.0F), this.entityPredicate).isEmpty() ? 0 : 15;
   }

   protected int getRedstoneStrength(BlockState state) {
      return (Boolean)state.m_61143_(POWERED) ? 15 : 0;
   }

   protected BlockState setRedstoneStrength(BlockState state, int strength) {
      return (BlockState)state.m_61124_(POWERED, strength > 0);
   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{POWERED});
   }

   static {
      POWERED = BlockStateProperties.f_61448_;
   }
}
