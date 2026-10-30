package com.wynprice.secretrooms.server.tileentity;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SecretTileEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SecretRooms6.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecretTileEntity>> SECRET_TILE_ENTITY =
            REGISTRY.register("secret_tile_entity", () ->
                    BlockEntityType.Builder.of(SecretTileEntity::new,
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
                            SecretBlocks.SECRET_TRAPDOOR.get(),
                            SecretBlocks.SECRET_IRON_TRAPDOOR.get(),
                            SecretBlocks.SECRET_OBSERVER.get(),
                            SecretBlocks.SECRET_CLAMBER.get(),
                            SecretBlocks.SECRET_GATE.get(),
                            SecretBlocks.TORCH_LEVER.get(),
                            SecretBlocks.WALL_TORCH_LEVER.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecretChestTileEntity>> SECRET_CHEST_TILE_ENTITY =
            REGISTRY.register("secret_chest_tile_entity", () ->
                    BlockEntityType.Builder.of(SecretChestTileEntity::new,
                            SecretBlocks.SECRET_CHEST.get(),
                            SecretBlocks.SECRET_TRAPPED_CHEST.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecretDaylightDetectorTileEntity>> SECRET_DAYLIGHT_DETECTOR_TILE_ENTITY =
            REGISTRY.register("secret_daylight_detector_tile_entity", () ->
                    BlockEntityType.Builder.of(SecretDaylightDetectorTileEntity::new,
                            SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get()
                    ).build(null));
}
