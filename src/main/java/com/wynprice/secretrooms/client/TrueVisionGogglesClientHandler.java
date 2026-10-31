package com.wynprice.secretrooms.client;

import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

/** Refreshes loaded secret-block sections when the goggles are equipped or removed. */
@EventBusSubscriber(modid = "secretroomsmod", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class TrueVisionGogglesClientHandler {
    private static boolean initialized;
    private static boolean wasWearing;
    private static BlockPos lastPlayerBlockPos;

    private TrueVisionGogglesClientHandler() {}

    @SubscribeEvent
    public static void onRenderFrame(RenderFrameEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || minecraft.levelRenderer == null) {
            initialized = false;
            return;
        }

        boolean wearing = minecraft.player.getItemBySlot(EquipmentSlot.HEAD)
                .is(SecretItems.TRUE_VISION_GOGGLES.get());
        BlockPos currentPlayerPos = minecraft.player.blockPosition().immutable();
        boolean initialRefresh = !initialized;
        if (initialRefresh) {
            initialized = true;
            wasWearing = wearing;
            lastPlayerBlockPos = currentPlayerPos;
            // The player can enter a world or respawn already wearing True Vision
            // goggles. Do not return before scheduling the initial mesh refresh.
            if (!wearing) return;
        }
        boolean equipmentChanged = initialRefresh || wearing != wasWearing;
        boolean playerChangedBlock = lastPlayerBlockPos == null || !lastPlayerBlockPos.equals(currentPlayerPos);
        if (!equipmentChanged && !playerChangedBlock) return;
        wasWearing = wearing;
        lastPlayerBlockPos = currentPlayerPos;

        ClientLevel level = minecraft.level;
        ChunkPos center = new ChunkPos(minecraft.player.blockPosition());
        int distance = minecraft.options.renderDistance().get() + 1;
        for (int chunkX = center.x - distance; chunkX <= center.x + distance; chunkX++) {
            for (int chunkZ = center.z - distance; chunkZ <= center.z + distance; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunk(chunkX, chunkZ, false);
                if (chunk == null) continue;
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (blockEntity instanceof SecretTileEntity) {
                        BlockPos pos = blockEntity.getBlockPos();
                        BlockState state = blockEntity.getBlockState();
                        minecraft.levelRenderer.blockChanged(level, pos, state, state, 8);
                    }
                }
            }
        }
    }
}
