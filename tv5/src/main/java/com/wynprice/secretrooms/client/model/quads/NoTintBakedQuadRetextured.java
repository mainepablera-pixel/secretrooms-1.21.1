package com.wynprice.secretrooms.client.model.quads;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/** Retextures an existing quad while preserving its geometry and removing tint. */
public final class NoTintBakedQuadRetextured extends BakedQuad {

    public NoTintBakedQuadRetextured(BakedQuad source, TextureAtlasSprite texture) {
        super(QuadUvRemapper.remap(source, texture), -1, FaceBakery.calculateFacing(source.getVertices()),
                texture, source.isShade(), source.hasAmbientOcclusion());
    }
}
