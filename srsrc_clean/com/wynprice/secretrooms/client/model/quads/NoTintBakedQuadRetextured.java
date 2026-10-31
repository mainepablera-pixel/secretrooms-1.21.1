package com.wynprice.secretrooms.client.model.quads;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.Arrays;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class NoTintBakedQuadRetextured extends BakedQuad {
   private final TextureAtlasSprite texture;

   public NoTintBakedQuadRetextured(BakedQuad quad, TextureAtlasSprite texture) {
      super(Arrays.copyOf(quad.m_111303_(), quad.m_111303_().length), -1, FaceBakery.m_111612_(quad.m_111303_()), quad.m_173410_(), quad.m_111307_());
      this.texture = texture;
      this.remapQuad();
   }

   protected void remapQuad() {
      for(int i = 0; i < 4; ++i) {
         int off = DefaultVertexFormat.f_85811_.m_86017_() * i;
         this.f_111292_[off + 4] = Float.floatToRawIntBits(this.texture.m_118367_((double)getUnInterpolatedU(Float.intBitsToFloat(this.f_111292_[off + 4]), this.f_111295_)));
         this.f_111292_[off + 5] = Float.floatToRawIntBits(this.texture.m_118393_((double)getUnInterpolatedV(Float.intBitsToFloat(this.f_111292_[off + 5]), this.f_111295_)));
      }

   }

   public static float getUnInterpolatedU(float u, TextureAtlasSprite sprite) {
      float f = sprite.m_118410_() - sprite.m_118409_();
      return (u - sprite.m_118409_()) / f * 16.0F;
   }

   public static float getUnInterpolatedV(float v, TextureAtlasSprite sprite) {
      float f = sprite.m_118412_() - sprite.m_118411_();
      return (v - sprite.m_118411_()) / f * 16.0F;
   }

   public TextureAtlasSprite m_173410_() {
      return this.texture;
   }
}
