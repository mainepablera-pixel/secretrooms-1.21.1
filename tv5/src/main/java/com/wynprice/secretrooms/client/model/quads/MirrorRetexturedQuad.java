package com.wynprice.secretrooms.client.model.quads;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Retextures an existing quad's geometry onto a sprite taken from the copied block,
 * keeping that source quad's tint index so biome-tinted blocks (leaves, grass, vines...)
 * are coloured by the secret block's colour handler instead of rendering grayscale.
 */
public final class MirrorRetexturedQuad extends BakedQuad {

    public MirrorRetexturedQuad(BakedQuad geometry, TextureAtlasSprite texture, int tintIndex) {
        super(QuadUvRemapper.remap(geometry, texture), tintIndex,
                FaceBakery.calculateFacing(geometry.getVertices()),
                texture, geometry.isShade(), geometry.hasAmbientOcclusion());
    }
}
