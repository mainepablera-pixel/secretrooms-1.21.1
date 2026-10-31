package com.wynprice.secretrooms.server.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.server.blocks.states.OneWayGlassState;
import com.wynprice.secretrooms.server.data.SecretBlockTags;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class OneWayGlass extends SecretBaseBlock {
   private static final BooleanProperty NORTH;
   private static final BooleanProperty EAST;
   private static final BooleanProperty SOUTH;
   private static final BooleanProperty WEST;
   private static final BooleanProperty UP;
   private static final BooleanProperty DOWN;

   public OneWayGlass(BlockBehaviour.Properties properties) {
      super(properties);
      this.m_49959_((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.m_49966_().m_61124_(NORTH, true)).m_61124_(EAST, true)).m_61124_(SOUTH, true)).m_61124_(WEST, true)).m_61124_(UP, true)).m_61124_(DOWN, true));
   }

   protected BlockState createNewState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
      return new OneWayGlassState(block, propertiesToValueMap, codec);
   }

   public Boolean getSolidValue() {
      return true;
   }

   public boolean m_7923_(BlockState state) {
      return true;
   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{NORTH, EAST, SOUTH, WEST, UP, DOWN});
   }

   public InteractionResult m_6227_(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (player.m_21120_(handIn).m_41619_()) {
         worldIn.m_7731_(pos, (BlockState)state.m_61122_((Property)PipeBlock.f_55154_.get(hit.m_82434_())), 3);
         BlockEntity tileEntity = worldIn.m_7702_(pos);
         if (tileEntity != null) {
            tileEntity.requestModelDataUpdate();
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.m_6227_(state, worldIn, pos, player, handIn, hit);
      }
   }

   public int m_7753_(BlockState state, BlockGetter world, BlockPos pos) {
      return 0;
   }

   public boolean m_7420_(BlockState state, BlockGetter world, BlockPos pos) {
      return false;
   }

   public boolean m_6104_(BlockState state, BlockState adjacentBlockState, Direction side) {
      boolean isGlass = (Boolean)state.m_61143_((Property)PipeBlock.f_55154_.get(side));
      boolean isInCullTag = adjacentBlockState.m_204336_(SecretBlockTags.ONE_WAY_GLASS_CULL);
      return super.m_6104_(state, adjacentBlockState, side) || isGlass && isInCullTag;
   }

   @Nullable
   public BlockState m_5573_(BlockPlaceContext context) {
      BlockPos offFace = context.m_7058_() ? context.m_8083_() : context.m_8083_().m_121945_(context.m_43719_().m_122424_());
      BlockState state = context.m_43725_().m_8055_(offFace);
      return state.m_60734_() == this ? state : (BlockState)super.m_5573_(context).m_61124_((Property)PipeBlock.f_55154_.get(context.m_7820_().m_122424_()), false);
   }

   public BlockState m_7417_(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      BlockEntity tileEntity = worldIn.m_7702_(currentPos);
      if (tileEntity != null && tileEntity.m_58904_().f_46443_) {
         tileEntity.requestModelDataUpdate();
      }

      return super.m_7417_(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   static {
      NORTH = PipeBlock.f_55148_;
      EAST = PipeBlock.f_55149_;
      SOUTH = PipeBlock.f_55150_;
      WEST = PipeBlock.f_55151_;
      UP = PipeBlock.f_55152_;
      DOWN = PipeBlock.f_55153_;
   }
}
