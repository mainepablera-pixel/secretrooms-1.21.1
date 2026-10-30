package com.wynprice.secretrooms.client.model.quads;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Re-maps a baked quad's UVs from its original atlas sprite onto another sprite.
 *
 * <p>Vertex UVs in a {@link BakedQuad} are absolute atlas coordinates (0..1 across the whole
 * atlas). The new coordinate is interpolated directly between the target sprite's
 * {@code getU0()/getU1()} and {@code getV0()/getV1()}. We deliberately do NOT use
 * {@code TextureAtlasSprite#getU(float)} / {@code getV(float)} here: their argument range
 * differs between Minecraft versions (0..16 vs 0..1), and feeding {@code local * 16} into the
 * 0..1 variant makes every face sample sixteen sprites' worth of the atlas, which shows up
 * in-game as the whole block/item atlas painted over the model.</p>
 */
public final class QuadUvRemapper {
    /** Ints per vertex in the BLOCK vertex format: pos(3) colour(1) uv(2) light(1) normal(1). */
    public static final int VERTEX_STRIDE = 8;
    private static final int UV_OFFSET = 4;
    private static final int LIGHT_OFFSET = 6;

    private QuadUvRemapper() {}

    /** Returns a copy of the source vertex data with UVs remapped onto {@code target}. */
    public static int[] remap(BakedQuad source, TextureAtlasSprite target) {
        return remap(source, target, -1);
    }

    /**
     * Same as {@link #remap(BakedQuad, TextureAtlasSprite)} but, when {@code packedLight >= 0},
     * also overwrites every vertex's packed light value (used for the full-bright True Vision marker).
     */
    public static int[] remap(BakedQuad source, TextureAtlasSprite target, int packedLight) {
        int[] vertices = source.getVertices().clone();
        TextureAtlasSprite original = source.getSprite();

        float sourceU0 = original.getU0();
        float sourceV0 = original.getV0();
        float sourceUSpan = original.getU1() - sourceU0;
        float sourceVSpan = original.getV1() - sourceV0;

        float targetU0 = target.getU0();
        float targetV0 = target.getV0();
        float targetUSpan = target.getU1() - targetU0;
        float targetVSpan = target.getV1() - targetV0;

        for (int vertex = 0; vertex < 4; vertex++) {
            int offset = vertex * VERTEX_STRIDE;
            float u = Float.intBitsToFloat(vertices[offset + UV_OFFSET]);
            float v = Float.intBitsToFloat(vertices[offset + UV_OFFSET + 1]);

            // Position of this vertex inside the source sprite, 0..1. Guard zero-sized
            // spans and clamp so a stray UV can never reach into a neighbouring sprite.
            float localU = sourceUSpan == 0.0F ? 0.5F : clamp01((u - sourceU0) / sourceUSpan);
            float localV = sourceVSpan == 0.0F ? 0.5F : clamp01((v - sourceV0) / sourceVSpan);

            vertices[offset + UV_OFFSET] = Float.floatToRawIntBits(targetU0 + targetUSpan * localU);
            vertices[offset + UV_OFFSET + 1] = Float.floatToRawIntBits(targetV0 + targetVSpan * localV);
            if (packedLight >= 0) {
                vertices[offset + LIGHT_OFFSET] = packedLight;
            }
        }
        return vertices;
    }

    private static float clamp01(float value) {
        if (!Float.isFinite(value)) return 0.5F;
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
