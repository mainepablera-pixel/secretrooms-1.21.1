package com.wynprice.secretrooms.client.model.quads;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.Arrays;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.TextureStitchEvent;

public class TrueVisionBakedQuad {
   private static final ResourceLocation OVERLAY_LOCATION = new ResourceLocation("secretroomsmod", "block/overlay");
   private static TextureAtlasSprite overlaySprite;

   public static void onTextureStitch(TextureStitchEvent.Pre event) {
      if (InventoryMenu.f_39692_.equals(event.getAtlas().m_118330_())) {
         event.addSprite(OVERLAY_LOCATION);
      }

   }

   public static void onTextureStitched(TextureStitchEvent.Post event) {
      if (InventoryMenu.f_39692_.equals(event.getAtlas().m_118330_())) {
         overlaySprite = event.getAtlas().m_118316_(OVERLAY_LOCATION);
      }

   }

   public static BakedQuad generateQuad(BakedQuad quad) {
      int[] data = Arrays.copyOf(quad.m_111303_(), quad.m_111303_().length);

      for(int i = 0; i < 4; ++i) {
         int j = DefaultVertexFormat.f_85811_.m_86017_() * i;
         float x = Float.intBitsToFloat(data[j]) + 0.001F * (float)quad.m_111306_().m_122429_();
         float y = Float.intBitsToFloat(data[j + 1]) + 0.001F * (float)quad.m_111306_().m_122430_();
         float z = Float.intBitsToFloat(data[j + 2]) + 0.001F * (float)quad.m_111306_().m_122431_();
         data[j] = Float.floatToRawIntBits(x);
         data[j + 1] = Float.floatToRawIntBits(y);
         data[j + 2] = Float.floatToRawIntBits(z);
         float ui;
         float vi;
         switch (quad.m_111306_().m_122434_()) {
            case X:
               ui = z;
               vi = 1.0F - y;
               break;
            case Y:
            default:
               ui = x;
               vi = z;
               break;
            case Z:
               ui = x;
               vi = 1.0F - y;
         }

         data[j + 4] = Float.floatToRawIntBits(overlaySprite.m_118367_((double)(ui * 16.0F)));
         data[j + 5] = Float.floatToRawIntBits(overlaySprite.m_118393_((double)(vi * 16.0F)));
         data[j + 6] = 15728880;
      }

      return new BakedQuad(data, -1, quad.m_111306_(), overlaySprite, quad.m_111307_());
   }
}
