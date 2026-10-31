package com.wynprice.secretrooms.server.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;

public class TrueVisionGogglesHandler {
   private static final ResourceLocation END_CHEST_LOOT = new ResourceLocation("chests/end_city_treasure");
   public static final float GOGGLES_BREAK_CHANCE = 0.2F;

   public static void onLootTableLoad(LootTableLoadEvent event) {
      if (END_CHEST_LOOT.equals(event.getName())) {
         event.getTable().addPool(LootPool.m_79043_().m_79076_(LootItem.m_79579_((ItemLike)SecretItems.TRUE_VISION_GOGGLES.get()).m_79080_(LootItemRandomChanceCondition.m_81927_(0.1F))).m_79082_());
      }

   }

   public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.side.isServer() && event.phase == Phase.END && event.player.f_19797_ % 20 == 0 && event.player.m_217043_().m_188501_() < 0.2F) {
         ItemStack stack = event.player.m_6844_(EquipmentSlot.HEAD);
         if (stack.m_41720_() == SecretItems.TRUE_VISION_GOGGLES.get()) {
            stack.m_41622_(1, event.player, (p) -> p.m_21166_(EquipmentSlot.HEAD));
         }
      }

   }
}
