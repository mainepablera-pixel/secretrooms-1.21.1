package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.server.tileentity.SecretDaylightDetectorTileEntity;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class SecretDaylightDetector extends SecretBaseBlock {
   public static final IntegerProperty POWER;
   public static final BooleanProperty INVERTED;

   public SecretDaylightDetector(BlockBehaviour.Properties properties) {
      super(properties);
      this.m_49959_((BlockState)((BlockState)this.m_49966_().m_61124_(POWER, 0)).m_61124_(INVERTED, false));
   }

   public int m_6378_(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
      return (Integer)blockState.m_61143_(POWER);
   }

   public static void updatePower(BlockState state, Level world, BlockPos pos) {
      if (world.m_6042_().f_223549_()) {
         int light = world.m_45517_(LightLayer.SKY, pos) - world.m_7445_();

         for(Direction value : Direction.values()) {
            light = Math.max(light, world.m_45517_(LightLayer.SKY, pos.m_121945_(value)) - world.m_7445_());
         }

         float sunAngle = world.m_46490_(1.0F);
         boolean flag = (Boolean)state.m_61143_(INVERTED);
         if (flag) {
            light = 15 - light;
         } else if (light > 0) {
            float f1 = sunAngle < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            sunAngle += (f1 - sunAngle) * 0.2F;
            light = Math.round((float)light * Mth.m_14089_(sunAngle));
         }

         light = Mth.m_14045_(light, 0, 15);
         if ((Integer)state.m_61143_(POWER) != light) {
            world.m_7731_(pos, (BlockState)state.m_61124_(POWER, light), 3);
         }
      }

   }

   public InteractionResult m_6227_(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (player.m_36326_()) {
         if (worldIn.f_46443_) {
            return InteractionResult.SUCCESS;
         } else {
            BlockState blockstate = (BlockState)state.m_61122_(INVERTED);
            worldIn.m_7731_(pos, blockstate, 4);
            updatePower(blockstate, worldIn, pos);
            return InteractionResult.SUCCESS;
         }
      } else {
         return super.m_6227_(state, worldIn, pos, player, handIn, hit);
      }
   }

   public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
      return false;
   }

   public boolean m_7899_(BlockState state) {
      return true;
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new SecretDaylightDetectorTileEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState state, BlockEntityType<T> type) {
      return !level.f_46443_ && level.m_6042_().f_223549_() ? BaseEntityBlock.m_152132_(type, (BlockEntityType)SecretTileEntities.SECRET_DAYLIGHT_DETECTOR_TILE_ENTITY.get(), SecretDaylightDetector::tickEntity) : null;
   }

   private static void tickEntity(Level level, BlockPos pos, BlockState state, SecretDaylightDetectorTileEntity t) {
      if (level.m_46467_() % 20L == 0L) {
         updatePower(state, level, pos);
      }

   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{POWER, INVERTED});
   }

   static {
      POWER = BlockStateProperties.f_61426_;
      INVERTED = BlockStateProperties.f_61441_;
   }
}
