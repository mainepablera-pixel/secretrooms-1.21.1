package com.wynprice.secretrooms.server.blocks.states;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SecretMappedModelState extends SecretBaseState {
   public SecretMappedModelState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
      super(block, propertiesToValueMap, codec);
   }

   public VoxelShape m_60655_(BlockGetter worldIn, BlockPos p, Direction directionIn) {
      return Shapes.m_83121_(this.m_60768_(worldIn, p), directionIn);
   }
}
