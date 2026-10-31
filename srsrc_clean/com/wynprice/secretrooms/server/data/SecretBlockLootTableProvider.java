package com.wynprice.secretrooms.server.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import com.wynprice.secretrooms.server.items.SecretItems;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class SecretBlockLootTableProvider extends LootTableProvider {
   public SecretBlockLootTableProvider(DataGenerator dataGenerator) {
      super(dataGenerator);
   }

   protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
      map.forEach((res, table) -> LootTables.m_79202_(validationtracker, res, table));
   }

   protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
      return ImmutableList.of(Pair.of(SecretRoomsBlockLootTables::new, LootContextParamSets.f_81421_));
   }

   private static class SecretRoomsBlockLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
      private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();

      public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
         this.lootTables.clear();
         this.addTables();
         this.lootTables.forEach(consumer);
      }

      private void addTables() {
         this.createBlockDrop(SecretBlocks.GHOST_BLOCK);
         this.createBlockDrop(SecretBlocks.SECRET_STAIRS);
         this.createBlockDrop(SecretBlocks.SECRET_LEVER);
         this.createBlockDrop(SecretBlocks.SECRET_REDSTONE);
         this.createBlockDrop(SecretBlocks.ONE_WAY_GLASS);
         this.createBlockDrop(SecretBlocks.SECRET_WOODEN_BUTTON);
         this.createBlockDrop(SecretBlocks.SECRET_STONE_BUTTON);
         this.createBlockDrop(SecretBlocks.TORCH_LEVER);
         this.createBlockDrop(SecretBlocks.WALL_TORCH_LEVER, SecretItems.TORCH_LEVER);
         this.createBlockDrop(SecretBlocks.SECRET_PRESSURE_PLATE);
         this.createBlockDrop(SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE);
         this.createDoorItemTable(SecretBlocks.SECRET_DOOR);
         this.createDoorItemTable(SecretBlocks.SECRET_IRON_DOOR);
         this.createBlockDrop(SecretBlocks.SECRET_CHEST);
         this.createBlockDrop(SecretBlocks.SECRET_TRAPDOOR);
         this.createBlockDrop(SecretBlocks.SECRET_IRON_TRAPDOOR);
         this.createBlockDrop(SecretBlocks.SECRET_TRAPPED_CHEST);
         this.createBlockDrop(SecretBlocks.SECRET_GATE);
         this.createBlockDrop(SecretBlocks.SECRET_DAYLIGHT_DETECTOR);
         this.createBlockDrop(SecretBlocks.SECRET_OBSERVER);
         this.createBlockDrop(SecretBlocks.SECRET_CLAMBER);
      }

      private void createBlockDrop(Supplier<Block> block) {
         this.createBlockDrop(block, block);
      }

      private void createBlockDrop(Supplier<Block> block, Supplier<? extends ItemLike> item) {
         this.lootTables.put(((Block)block.get()).m_60589_(), this.createSingleItemTable((ItemLike)item.get()));
      }

      private LootTable.Builder createSingleItemTable(ItemLike item) {
         return LootTable.m_79147_().m_79161_(LootPool.m_79043_().m_165133_(ConstantValue.m_165692_(1.0F)).m_79076_(LootItem.m_79579_(item)).m_79080_(ExplosionCondition.m_81661_()));
      }

      private void createDoorItemTable(Supplier<Block> block) {
         this.lootTables.put(((Block)block.get()).m_60589_(), BlockLoot.m_124137_((Block)block.get()));
      }
   }
}
