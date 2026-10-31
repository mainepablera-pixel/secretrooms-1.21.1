package com.wynprice.secretrooms.server.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SecretDoubleBlockItem extends SecretBlockItem {
   public SecretDoubleBlockItem(Block blockIn, Item.Properties builder) {
      super(blockIn, builder);
   }

   protected boolean m_7429_(BlockPlaceContext context, BlockState state) {
      context.m_43725_().m_7731_(context.m_8083_().m_7494_(), Blocks.f_50016_.m_49966_(), 27);
      return super.m_7429_(context, state);
   }

   protected boolean doSetBlock(Level world, BlockPos pos, BlockPos placedOn, BlockState state, BlockState placedOnState, BlockEntity placedOnTileEntity) {
      return super.doSetBlock(world, pos, placedOn, state, placedOnState, placedOnTileEntity) | super.doSetBlock(world, pos.m_7494_(), placedOn, state, placedOnState, placedOnTileEntity);
   }
}
