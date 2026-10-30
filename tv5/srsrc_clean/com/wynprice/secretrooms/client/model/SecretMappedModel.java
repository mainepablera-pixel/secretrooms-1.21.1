package com.wynprice.secretrooms.client.model;

import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.client.model.quads.NoTintBakedQuadRetextured;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Renders vanilla door/trapdoor geometry with a camouflage particle texture. */
public class SecretMappedModel extends SecretBlockModel {
    public SecretMappedModel(BakedModel fallback) {
        super(fallback);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
                                             @NotNull RandomSource rand, @NotNull ModelData data,
                                             @Nullable RenderType renderType) {
        if (isTrueVisionActive()) {
            if (renderType != RenderType.solid()) return List.of();
            BlockState mappedForIdentifier = data.get(SecretModelData.MODEL_MAP_STATE);
            if (mappedForIdentifier == null) return identifierQuads(state, side, rand, data);
            BakedModel mappedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(mappedForIdentifier);
            List<BakedQuad> source = mappedModel.getQuads(mappedForIdentifier, side, rand, ModelData.EMPTY, null);
            List<BakedQuad> result = new ArrayList<>(source.size());
            for (BakedQuad quad : source) result.add(com.wynprice.secretrooms.client.model.quads.TrueVisionBakedQuad.generateQuad(quad));
            return result;
        }
        BlockState mirrorState = data.get(SecretModelData.SRM_BLOCKSTATE);
        BlockState mappedState = data.get(SecretModelData.MODEL_MAP_STATE);
        if (mirrorState == null || mappedState == null) {
            return super.getQuads(state, side, rand, data, renderType);
        }

        BakedModel textureModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(mirrorState);
        BakedModel shapeModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(mappedState);
        // Use the mapped vanilla door/trapdoor model for vertex positions. The
        // previous approach clamped arbitrary source-block vertices into the
        // target bounding box, which distorted the panel and made its apparent
        // edge disagree with the collision shape. Retexture the actual target
        // quads instead; particle sprites provide a stable single-block texture.
        var camouflageSprite = textureModel.getParticleIcon(ModelData.EMPTY);
        // Preserve the model renderer's directional query. Returning only
        // unculled quads makes vanilla door/trapdoor models disappear because
        // their visible faces are stored in directional buckets.
        // Query the mapped door/trapdoor model without the camouflage block's
        // render layer. Vanilla door models use cutout geometry, while the
        // copied block may be solid (for example stone). Passing the copied
        // block's solid layer filters out every panel quad and leaves only the
        // collision/outline shape visible. The secret block's own render type
        // is still selected by getRenderTypes(); only geometry retrieval is
        // layer-independent here.
        List<BakedQuad> shapeQuads = new ArrayList<>(
                shapeModel.getQuads(mappedState, side, rand, ModelData.EMPTY, null));
        List<BakedQuad> output = new ArrayList<>(shapeQuads.size());
        for (BakedQuad quad : shapeQuads) {
            output.add(new NoTintBakedQuadRetextured(quad, camouflageSprite));
        }
        return output;
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand,
                                                       @NotNull ModelData data) {
        if (isTrueVisionActive()) return ChunkRenderTypeSet.of(RenderType.solid());
        return super.getRenderTypes(state, rand, data);
    }

}
