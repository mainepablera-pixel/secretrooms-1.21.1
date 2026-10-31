package com.wynprice.secretrooms.client.model;

import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.client.GhostGroupVisibility;
import com.wynprice.secretrooms.server.blocks.GhostBlock;
import com.wynprice.secretrooms.server.blocks.TorchLever;
import com.wynprice.secretrooms.server.blocks.WallTorchLever;
import com.wynprice.secretrooms.client.model.quads.TrueVisionBakedQuad;
import com.wynprice.secretrooms.client.model.quads.NoTintBakedQuadRetextured;
import com.wynprice.secretrooms.server.items.SecretItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * Baked model that delegates all rendering to the mirrored block's model.
 * The mirror state is supplied via ModelData from SecretTileEntity.
 */
public class SecretBlockModel implements BakedModel {

    private final BakedModel fallback;

    public SecretBlockModel(BakedModel fallback) {
        this.fallback = fallback;
    }

    protected Optional<BlockState> getMirrorState(ModelData data) {
        return Optional.ofNullable(data.get(SecretModelData.SRM_BLOCKSTATE));
    }

    protected Optional<BakedModel> getMirrorModel(ModelData data) {
        return getMirrorState(data).map(state ->
                Minecraft.getInstance().getBlockRenderer().getBlockModel(state));
    }

    /**
     * Preserve model data produced by the wrapped model without dropping the
     * secret block entity's mirror/mapped-state properties. Some wrapped models
     * return a derived ModelData instance, so relying on the default method can
     * silently lose the data needed to render camouflage.
     */
    @Override
    public ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos,
                                  @NotNull BlockState state, @NotNull ModelData tileData) {
        ModelData wrappedData = fallback.getModelData(level, pos, state, tileData);
        ModelData.Builder builder = wrappedData.derive();
        if (tileData.has(SecretModelData.SRM_BLOCKSTATE)
                && !wrappedData.has(SecretModelData.SRM_BLOCKSTATE)) {
            builder.with(SecretModelData.SRM_BLOCKSTATE, tileData.get(SecretModelData.SRM_BLOCKSTATE));
        }
        if (tileData.has(SecretModelData.MODEL_MAP_STATE)
                && !wrappedData.has(SecretModelData.MODEL_MAP_STATE)) {
            builder.with(SecretModelData.MODEL_MAP_STATE, tileData.get(SecretModelData.MODEL_MAP_STATE));
        }
        if (state != null && state.getBlock() instanceof GhostBlock) {
            builder.with(SecretModelData.GHOST_GROUP_HIDDEN, GhostGroupVisibility.shouldHide(level, pos));
        }
        return builder.build();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
                                             @NotNull RandomSource rand, @NotNull ModelData data,
                                             @Nullable RenderType renderType) {
        // Torch levers are ordinary visible torches (they have no block entity and
        // no camouflage). Emit the vanilla torch geometry untouched: no True Vision
        // marker, no mirror lookup.
        if (isTorchLever(state)) {
            return fallback.getQuads(state, side, rand, data, renderType);
        }
        if (Boolean.TRUE.equals(data.get(SecretModelData.GHOST_GROUP_HIDDEN))) {
            return List.of();
        }

        // Normal camouflage, only in the layers the camouflage itself uses.
        List<BakedQuad> result = new ArrayList<>();
        if (renderType == null || baseRenderTypes(state, rand, data).contains(renderType)) {
            result.addAll(camouflageQuads(state, side, rand, data, renderType));
        }

        // True Vision (same as the original mod): the camouflage is still drawn, and
        // the animated overlay is added on top of every face in the translucent layer.
        if (isTrueVisionActive() && renderType == RenderType.translucent() && TrueVisionBakedQuad.isReady()) {
            for (BakedQuad quad : camouflageQuads(state, side, rand, data, null)) {
                result.add(TrueVisionBakedQuad.generateQuad(quad));
            }
        }
        return result;
    }

    /** The block's normal (non-True-Vision) quads. Subclasses override this, not getQuads. */
    protected List<BakedQuad> camouflageQuads(@Nullable BlockState state, @Nullable Direction side,
                                              @NotNull RandomSource rand, @NotNull ModelData data,
                                              @Nullable RenderType renderType) {
        Optional<BakedModel> mirrorModel = getMirrorModel(data);
        if (mirrorModel.isEmpty()) {
            return fallback.getQuads(state, side, rand, data, renderType);
        }

        BlockState mirror = getMirrorState(data).orElse(state);
        BakedModel model = mirrorModel.get();

        // Ask for source geometry without applying the secret block's render layer.
        // The selected render layers are already delegated by getRenderTypes(); passing
        // the wrapper's layer here can suppress cutout torch/flame geometry when the
        // source and fallback models have different render-layer requirements.
        List<BakedQuad> quads = new ArrayList<>(model.getQuads(mirror, side, rand, ModelData.EMPTY, null));
        // Chest blocks have a special block-entity renderer in vanilla, so
        // their baked model quads are not suitable camouflage geometry. Use the
        // secret chest's cube silhouette and the copied chest's particle sprite
        // instead of importing unrelated/empty chest-model quads.
        if (mirror.getBlock() == Blocks.CHEST || mirror.getBlock() == Blocks.TRAPPED_CHEST) {
            TextureAtlasSprite sprite = model.getParticleIcon(ModelData.EMPTY);
            List<BakedQuad> cube = fallback.getQuads(state, side, rand, data, renderType);
            List<BakedQuad> retextured = new ArrayList<>(cube.size());
            for (BakedQuad quad : cube) retextured.add(new NoTintBakedQuadRetextured(quad, sprite));
            return retextured;
        }
        return quads;
    }

    // Legacy path (some code still calls the non-ModelData version)
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return fallback.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return fallback.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return fallback.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return fallback.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return fallback.getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        return getMirrorModel(data)
                .map(m -> m.getParticleIcon(data))
                .orElse(fallback.getParticleIcon(data));
    }

    protected static boolean isTorchLever(@Nullable BlockState state) {
        return state != null
                && (state.getBlock() instanceof TorchLever || state.getBlock() instanceof WallTorchLever);
    }

    protected static boolean isTrueVisionActive() {
        Player player = Minecraft.getInstance().player;
        return player != null && player.getItemBySlot(EquipmentSlot.HEAD).is(SecretItems.TRUE_VISION_GOGGLES.get());
    }

    @Override
    public ItemTransforms getTransforms() {
        return fallback.getTransforms();
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand,
                                                      @NotNull ModelData data) {
        // The vanilla torch model is two full 16x16 crossed planes whose texture is
        // mostly transparent. Torch lever blocks are not in vanilla's cutout list, so
        // without this they default to the SOLID layer, where the transparent pixels
        // are drawn black (a large black cross/star around the torch). Always render
        // them in the CUTOUT layer, goggles or not.
        if (isTorchLever(state)) {
            return ChunkRenderTypeSet.of(RenderType.cutout());
        }
        ChunkRenderTypeSet base = baseRenderTypes(state, rand, data);
        // True Vision adds the translucent overlay pass on top of the normal layers.
        return isTrueVisionActive()
                ? ChunkRenderTypeSet.union(base, ChunkRenderTypeSet.of(RenderType.translucent()))
                : base;
    }

    /** Render layers of the normal camouflage, without the True Vision overlay layer. */
    protected ChunkRenderTypeSet baseRenderTypes(@Nullable BlockState state, @NotNull RandomSource rand,
                                                 @NotNull ModelData data) {
        return getMirrorState(data)
                .map(mirror -> Minecraft.getInstance().getBlockRenderer().getBlockModel(mirror)
                        .getRenderTypes(mirror, rand, ModelData.EMPTY))
                .orElseGet(() -> state == null ? ChunkRenderTypeSet.of(RenderType.solid())
                        : fallback.getRenderTypes(state, rand, data));
    }

    @Override
    public ItemOverrides getOverrides() {
        return fallback.getOverrides();
    }
}
