package com.wynprice.secretrooms.client;

import com.wynprice.secretrooms.server.data.SecretData;
import com.wynprice.secretrooms.server.items.SecretItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SwitchProbeTooltipComponent implements TooltipComponent {
   private final FormattedCharSequence text;
   private TextureAtlasSprite spriteIfRender;
   private int spriteColourIfRender;
   private ItemStack itemStackIfRender;

   public SwitchProbeTooltipComponent(FormattedCharSequence text, ItemStack stack) {
      this.text = text;
      CompoundTag compound = stack.m_41737_("probe_hit_data");
      if (SecretItems.SWITCH_PROBE.get() == stack.m_41720_() && compound != null && !compound.m_128456_()) {
         SecretData data = new SecretData((BlockEntity)null);
         data.readNBT(compound);
         Item item = data.getBlockState().m_60734_().m_5456_();
         if (item == Items.f_41852_) {
            this.spriteIfRender = Minecraft.m_91087_().m_91289_().m_110907_().m_110882_(data.getBlockState());
            this.spriteColourIfRender = Minecraft.m_91087_().m_91298_().m_92577_(data.getBlockState(), Minecraft.m_91087_().f_91073_, Minecraft.m_91087_().f_91074_.m_20183_(), 0);
         } else {
            this.itemStackIfRender = new ItemStack(item);
         }

      }
   }

   public FormattedCharSequence getText() {
      return this.text;
   }

   public TextureAtlasSprite getSpriteIfRender() {
      return this.spriteIfRender;
   }

   public int getSpriteColourIfRender() {
      return this.spriteColourIfRender;
   }

   public ItemStack getItemStackIfRender() {
      return this.itemStackIfRender;
   }
}
