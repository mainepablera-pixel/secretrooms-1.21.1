package com.wynprice.secretrooms.server.blocks.states;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SecretBaseState extends BlockState {
   private static ThreadLocal<List<Block.BlockStatePairKey>> TO_REMOVE = ThreadLocal.withInitial(ArrayList::new);

   public SecretBaseState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
      super(block, propertiesToValueMap, codec);
   }

   public boolean m_60719_(BlockState p_60720_, Direction p_60721_) {
      this.removeAllKeys();
      return super.m_60719_(p_60720_, p_60721_);
   }

   public boolean m_60815_() {
      this.removeAllKeys();
      Block block = this.m_60734_();
      if (block instanceof SecretBaseBlock) {
         Boolean value = ((SecretBaseBlock)block).getSolidValue();
         if (value != null) {
            return value;
         }
      }

      return (Boolean)this.m_61143_(SecretBaseBlock.SOLID);
   }

   public float m_60800_(BlockGetter worldIn, BlockPos pos) {
      float value = (Float)SecretBaseBlock.getValue(worldIn, pos, BlockBehaviour.BlockStateBase::m_60800_, () -> super.m_60800_(worldIn, pos));
      if (value > 5.0F) {
         return 5.0F;
      } else {
         return value < 0.0F ? 0.5F : value;
      }
   }

   public VoxelShape m_60655_(BlockGetter worldIn, BlockPos p, Direction directionIn) {
      this.addOcclusionCacheKeysForRemoval(worldIn, p, directionIn);
      return (VoxelShape)SecretBaseBlock.getValue(worldIn, p, (mirror, reader, pos1) -> mirror.m_60655_(reader, p, directionIn), () -> super.m_60655_(worldIn, p, directionIn));
   }

   public boolean m_60788_(BlockGetter worldIn, BlockPos pos) {
      return (Boolean)SecretBaseBlock.getValue(worldIn, pos, (mirror, reader, pos1) -> mirror.m_60788_(reader, pos), () -> super.m_60788_(worldIn, pos));
   }

   public boolean m_60787_() {
      return true;
   }

   private void addOcclusionCacheKeysForRemoval(BlockGetter worldIn, BlockPos p, Direction directionIn) {
      BlockState other = worldIn.m_8055_(p.m_121945_(directionIn));
      Block.BlockStatePairKey thisCall = new Block.BlockStatePairKey(other, this.m_7160_(), directionIn.m_122424_());
      Block.BlockStatePairKey otherCall = new Block.BlockStatePairKey(this.m_7160_(), other, directionIn);
      List<Block.BlockStatePairKey> keys = (List)TO_REMOVE.get();
      keys.add(thisCall);
      keys.add(otherCall);
   }

   private void removeAllKeys() {
      List<Block.BlockStatePairKey> keys = (List)TO_REMOVE.get();
      Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> map = (Object2ByteLinkedOpenHashMap)Block.f_49789_.get();

      for(Block.BlockStatePairKey key : keys) {
         map.removeByte(key);
      }

      keys.clear();
   }
}
