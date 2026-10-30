package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import com.wynprice.secretrooms.server.items.SecretItems;
import java.util.function.Consumer;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemPredicate.Builder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class SecretRecipeProvider extends RecipeProvider {
   public SecretRecipeProvider(DataGenerator generatorIn) {
      super(generatorIn);
   }

   protected void m_176531_(Consumer<FinishedRecipe> consumer) {
      Item camoPaste = (Item)SecretItems.CAMOUFLAGE_PASTE.get();
      ShapedRecipeBuilder.m_126116_((ItemLike)SecretBlocks.TORCH_LEVER.get()).m_126127_('T', Items.f_42000_).m_126127_('L', Items.f_41966_).m_126130_("T").m_126130_("L").m_126145_("torch_lever.json").m_126132_("has_torch_lever", this.hasItems(Items.f_42000_, Items.f_41966_)).m_176498_(consumer);
      ShapedRecipeBuilder.m_126118_(camoPaste, 9).m_206416_('X', net.minecraftforge.common.Tags.Items.DYES).m_126127_('#', Items.f_42461_).m_126130_("XXX").m_126130_("X#X").m_126130_("XXX").m_126145_("camouflage_paste").m_126132_("has_earth_item", m_206406_(SecretItemTags.EARTH_ITEM)).m_176498_(consumer);
      ShapedRecipeBuilder.m_126118_((ItemLike)SecretBlocks.GHOST_BLOCK.get(), 4).m_126127_('X', camoPaste).m_206416_('0', SecretItemTags.SECRET_RECIPE_ITEMS).m_126130_("X0X").m_126130_("0 0").m_126130_("X0X").m_126145_("ghost_block").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapedRecipeBuilder.m_126118_((ItemLike)SecretBlocks.ONE_WAY_GLASS.get(), 9).m_126127_('X', camoPaste).m_206416_('0', SecretItemTags.CLEAR_GLASS).m_126130_("XXX").m_126130_("000").m_126130_("000").m_126145_("one_way_glass").m_126132_("has_camo", m_125977_(camoPaste)).m_126140_(consumer, new ResourceLocation("secretroomsmod", "one_way_glass_up"));
      ShapedRecipeBuilder.m_126118_((ItemLike)SecretBlocks.ONE_WAY_GLASS.get(), 9).m_126127_('X', camoPaste).m_206416_('0', SecretItemTags.CLEAR_GLASS).m_126130_("000").m_126130_("000").m_126130_("XXX").m_126145_("one_way_glass").m_126132_("has_camo", m_125977_(camoPaste)).m_126140_(consumer, new ResourceLocation("secretroomsmod", "one_way_glass_down"));
      ShapedRecipeBuilder.m_126118_((ItemLike)SecretBlocks.ONE_WAY_GLASS.get(), 9).m_126127_('X', camoPaste).m_206416_('0', SecretItemTags.CLEAR_GLASS).m_126130_("X00").m_126130_("X00").m_126130_("X00").m_126145_("one_way_glass").m_126132_("has_camo", m_125977_(camoPaste)).m_126140_(consumer, new ResourceLocation("secretroomsmod", "one_way_glass_left"));
      ShapedRecipeBuilder.m_126118_((ItemLike)SecretBlocks.ONE_WAY_GLASS.get(), 9).m_126127_('X', camoPaste).m_206416_('0', SecretItemTags.CLEAR_GLASS).m_126130_("00X").m_126130_("00X").m_126130_("00X").m_126145_("one_way_glass").m_126132_("has_camo", m_125977_(camoPaste)).m_126140_(consumer, new ResourceLocation("secretroomsmod", "one_way_glass_right"));
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretItems.SWITCH_PROBE.get()).m_126209_(camoPaste).m_126209_(Items.f_41978_).m_126145_("switch_probe").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.ONE_WAY_GLASS.get()).m_126209_(camoPaste).m_206419_(SecretItemTags.CLEAR_GLASS).m_126145_("one_way_glass_shapeless").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_CHEST.get()).m_126209_(camoPaste).m_206419_(net.minecraftforge.common.Tags.Items.CHESTS_WOODEN).m_126145_("secret_chest").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_PRESSURE_PLATE.get()).m_126209_(camoPaste).m_206419_(ItemTags.f_13177_).m_126145_("secret_pressure_plate").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get()).m_126209_(camoPaste).m_126209_(Items.f_41967_).m_126145_("secret_player_pressure_plate").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_DOOR.get()).m_126209_(camoPaste).m_206419_(ItemTags.f_13173_).m_126145_("secret_door").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_IRON_DOOR.get()).m_126209_(camoPaste).m_126209_(Items.f_42341_).m_126145_("secret_iron_door").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_LEVER.get()).m_126209_(camoPaste).m_126209_(Items.f_41966_).m_126145_("secret_lever").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get()).m_126209_(camoPaste).m_126209_(Items.f_42152_).m_126145_("secret_daylight_detector").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_REDSTONE.get()).m_126209_(camoPaste).m_126209_(Items.f_42451_).m_126145_("secret_redstone").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_STAIRS.get()).m_126209_(camoPaste).m_206419_(ItemTags.f_13138_).m_126145_("secret_stairs").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_WOODEN_BUTTON.get()).m_126209_(camoPaste).m_206419_(ItemTags.f_13170_).m_126145_("secret_wooden_button").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_STONE_BUTTON.get()).m_126209_(camoPaste).m_126209_(Items.f_42083_).m_126145_("secret_stone_button").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_TRAPPED_CHEST.get()).m_126209_(camoPaste).m_206419_(net.minecraftforge.common.Tags.Items.CHESTS_TRAPPED).m_126145_("secret_trapped_chest").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_TRAPDOOR.get()).m_126209_(camoPaste).m_206419_(ItemTags.f_13178_).m_126145_("secret_trapdoor").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_IRON_TRAPDOOR.get()).m_126209_(camoPaste).m_126209_(Items.f_42128_).m_126145_("secret_iron_trapdoor").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_OBSERVER.get()).m_126209_(camoPaste).m_126209_(Items.f_42264_).m_126145_("secret_observer").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapelessRecipeBuilder.m_126189_((ItemLike)SecretBlocks.SECRET_CLAMBER.get()).m_126209_(camoPaste).m_126209_(Items.f_41963_).m_126145_("secret_clamber").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
      ShapedRecipeBuilder.m_126116_((ItemLike)SecretBlocks.SECRET_GATE.get()).m_206416_('X', ItemTags.f_13168_).m_126127_('0', camoPaste).m_126127_('R', Items.f_42451_).m_126127_('A', Items.f_42584_).m_126130_("X0X").m_126130_("0A0").m_126130_("XRX").m_126145_("secret_gate").m_126132_("has_camo", m_125977_(camoPaste)).m_176498_(consumer);
   }

   private InventoryChangeTrigger.TriggerInstance hasItems(ItemLike... itemIn) {
      ItemPredicate.Builder builder = Builder.m_45068_();

      for(ItemLike provider : itemIn) {
         builder.m_151445_(new ItemLike[]{provider});
      }

      return m_126011_(new ItemPredicate[]{builder.m_45077_()});
   }
}
