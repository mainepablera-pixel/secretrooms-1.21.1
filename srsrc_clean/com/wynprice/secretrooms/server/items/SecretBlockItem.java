package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.client.world.DummyIWorld;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

class SecretBlockItem extends BlockItem {
   public SecretBlockItem(Block blockIn, Item.Properties builder) {
      super(blockIn, builder);
   }

   protected boolean m_7429_(BlockPlaceContext context, BlockState state) {
      BlockPos offFace = context.m_7058_() ? context.m_8083_() : context.m_8083_().m_121945_(context.m_43719_().m_122424_());
      BlockState placedOnStateRaw = context.m_43725_().m_8055_(offFace);
      BlockEntity placedOnTileEntity = context.m_43725_().m_7702_(offFace);
      if (placedOnTileEntity instanceof SecretTileEntity) {
         SecretData data = ((SecretTileEntity)placedOnTileEntity).getData();
         placedOnStateRaw = data.getBlockState();
         placedOnTileEntity = data.getTileEntityCache();
      }

      BlockState placedOnState = placedOnStateRaw.m_60734_().m_5573_(new BlockPlaceContext(context.m_43723_(), context.m_43724_(), context.m_43722_(), new BlockHitResult(context.m_43720_(), context.m_43719_(), offFace, context.m_43721_())));
      if (placedOnState == null) {
         placedOnState = placedOnStateRaw;
      }

      if (placedOnState.m_61138_(BlockStateProperties.f_61362_)) {
         placedOnState = (BlockState)placedOnState.m_61124_(BlockStateProperties.f_61362_, false);
      }

      return this.doSetBlock(context.m_43725_(), context.m_8083_(), offFace, state, placedOnState, placedOnTileEntity);
   }

   protected boolean doSetBlock(Level world, BlockPos pos, BlockPos placedOn, BlockState state, BlockState placedOnState, BlockEntity placedOnTileEntity) {
      if (world.m_7731_(pos, this.m_40614_() instanceof SecretBaseBlock ? ((SecretBaseBlock)this.m_40614_()).getPlaceState(world, placedOn, placedOnState, state) : state, 11)) {
         placedOnState = Block.m_49931_(placedOnState, new DummyIWorld(world), pos);
         this.setData(world, pos, placedOnState, placedOnTileEntity);
         world.m_7726_().m_7827_().m_7174_(pos);
         return true;
      } else {
         return false;
      }
   }

   protected void setData(Level world, BlockPos pos, BlockState placedOnState, BlockEntity placedOnTileEntity) {
      BlockEntity te = world.m_7702_(pos);
      if (te instanceof SecretTileEntity) {
         SecretData data = ((SecretTileEntity)te).getData();
         data.setBlockState(placedOnState);
         data.setTileEntityNBT(placedOnTileEntity != null ? placedOnTileEntity.serializeNBT() : null);
      }

   }
}
