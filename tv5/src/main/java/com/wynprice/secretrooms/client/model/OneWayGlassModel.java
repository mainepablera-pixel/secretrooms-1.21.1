package com.wynprice.secretrooms.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** One-way glass: selected faces use a translucent glass texture; the others show camouflage. */
public class OneWayGlassModel extends SecretBlockModel {
    private static volatile BakedModel glassModel;

    public OneWayGlassModel(BakedModel fallback) {
        super(fallback);
    }

    public static void setGlassModel(BakedModel model) {
        glassModel = model;
    }

    @Override
    protected ChunkRenderTypeSet baseRenderTypes(@Nullable BlockState state, @NotNull RandomSource rand,
                                                 @NotNull ModelData data) {
        ChunkRenderTypeSet camouflage = super.baseRenderTypes(state, rand, data);
        return ChunkRenderTypeSet.union(camouflage, ChunkRenderTypeSet.of(RenderType.translucent()));
    }

    @Override
    protected List<BakedQuad> camouflageQuads(@Nullable BlockState state, @Nullable Direction side,
                                              @NotNull RandomSource rand, @NotNull ModelData data,
                                              @Nullable RenderType renderType) {
        Optional<BlockState> mirrorOptional = getMirrorState(data);
        Optional<BakedModel> modelOptional = getMirrorModel(data);
        if (state == null || mirrorOptional.isEmpty() || modelOptional.isEmpty() || glassModel == null) {
            // The fallback is the vanilla glass model, so missing initial block
            // entity data cannot expose the item-icon texture or a block image.
            return super.camouflageQuads(state, side, rand, data, renderType);
        }

        BlockState mirror = mirrorOptional.get();
        BakedModel mirrorModel = modelOptional.get();
        BlockState glassState = Blocks.GLASS.defaultBlockState();

        // Keep the two render paths separate: configured glass faces must use
        // vanilla glass geometry and its translucent render type, not opaque
        // camouflage quads with their texture merely replaced. Retexturing the
        // mirror geometry made full-cube camouflage blocks appear as block
        // images and could suppress the glass layer entirely.
        List<BakedQuad> camouflageQuads = new ArrayList<>(
                mirrorModel.getQuads(mirror, side, rand, ModelData.EMPTY, renderType));
        List<BakedQuad> glassQuads = new ArrayList<>(
                glassModel.getQuads(glassState, side, rand, ModelData.EMPTY, null));

        // BakedModel.getQuads(state, side, ...) is the authoritative query for
        // a face. Re-querying the unculled list and merging it here duplicates
        // quads for many vanilla models, which makes the glass face appear to
        // flicker or disappear behind a second camouflage face.

        List<BakedQuad> output = new ArrayList<>();
        if (renderType == null || renderType == RenderType.translucent()) {
            for (BakedQuad quad : glassQuads) {
                Direction face = quad.getDirection();
                if (face != null && state.getValue(propertyFor(face))) {
                    output.add(quad);
                }
            }
        }

        // State semantics are deliberate: true means the selected side is glass;
        // false means it displays the mirrored block. Do not reverse this mapping
        // as a rendering workaround: the placement code initializes the side facing
        // the placer to false, and the interaction toggles only the clicked side.
        // Faces whose properties are false show the mirrored block; faces whose
        // properties are true use vanilla glass geometry. Unculled quads are retained.
        if (renderType == null || mirrorModel.getRenderTypes(mirror, rand, ModelData.EMPTY).contains(renderType)) {
            for (BakedQuad quad : camouflageQuads) {
                Direction face = quad.getDirection();
                if (face == null || !state.getValue(propertyFor(face))) {
                    output.add(quad);
                }
            }
        }
        return output;
    }

    private static BooleanProperty propertyFor(Direction direction) {
        return PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
    }
}
