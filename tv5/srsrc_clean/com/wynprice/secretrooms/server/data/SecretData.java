package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SecretData {
   @Nullable
   private final BlockEntity base;
   private BlockState blockState;
   @Nullable
   private CompoundTag tileEntityNBT;
   private BlockEntity tileEntityCache;

   public SecretData(BlockEntity base) {
      this.blockState = Blocks.f_50069_.m_49966_();
      this.tileEntityNBT = null;
      this.base = base;
   }

   public CompoundTag writeNBT(CompoundTag tag) {
      tag.m_128365_("blockstate", NbtUtils.m_129202_(this.getBlockState()));
      if (this.tileEntityNBT != null) {
         tag.m_128365_("tile_data", this.tileEntityNBT);
      }

      return tag;
   }

   public void readNBT(CompoundTag tag) {
      this.setBlockState(NbtUtils.m_129241_(tag.m_128469_("blockstate")));
      this.setTileEntityNBT(tag.m_128469_("tile_data"));
   }

   public BlockEntity getTileEntityCache() {
      if (this.tileEntityNBT != null && this.tileEntityCache == null) {
         if ((this.tileEntityCache = BlockEntity.m_155241_(this.base.m_58899_(), this.blockState, this.tileEntityNBT)) != null) {
            if (this.base != null) {
               this.tileEntityCache.m_142339_(this.base.m_58904_());
            }

            this.tileEntityCache.f_58856_ = this.blockState;
         } else {
            SecretRooms6.LOGGER.warn("Unable to create Block Entity with id {}. Disregarding it from future references ", this.tileEntityNBT.m_128461_("id"));
            this.tileEntityNBT = null;
         }
      }

      return this.tileEntityCache;
   }

   public void setFrom(SecretData data) {
      this.setBlockState(data.getBlockState());
      this.setTileEntityNBT(data.getTileEntityNBT());
   }

   public void setBlockState(BlockState blockState) {
      this.blockState = blockState;
      this.onChanged();
   }

   public BlockState getBlockState() {
      if (this.blockState.m_60734_() instanceof SecretBaseBlock || this.base != null && this.blockState.m_60795_()) {
         this.blockState = Blocks.f_50069_.m_49966_();
      }

      return this.blockState;
   }

   public void setTileEntityNBT(@Nullable CompoundTag tileEntityNBT) {
      if (tileEntityNBT != null && tileEntityNBT.m_128456_()) {
         this.tileEntityNBT = null;
      } else {
         this.tileEntityNBT = tileEntityNBT;
      }

      this.tileEntityCache = null;
      this.onChanged();
   }

   private void onChanged() {
      if (this.base != null && this.base.m_58904_() != null) {
         Level world = this.base.m_58904_();
         if (world.f_46443_) {
            this.base.requestModelDataUpdate();
            world.m_7260_(this.base.m_58899_(), this.base.m_58900_(), this.base.m_58900_(), 11);
         } else {
            Packet<ClientGamePacketListener> supdatetileentitypacket = this.base.m_58483_();

            for(ServerPlayer player : ((ServerLevel)world).m_6907_()) {
               if (supdatetileentitypacket != null) {
                  player.f_8906_.m_9829_(supdatetileentitypacket);
               }
            }
         }

         world.m_7726_().m_7827_().m_7174_(this.base.m_58899_());
      }

   }

   @Nullable
   public CompoundTag getTileEntityNBT() {
      return this.tileEntityNBT;
   }
}
