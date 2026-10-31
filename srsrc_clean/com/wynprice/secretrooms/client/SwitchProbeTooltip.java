package com.wynprice.secretrooms.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class SwitchProbeTooltip extends ClientTextTooltip {
   private final SwitchProbeTooltipComponent component;

   public SwitchProbeTooltip(SwitchProbeTooltipComponent component) {
      super(component.getText());
      this.component = component;
   }

   public void m_183452_(Font font, int x, int y, PoseStack stack, ItemRenderer item, int p_194053_) {
      int xStart = x + super.m_142069_(font) + 3;
      int yStart = y - 6;
      if (this.component.getItemStackIfRender() != null) {
         item.m_174253_(this.component.getItemStackIfRender(), xStart, yStart, 0);
         item.m_115169_(font, this.component.getItemStackIfRender(), xStart, yStart);
      } else if (this.component.getSpriteIfRender() != null) {
         int colour = this.component.getSpriteColourIfRender();
         RenderSystem.m_157429_((float)(colour >> 16 & 255) / 255.0F, (float)(colour >> 8 & 255) / 255.0F, (float)(colour & 255) / 255.0F, 1.0F);
         GuiComponent.m_93200_(stack, xStart, yStart, 0, 16, 16, this.component.getSpriteIfRender());
      }

   }

   public int m_142069_(Font font) {
      int superWidth = super.m_142069_(font);
      return this.component.getItemStackIfRender() == null && this.component.getSpriteIfRender() == null ? superWidth : superWidth + 20;
   }
}
