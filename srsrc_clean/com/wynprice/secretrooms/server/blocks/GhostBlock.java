package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GhostBlock extends SecretBaseBlock {
   public static final BooleanProperty NORMAL_CUBE = BooleanProperty.m_61465_("normal_cube");

   public GhostBlock(BlockBehaviour.Properties properties) {
      super(properties);
      this.m_49959_((BlockState)this.m_49966_().m_61124_(NORMAL_CUBE, false));
   }

   public BlockState getPlaceState(BlockGetter wold, BlockPos placedOnPos, BlockState placedOn, BlockState fallback) {
      return (BlockState)super.getPlaceState(wold, placedOnPos, placedOn, fallback).m_61124_(NORMAL_CUBE, placedOn.m_60838_(wold, placedOnPos));
   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{NORMAL_CUBE});
      super.m_7926_(builder);
   }

   public VoxelShape m_5939_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return Shapes.m_83040_();
   }

   public boolean m_6104_(BlockState state, BlockState adjacent, Direction side) {
      return adjacent.m_60734_() == this && (Boolean)adjacent.m_61143_(NORMAL_CUBE) || super.m_6104_(state, adjacent, side);
   }
}
