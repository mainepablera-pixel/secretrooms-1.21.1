package com.wynprice.secretrooms;

import com.mojang.logging.LogUtils;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(SecretRooms6.MODID)
public class SecretRooms6 {
    public static final String MODID = "secretroomsmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SECRET_ROOMS_TAB =
            CREATIVE_TABS.register("secretroomsmod", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.secretroomsmod"))
                    .icon(() -> new ItemStack(SecretItems.CAMOUFLAGE_PASTE.get()))
                    .displayItems((params, output) -> {
                        SecretItems.REGISTRY.getEntries().forEach(entry -> output.accept(entry.get()));
                    })
                    .build());

    public SecretRooms6(IEventBus modEventBus, ModContainer modContainer) {
        SecretBlocks.REGISTRY.register(modEventBus);
        SecretItems.REGISTRY.register(modEventBus);
        SecretTileEntities.REGISTRY.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        LOGGER.info("Secret Rooms 6 (NeoForge 1.21.1) loading");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Secret Rooms 6 common setup complete");
    }
}
