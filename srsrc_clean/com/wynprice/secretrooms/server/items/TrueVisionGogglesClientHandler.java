package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;

public class TrueVisionGogglesClientHandler {
   private static boolean clientWearingItem;

   public static void onClientWorldLoad(ClientPlayerNetworkEvent.LoggingIn event) {
      clientWearingItem = isWearingGoggles(event.getPlayer());
   }

   public static void onClientWorldTick(TickEvent.ClientTickEvent event) {
      if (event.phase == Phase.START) {
         LocalPlayer player = Minecraft.m_91087_().f_91074_;
         if (player != null) {
            boolean wearing = isWearingGoggles(player);
            if (wearing != clientWearingItem) {
               clientWearingItem = wearing;
               LevelRenderer renderer = Minecraft.m_91087_().f_91060_;
               ClientChunkCache source = player.f_108545_.m_7726_();
               ChunkPos chunkPos = new ChunkPos(player.m_20183_());
               int d = (Integer)Minecraft.m_91087_().f_91066_.m_231984_().m_231551_();

               for(int x = -d; x <= d; ++x) {
                  for(int z = -d; z <= d; ++z) {
                     if (source.m_5563_(chunkPos.f_45578_ + x, chunkPos.f_45579_ + z)) {
                        for(BlockEntity blockEntity : source.m_62227_(chunkPos.f_45578_ + x, chunkPos.f_45579_ + z, false).m_62954_().values()) {
                           if (blockEntity instanceof SecretTileEntity) {
                              BlockPos pos = blockEntity.m_58899_();
                              renderer.m_109544_((BlockGetter)null, pos, (BlockState)null, (BlockState)null, 8);
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   public static boolean isWearingGoggles(Player player) {
      return player.m_6844_(EquipmentSlot.HEAD).m_41720_() == SecretItems.TRUE_VISION_GOGGLES.get();
   }
}
