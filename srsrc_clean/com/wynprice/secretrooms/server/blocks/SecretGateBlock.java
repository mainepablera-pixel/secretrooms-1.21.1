package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.server.data.SecretData;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class SecretGateBlock extends SecretBaseBlock {
   private static final int MAX_LEVELS = 10;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty OPEN;
   private static final DirectionProperty FACING;

   public SecretGateBlock(BlockBehaviour.Properties properties) {
      super(properties);
   }

   public void m_6861_(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      boolean powered = (Boolean)state.m_61143_(POWERED);
      boolean newPowered = worldIn.m_46753_(pos);
      if (powered != newPowered) {
         if (newPowered) {
            this.tryBuildGate(worldIn, pos, state);
         } else {
            this.destroyGate(worldIn, pos, state, true);
         }

         worldIn.m_46597_(pos, (BlockState)((BlockState)state.m_61124_(POWERED, newPowered)).m_61124_(OPEN, newPowered));
      }

   }

   public void m_6810_(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.m_60734_() != newState.m_60734_()) {
         this.destroyGate(worldIn, pos, state, false);
      }

      super.m_6810_(state, worldIn, pos, newState, isMoving);
   }

   @Nullable
   public BlockState m_5573_(BlockPlaceContext context) {
      return (BlockState)super.m_5573_(context).m_61124_(FACING, context.m_43719_());
   }

   public void m_213897_(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
      if ((Boolean)state.m_61143_(OPEN)) {
         this.tryBuildGate(worldIn, pos, state);
      } else {
         this.destroyGate(worldIn, pos, state, true);
      }

      super.m_213897_(state, worldIn, pos, rand);
   }

   private void tryBuildGate(Level world, BlockPos pos, BlockState gateState) {
      Optional<SecretData> data = getMirrorData(world, pos);
      Direction direction = (Direction)gateState.m_61143_(FACING);

      for(int i = 1; i <= 10; ++i) {
         BlockPos off = pos.m_5484_(direction, i);
         BlockState state = world.m_8055_(off);
         if (!state.m_60767_().m_76336_() && state.m_60734_() != SecretBlocks.SECRET_DUMMY_BLOCK.get()) {
            if (state.m_60734_() == this) {
               world.m_46597_(off, (BlockState)state.m_61124_(OPEN, true));
               world.m_186460_(off, this, 3);
            }

            return;
         }

         if (i == 10) {
            return;
         }

         for(Direction value : Direction.values()) {
            if (value.m_122434_() != direction.m_122434_()) {
               BlockPos offPos = off.m_121945_(value);
               BlockState offState = world.m_8055_(offPos);
               if (offState.m_60734_() == this) {
                  world.m_46597_(offPos, (BlockState)offState.m_61124_(OPEN, true));
                  world.m_186460_(offPos, this, 3);
               }
            }
         }

         world.m_46597_(off, ((Block)SecretBlocks.SECRET_DUMMY_BLOCK.get()).m_49966_());
         data.ifPresent((sdata) -> getMirrorData(world, off).ifPresent((d) -> d.setFrom(sdata)));
         requestModelRefresh(world, off);
         world.m_7702_(off).m_6596_();
      }

   }

   private void destroyGate(Level world, BlockPos pos, BlockState gateState, boolean recursive) {
      Direction direction = (Direction)gateState.m_61143_(FACING);

      for(int i = 1; i <= 10; ++i) {
         BlockPos off = pos.m_5484_(direction, i);
         BlockState state = world.m_8055_(off);
         if (state.m_60734_() != SecretBlocks.SECRET_DUMMY_BLOCK.get()) {
            if (state.m_60734_() == this && recursive) {
               world.m_46597_(off, (BlockState)state.m_61124_(OPEN, false));
               world.m_186460_(off, this, 3);
            }

            return;
         }

         if (i == 10) {
            return;
         }

         if (recursive) {
            for(Direction value : Direction.values()) {
               if (value.m_122434_() != direction.m_122434_()) {
                  BlockPos offPos = off.m_121945_(value);
                  BlockState offState = world.m_8055_(offPos);
                  if (offState.m_60734_() == this) {
                     world.m_46597_(offPos, (BlockState)offState.m_61124_(OPEN, false));
                     world.m_186460_(offPos, this, 3);
                  }
               }
            }
         }

         world.m_46597_(off, Blocks.f_50016_.m_49966_());
      }

   }

   protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{POWERED, OPEN, FACING});
   }

   static {
      POWERED = BlockStateProperties.f_61448_;
      OPEN = BlockStateProperties.f_61446_;
      FACING = DirectionalBlock.f_52588_;
   }
}
