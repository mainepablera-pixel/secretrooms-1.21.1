package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.server.tileentity.SecretChestTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SecretTrappedChest extends SecretChest {
   public SecretTrappedChest(BlockBehaviour.Properties properties) {
      super(properties);
   }

   public boolean m_7899_(BlockState state) {
      return true;
   }

   public int m_6378_(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
      BlockEntity tileEntity = blockAccess.m_7702_(pos);
      return tileEntity instanceof SecretChestTileEntity ? Mth.m_14045_(((SecretChestTileEntity)tileEntity).getNumPlayersUsing(), 0, 15) : 0;
   }

   public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
      return false;
   }

   public int m_6376_(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
      return side == Direction.UP ? blockState.m_60746_(blockAccess, pos, side) : 0;
   }
}
