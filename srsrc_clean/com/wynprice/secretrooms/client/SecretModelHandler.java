package com.wynprice.secretrooms.client;

import com.google.common.collect.UnmodifiableIterator;
import com.wynprice.secretrooms.client.model.OneWayGlassModel;
import com.wynprice.secretrooms.client.model.SecretBlockModel;
import com.wynprice.secretrooms.client.model.SecretMappedModel;
import com.wynprice.secretrooms.client.model.TrueVisionGogglesModel;
import com.wynprice.secretrooms.client.world.DelegateWorld;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public class SecretModelHandler {
   public static void onEntityModelRegistered(EntityRenderersEvent.RegisterLayerDefinitions event) {
      event.registerLayerDefinition(TrueVisionGogglesModel.TRUE_VISION_GOGGLES_MODEL, () -> LayerDefinition.m_171565_(TrueVisionGogglesModel.createMesh(CubeDeformation.f_171458_, 0.0F), 64, 32));
   }

   public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
      BlockColors colors = event.getBlockColors();
      colors.m_92589_((state, world, pos, index) -> (Integer)SecretBaseBlock.getMirrorState(world, pos).map(DelegateWorld.createFunction(world, (reader, mirror) -> colors.m_92577_(mirror, reader, pos, index))).orElse(-1), new Block[]{(Block)SecretBlocks.GHOST_BLOCK.get(), (Block)SecretBlocks.SECRET_STAIRS.get(), (Block)SecretBlocks.SECRET_LEVER.get(), (Block)SecretBlocks.SECRET_REDSTONE.get(), (Block)SecretBlocks.ONE_WAY_GLASS.get(), (Block)SecretBlocks.SECRET_WOODEN_BUTTON.get(), (Block)SecretBlocks.SECRET_STONE_BUTTON.get(), (Block)SecretBlocks.SECRET_PRESSURE_PLATE.get(), (Block)SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get(), (Block)SecretBlocks.SECRET_DOOR.get(), (Block)SecretBlocks.SECRET_IRON_DOOR.get(), (Block)SecretBlocks.SECRET_CHEST.get(), (Block)SecretBlocks.SECRET_TRAPDOOR.get(), (Block)SecretBlocks.SECRET_IRON_TRAPDOOR.get(), (Block)SecretBlocks.SECRET_TRAPPED_CHEST.get(), (Block)SecretBlocks.SECRET_GATE.get(), (Block)SecretBlocks.SECRET_DUMMY_BLOCK.get(), (Block)SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get(), (Block)SecretBlocks.SECRET_OBSERVER.get(), (Block)SecretBlocks.SECRET_CLAMBER.get()});
   }

   public static void onModelBaked(ModelEvent.BakingCompleted event) {
      Map<ResourceLocation, BakedModel> registry = event.getModels();
      put(registry, SecretBlockModel::new, (Block)SecretBlocks.GHOST_BLOCK.get(), (Block)SecretBlocks.SECRET_STAIRS.get(), (Block)SecretBlocks.SECRET_LEVER.get(), (Block)SecretBlocks.SECRET_REDSTONE.get(), (Block)SecretBlocks.SECRET_WOODEN_BUTTON.get(), (Block)SecretBlocks.SECRET_STONE_BUTTON.get(), (Block)SecretBlocks.SECRET_PRESSURE_PLATE.get(), (Block)SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get(), (Block)SecretBlocks.SECRET_CHEST.get(), (Block)SecretBlocks.SECRET_TRAPPED_CHEST.get(), (Block)SecretBlocks.SECRET_GATE.get(), (Block)SecretBlocks.SECRET_DUMMY_BLOCK.get(), (Block)SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get(), (Block)SecretBlocks.SECRET_OBSERVER.get(), (Block)SecretBlocks.SECRET_CLAMBER.get());
      put(registry, SecretMappedModel::new, (Block)SecretBlocks.SECRET_DOOR.get(), (Block)SecretBlocks.SECRET_IRON_DOOR.get(), (Block)SecretBlocks.SECRET_TRAPDOOR.get(), (Block)SecretBlocks.SECRET_IRON_TRAPDOOR.get());
      put(registry, OneWayGlassModel::new, (Block)SecretBlocks.ONE_WAY_GLASS.get());
   }

   private static void put(Map<ResourceLocation, BakedModel> registry, Function<BakedModel, BakedModel> creator, Block... blocks) {
      for(Block block : blocks) {
         UnmodifiableIterator var7 = block.m_49965_().m_61056_().iterator();

         while(var7.hasNext()) {
            BlockState state = (BlockState)var7.next();
            registry.put(BlockModelShaper.m_110895_(state), (BakedModel)creator.apply((BakedModel)registry.get(BlockModelShaper.m_110895_(state))));
         }
      }

   }
}
