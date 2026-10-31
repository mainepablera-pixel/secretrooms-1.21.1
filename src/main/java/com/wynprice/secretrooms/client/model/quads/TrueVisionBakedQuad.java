package com.wynprice.secretrooms.client.model.quads;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

/**
 * True Vision overlay: a copy of a secret block's quad, nudged 0.001 outward and
 * textured with the mod's animated striped overlay, drawn full-bright in the
 * translucent layer on top of the normal camouflage (same behaviour as the
 * original 1.19.2 mod).
 */
public final class TrueVisionBakedQuad {
    public static final ResourceLocation OVERLAY_LOCATION =
            ResourceLocation.fromNamespaceAndPath("secretroomsmod", "block/overlay");
    private static volatile TextureAtlasSprite overlaySprite;

    private TrueVisionBakedQuad() {}

    public static boolean isBlockAtlas(ResourceLocation atlas) {
        return InventoryMenu.BLOCK_ATLAS.equals(atlas);
    }

    public static boolean isReady() { return overlaySprite != null; }

    public static void setOverlaySprite(TextureAtlasSprite sprite) {
        overlaySprite = sprite;
    }

    /** Full-bright packed light (block 15, sky 15). */
    private static final int FULL_BRIGHT = 15728880;
    private static final int STRIDE = 8; // pos(3) colour(1) uv(2) light(1) normal(1)

    public static BakedQuad generateQuad(BakedQuad source) {
        TextureAtlasSprite sprite = overlaySprite;
        if (sprite == null) return source;

        int[] data = source.getVertices().clone();
        Direction face = source.getDirection();
        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float uSpan = sprite.getU1() - u0;
        float vSpan = sprite.getV1() - v0;

        for (int i = 0; i < 4; i++) {
            int j = STRIDE * i;
            float x = Float.intBitsToFloat(data[j]) + 0.001F * face.getStepX();
            float y = Float.intBitsToFloat(data[j + 1]) + 0.001F * face.getStepY();
            float z = Float.intBitsToFloat(data[j + 2]) + 0.001F * face.getStepZ();
            data[j] = Float.floatToRawIntBits(x);
            data[j + 1] = Float.floatToRawIntBits(y);
            data[j + 2] = Float.floatToRawIntBits(z);

            // Same projection as the original mod: the stripes follow world-space
            // position on the block, one overlay tile per block face.
            float ui;
            float vi;
            switch (face.getAxis()) {
                case X -> { ui = z; vi = 1.0F - y; }
                case Z -> { ui = x; vi = 1.0F - y; }
                default -> { ui = x; vi = z; }
            }
            ui = Math.max(0.0F, Math.min(1.0F, ui));
            vi = Math.max(0.0F, Math.min(1.0F, vi));

            // Interpolate between the sprite's own bounds. getU/getV are avoided on
            // purpose: their argument range (0..16 vs 0..1) differs between versions.
            data[j + 4] = Float.floatToRawIntBits(u0 + uSpan * ui);
            data[j + 5] = Float.floatToRawIntBits(v0 + vSpan * vi);
            data[j + 6] = FULL_BRIGHT;
        }
        return new BakedQuad(data, -1, face, sprite, source.isShade(), false);
    }
}
