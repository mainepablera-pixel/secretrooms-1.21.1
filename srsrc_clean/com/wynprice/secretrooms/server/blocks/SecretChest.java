package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.server.tileentity.SecretChestTileEntity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SecretChest extends SecretBaseBlock {
   public SecretChest(BlockBehaviour.Properties properties) {
      super(properties);
   }

   public InteractionResult m_6227_(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (worldIn.f_46443_) {
         return InteractionResult.SUCCESS;
      } else {
         BlockEntity tileentity = worldIn.m_7702_(pos);
         if (tileentity instanceof SecretChestTileEntity) {
            player.m_5893_((SecretChestTileEntity)tileentity);
         }

         return InteractionResult.SUCCESS;
      }
   }

   public void m_6810_(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.m_60734_() != newState.m_60734_()) {
         BlockEntity tileentity = worldIn.m_7702_(pos);
         if (tileentity instanceof Container) {
            Containers.m_19002_(worldIn, pos, (Container)tileentity);
            worldIn.m_46717_(pos, this);
         }

         super.m_6810_(state, worldIn, pos, newState, isMoving);
      }

   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new SecretChestTileEntity(pos, state);
   }

   public boolean m_7278_(BlockState state) {
      return true;
   }

   public int m_6782_(BlockState blockState, Level worldIn, BlockPos pos) {
      return AbstractContainerMenu.m_38918_(worldIn.m_7702_(pos));
   }
}
