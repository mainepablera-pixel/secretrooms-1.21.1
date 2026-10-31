package com.wynprice.secretrooms.server.blocks.states;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.client.world.DelegateWorld;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OneWayGlassState extends SecretBaseState {
   public OneWayGlassState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
      super(block, propertiesToValueMap, codec);
   }

   public boolean m_60815_() {
      StackTraceElement trace = Thread.currentThread().getStackTrace()[2];
      return !"net.minecraft.world.level.block.Block".equals(trace.getClassName()) || !"shouldRenderFace".equals(trace.getMethodName());
   }

   public VoxelShape m_60655_(BlockGetter worldIn, BlockPos pos, Direction directionIn) {
      Optional<BlockState> mirror = SecretBaseBlock.getMirrorState(worldIn, pos);
      DelegateWorld world = DelegateWorld.getPooled(worldIn);
      VoxelShape shape = (Boolean)this.m_61143_(SecretBaseBlock.SOLID) && mirror.isPresent() && !(Boolean)this.m_61143_((Property)PipeBlock.f_55154_.get(directionIn)) && ((BlockState)mirror.get()).m_60659_(world, pos, directionIn, SupportType.CENTER) ? Shapes.m_83144_() : Shapes.m_83040_();
      world.release();
      return shape;
   }
}
