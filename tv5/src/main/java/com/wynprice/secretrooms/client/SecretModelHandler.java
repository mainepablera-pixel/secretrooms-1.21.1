package com.wynprice.secretrooms.client;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.client.model.SecretBlockModel;
import com.wynprice.secretrooms.client.model.SecretMappedModel;
import com.wynprice.secretrooms.client.model.OneWayGlassModel;
import com.wynprice.secretrooms.client.model.quads.TrueVisionBakedQuad;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

import java.util.Map;

@EventBusSubscriber(modid = SecretRooms6.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SecretModelHandler {

    @SubscribeEvent
    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        Block[] secretBlocks = getAllSecretBlocks();

        colors.register((state, level, pos, tintIndex) -> {
            if (level == null || pos == null) return -1;
            return SecretBaseBlock.getMirrorState(level, pos)
                    .map(mirror -> colors.getColor(mirror, level, pos, tintIndex))
                    .orElse(-1);
        }, secretBlocks);
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> models = event.getModels();

        for (Block block : getAllSecretBlocks()) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                ModelResourceLocation mrl = BlockModelShaper.stateToModelLocation(state);
                BakedModel existing = models.get(mrl);
                if (existing != null) {
                    boolean needsMappedGeometry = block == SecretBlocks.SECRET_DOOR.get()
                            || block == SecretBlocks.SECRET_IRON_DOOR.get()
                            || block == SecretBlocks.SECRET_TRAPDOOR.get()
                            || block == SecretBlocks.SECRET_IRON_TRAPDOOR.get();
                    if (needsMappedGeometry) {
                        models.put(mrl, new SecretMappedModel(existing));
                    } else if (block == SecretBlocks.ONE_WAY_GLASS.get()) {
                        BakedModel glassFallback = models.get(
                                BlockModelShaper.stateToModelLocation(Blocks.GLASS.defaultBlockState()));
                        models.put(mrl, new OneWayGlassModel(glassFallback != null ? glassFallback : existing));
                    } else {
                        models.put(mrl, new SecretBlockModel(existing));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTextureAtlasStitched(TextureAtlasStitchedEvent event) {
        if (TrueVisionBakedQuad.isBlockAtlas(event.getAtlas().location())) {
            TrueVisionBakedQuad.setOverlaySprite(event.getAtlas().getSprite(TrueVisionBakedQuad.OVERLAY_LOCATION));
        }
    }

    @SubscribeEvent
    public static void onModelsReady(ModelEvent.BakingCompleted event) {
        BakedModel glass = event.getModelManager().getModel(
                BlockModelShaper.stateToModelLocation(Blocks.GLASS.defaultBlockState()));
        OneWayGlassModel.setGlassModel(glass);
    }

    private static Block[] getAllSecretBlocks() {
        return new Block[]{
                SecretBlocks.GHOST_BLOCK.get(),
                SecretBlocks.SECRET_STAIRS.get(),
                SecretBlocks.SECRET_LEVER.get(),
                SecretBlocks.SECRET_REDSTONE.get(),
                SecretBlocks.ONE_WAY_GLASS.get(),
                SecretBlocks.SECRET_WOODEN_BUTTON.get(),
                SecretBlocks.SECRET_STONE_BUTTON.get(),
                SecretBlocks.SECRET_PRESSURE_PLATE.get(),
                SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get(),
                SecretBlocks.SECRET_DOOR.get(),
                SecretBlocks.SECRET_IRON_DOOR.get(),
                SecretBlocks.SECRET_CHEST.get(),
                SecretBlocks.SECRET_TRAPDOOR.get(),
                SecretBlocks.SECRET_IRON_TRAPDOOR.get(),
                SecretBlocks.SECRET_TRAPPED_CHEST.get(),
                SecretBlocks.SECRET_GATE.get(),
                SecretBlocks.SECRET_DUMMY_BLOCK.get(),
                SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get(),
                SecretBlocks.SECRET_OBSERVER.get(),
                SecretBlocks.SECRET_CLAMBER.get(),
                SecretBlocks.TORCH_LEVER.get(),
                SecretBlocks.WALL_TORCH_LEVER.get()
        };
    }
}
