package com.wynprice.secretrooms.client;

import com.wynprice.secretrooms.SecretRooms6;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = SecretRooms6.MODID, dist = Dist.CLIENT)
public class SecretRoomsClient {
    public SecretRoomsClient(IEventBus modEventBus) {
        SecretRooms6.LOGGER.info("Secret Rooms client initialized");
    }
}
