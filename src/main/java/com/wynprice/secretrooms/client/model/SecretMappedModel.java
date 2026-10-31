package com.wynprice.secretrooms.client.model;

import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.client.model.quads.MirrorRetexturedQuad;
import com.wynprice.secretrooms.client.model.quads.NoTintBakedQuadRetextured;
import com.wynprice.secretrooms.server.blocks.SecretDoor;
import com.wynprice.secretrooms.server.blocks.SecretTrapdoor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
    protected List<BakedQuad> camouflageQuads(@Nullable BlockState state, @Nullable Direction side,
                                              @NotNull RandomSource rand, @NotNull ModelData data,
                                              @Nullable RenderType renderType) {
        BlockState mirrorState = data.get(SecretModelData.SRM_BLOCKSTATE);
        BlockState mappedState = resolveMappedState(state, data);
        if (mirrorState == null || mappedState == null) {
            return super.camouflageQuads(state, side, rand, data, renderType);
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
            // Pick the copied block's texture for THIS face (grass: top = grass, sides =
            // grass-side + overlay, bottom = dirt) rather than one particle sprite for the
            // whole door, and keep each source quad's tint index so leaves/grass stay coloured.
            List<BakedQuad> faceSources = sourceQuadsForFace(textureModel, mirrorState, quad.getDirection(), rand);
            if (faceSources.isEmpty()) {
                output.add(new NoTintBakedQuadRetextured(quad, camouflageSprite));
            } else {
                for (BakedQuad source : faceSources) {
                    output.add(new MirrorRetexturedQuad(quad, source.getSprite(), source.getTintIndex()));
                }
            }
        }
        return output;
    }

    /** The copied block's quads that face {@code face}, culled or not, in all render layers. */
    private static List<BakedQuad> sourceQuadsForFace(BakedModel textureModel, BlockState mirrorState,
                                                      Direction face, RandomSource rand) {
        List<BakedQuad> culled = textureModel.getQuads(mirrorState, face, rand, ModelData.EMPTY, null);
        if (!culled.isEmpty()) return culled;
        List<BakedQuad> result = new ArrayList<>();
        for (BakedQuad candidate : textureModel.getQuads(mirrorState, null, rand, ModelData.EMPTY, null)) {
            if (candidate.getDirection() == face) result.add(candidate);
        }
        return result;
    }

    /**
     * The vanilla door/trapdoor state whose baked geometry this block should use.
     *
     * <p>Derived from the block state the renderer actually passes in, so an open/close,
     * hinge or power change is reflected immediately. The value cached in ModelData is only
     * refreshed when the block entity asks for a model-data update, and can lag behind the
     * real state (which is how a door could be shaped one way but drawn another). ModelData
     * is kept as a fallback for callers that pass a null state.</p>
     */
    @Nullable
    private static BlockState resolveMappedState(@Nullable BlockState state, ModelData data) {
        if (state != null) {
            if (state.getBlock() instanceof SecretDoor) {
                return Blocks.OAK_DOOR.defaultBlockState()
                        .setValue(DoorBlock.FACING, state.getValue(SecretDoor.FACING))
                        .setValue(DoorBlock.OPEN, state.getValue(SecretDoor.OPEN))
                        .setValue(DoorBlock.HINGE, state.getValue(SecretDoor.HINGE))
                        .setValue(DoorBlock.POWERED, state.getValue(SecretDoor.POWERED))
                        .setValue(DoorBlock.HALF, state.getValue(SecretDoor.HALF));
            }
            if (state.getBlock() instanceof SecretTrapdoor) {
                return Blocks.OAK_TRAPDOOR.defaultBlockState()
                        .setValue(TrapDoorBlock.FACING, state.getValue(SecretTrapdoor.FACING))
                        .setValue(TrapDoorBlock.OPEN, state.getValue(SecretTrapdoor.OPEN))
                        .setValue(TrapDoorBlock.HALF, state.getValue(SecretTrapdoor.HALF))
                        .setValue(TrapDoorBlock.POWERED, state.getValue(SecretTrapdoor.POWERED))
                        .setValue(BlockStateProperties.WATERLOGGED,
                                state.getValue(BlockStateProperties.WATERLOGGED));
            }
        }
        return data.get(SecretModelData.MODEL_MAP_STATE);
    }
}
