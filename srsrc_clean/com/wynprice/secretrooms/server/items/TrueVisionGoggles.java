package com.wynprice.secretrooms.server.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.wynprice.secretrooms.client.model.TrueVisionGogglesModel;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class TrueVisionGoggles extends ArmorItem {
   private static final String ARMOR_TEXTURE = (new ResourceLocation("secretroomsmod", "textures/models/armor/true_vision_goggles.png")).toString();
   @OnlyIn(Dist.CLIENT)
   private TrueVisionGogglesModel model;

   public TrueVisionGoggles(Item.Properties properties) {
      super(TrueVisionGoggles.DummyArmorMaterial.INSTANCE, EquipmentSlot.HEAD, properties);
   }

   public Multimap<Attribute, AttributeModifier> m_7167_(EquipmentSlot equipmentSlot) {
      return ImmutableMultimap.of();
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      super.initializeClient(consumer);
      consumer.accept(new IClientItemExtensions() {
         public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
            if (TrueVisionGoggles.this.model == null) {
               TrueVisionGoggles.this.refreshArmorModel();
            }

            return TrueVisionGoggles.this.model;
         }
      });
   }

   @OnlyIn(Dist.CLIENT)
   public void refreshArmorModel() {
      this.model = new TrueVisionGogglesModel(Minecraft.m_91087_().m_167973_().m_171103_(TrueVisionGogglesModel.TRUE_VISION_GOGGLES_MODEL));
   }

   @Nullable
   public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
      return ARMOR_TEXTURE;
   }

   public boolean m_8120_(ItemStack stack) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return false;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }

   private static enum DummyArmorMaterial implements ArmorMaterial {
      INSTANCE;

      public int m_7366_(EquipmentSlot slotIn) {
         return 0;
      }

      public int m_7365_(EquipmentSlot slotIn) {
         return 0;
      }

      public int m_6646_() {
         return 0;
      }

      public SoundEvent m_7344_() {
         return SoundEvents.f_11672_;
      }

      public Ingredient m_6230_() {
         return Ingredient.f_43901_;
      }

      public String m_6082_() {
         return "true_sight_goggles";
      }

      public float m_6651_() {
         return 0.0F;
      }

      public float m_6649_() {
         return 0.0F;
      }

      // $FF: synthetic method
      private static DummyArmorMaterial[] $values() {
         return new DummyArmorMaterial[]{INSTANCE};
      }
   }
}
