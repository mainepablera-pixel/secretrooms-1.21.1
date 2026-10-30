package com.wynprice.secretrooms.server.tileentity;

import com.mojang.datafixers.types.Type;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SecretTileEntities {
   public static final DeferredRegister<BlockEntityType<?>> REGISTRY;
   public static final Supplier<BlockEntityType<SecretTileEntity>> SECRET_TILE_ENTITY;
   public static final Supplier<BlockEntityType<SecretChestTileEntity>> SECRET_CHEST_TILE_ENTITY;
   public static final Supplier<BlockEntityType<SecretDaylightDetectorTileEntity>> SECRET_DAYLIGHT_DETECTOR_TILE_ENTITY;

   static {
      REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "secretroomsmod");
      SECRET_TILE_ENTITY = REGISTRY.register("secret_tile_entity", () -> Builder.m_155273_(SecretTileEntity::new, new Block[]{(Block)SecretBlocks.GHOST_BLOCK.get(), (Block)SecretBlocks.SECRET_STAIRS.get(), (Block)SecretBlocks.SECRET_LEVER.get(), (Block)SecretBlocks.SECRET_REDSTONE.get(), (Block)SecretBlocks.ONE_WAY_GLASS.get(), (Block)SecretBlocks.SECRET_WOODEN_BUTTON.get(), (Block)SecretBlocks.SECRET_STONE_BUTTON.get(), (Block)SecretBlocks.SECRET_PRESSURE_PLATE.get(), (Block)SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get(), (Block)SecretBlocks.SECRET_DOOR.get(), (Block)SecretBlocks.SECRET_IRON_DOOR.get(), (Block)SecretBlocks.SECRET_TRAPDOOR.get(), (Block)SecretBlocks.SECRET_IRON_TRAPDOOR.get(), (Block)SecretBlocks.SECRET_OBSERVER.get(), (Block)SecretBlocks.SECRET_CLAMBER.get()}).m_58966_((Type)null));
      SECRET_CHEST_TILE_ENTITY = REGISTRY.register("secret_chest_tile_entity", () -> Builder.m_155273_(SecretChestTileEntity::new, new Block[]{(Block)SecretBlocks.SECRET_CHEST.get(), (Block)SecretBlocks.SECRET_TRAPPED_CHEST.get()}).m_58966_((Type)null));
      SECRET_DAYLIGHT_DETECTOR_TILE_ENTITY = REGISTRY.register("secret_daylight_detector_tile_entity", () -> Builder.m_155273_(SecretDaylightDetectorTileEntity::new, new Block[]{(Block)SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get()}).m_58966_((Type)null));
   }
}
