package com.wynprice.secretrooms;

import com.wynprice.secretrooms.client.SecretModelHandler;
import com.wynprice.secretrooms.client.model.OneWayGlassModel;
import com.wynprice.secretrooms.client.model.quads.TrueVisionBakedQuad;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import com.wynprice.secretrooms.server.data.SecretBlockLootTableProvider;
import com.wynprice.secretrooms.server.data.SecretBlockTagsProvider;
import com.wynprice.secretrooms.server.data.SecretItemTagsProvider;
import com.wynprice.secretrooms.server.data.SecretRecipeProvider;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.items.SwitchProbe;
import com.wynprice.secretrooms.server.items.TrueVisionGoggles;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesClientHandler;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesHandler;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntities;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("secretroomsmod")
public class SecretRooms6 {
   public static final String MODID = "secretroomsmod";
   public static final Logger LOGGER = LogManager.getLogger("secretroomsmod");
   public static final CreativeModeTab TAB = new CreativeModeTab(-1, "secretroomsmod") {
      public ItemStack m_6976_() {
         return new ItemStack((ItemLike)SecretItems.CAMOUFLAGE_PASTE.get());
      }
   };
   private boolean recurse = false;

   public SecretRooms6() {
      IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
      IEventBus forgeBus = MinecraftForge.EVENT_BUS;
      bus.addListener(this::gatherData);
      SecretBlocks.REGISTRY.register(bus);
      SecretItems.REGISTRY.register(bus);
      SecretTileEntities.REGISTRY.register(bus);
      bus.addListener(this::onRegisterReloads);
      forgeBus.addListener(this::modifyBreakSpeed);
      forgeBus.addListener(TrueVisionGogglesHandler::onLootTableLoad);
      forgeBus.addListener(TrueVisionGogglesHandler::onPlayerTick);
      DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(SecretModelHandler::onBlockColors);
            bus.addListener(SecretModelHandler::onModelBaked);
            bus.addListener(SecretModelHandler::onEntityModelRegistered);
            bus.addListener(OneWayGlassModel::onModelsReady);
            bus.addListener(TrueVisionBakedQuad::onTextureStitch);
            bus.addListener(TrueVisionBakedQuad::onTextureStitched);
            bus.addListener(SwitchProbe::onRegisterTooltipFactories);
            forgeBus.addListener(SwitchProbe::appendHover);
            forgeBus.addListener(TrueVisionGogglesClientHandler::onClientWorldLoad);
            forgeBus.addListener(TrueVisionGogglesClientHandler::onClientWorldTick);
         });
   }

   public void modifyBreakSpeed(PlayerEvent.BreakSpeed event) {
      if (!this.recurse) {
         this.recurse = true;
         Player player = event.getEntity();
         Optional var10000 = event.getPosition().flatMap((pos) -> SecretBaseBlock.getMirrorState(player.f_19853_, pos).map((mirror) -> player.getDigSpeed(mirror, pos)));
         Objects.requireNonNull(event);
         var10000.ifPresent(event::setNewSpeed);
         this.recurse = false;
      }
   }

   public void gatherData(GatherDataEvent event) {
      DataGenerator gen = event.getGenerator();
      ExistingFileHelper helper = event.getExistingFileHelper();
      gen.m_236039_(event.includeServer(), new SecretRecipeProvider(gen));
      gen.m_236039_(event.includeServer(), new SecretItemTagsProvider(gen, helper));
      gen.m_236039_(event.includeServer(), new SecretBlockTagsProvider(gen, helper));
      gen.m_236039_(event.includeServer(), new SecretBlockLootTableProvider(gen));
   }

   public void onRegisterReloads(RegisterClientReloadListenersEvent event) {
      ResourceManagerReloadListener listener = (rm) -> SecretItems.TRUE_VISION_GOGGLES.ifPresent(TrueVisionGoggles::refreshArmorModel);
      event.registerReloadListener(listener);
   }
}
