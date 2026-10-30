package com.wynprice.secretrooms.server.blocks;

import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SecretBlocks {
   public static final DeferredRegister<Block> REGISTRY;
   public static final Supplier<Block> GHOST_BLOCK;
   public static final Supplier<Block> SECRET_STAIRS;
   public static final Supplier<Block> SECRET_LEVER;
   public static final Supplier<Block> SECRET_REDSTONE;
   public static final Supplier<Block> ONE_WAY_GLASS;
   public static final Supplier<Block> SECRET_WOODEN_BUTTON;
   public static final Supplier<Block> SECRET_STONE_BUTTON;
   public static final Supplier<Block> TORCH_LEVER;
   public static final Supplier<Block> WALL_TORCH_LEVER;
   public static final Supplier<Block> SECRET_PRESSURE_PLATE;
   public static final Supplier<Block> SECRET_PLAYER_PRESSURE_PLATE;
   public static final Supplier<Block> SECRET_DOOR;
   public static final Supplier<Block> SECRET_IRON_DOOR;
   public static final Supplier<Block> SECRET_CHEST;
   public static final Supplier<Block> SECRET_TRAPDOOR;
   public static final Supplier<Block> SECRET_IRON_TRAPDOOR;
   public static final Supplier<Block> SECRET_TRAPPED_CHEST;
   public static final Supplier<Block> SECRET_GATE;
   public static final Supplier<Block> SECRET_DUMMY_BLOCK;
   public static final Supplier<Block> SECRET_DAYLIGHT_DETECTOR;
   public static final Supplier<Block> SECRET_OBSERVER;
   public static final Supplier<Block> SECRET_CLAMBER;

   static {
      REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "secretroomsmod");
      GHOST_BLOCK = REGISTRY.register("ghost_block", () -> new GhostBlock(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
      SECRET_STAIRS = REGISTRY.register("secret_stairs", () -> new SecretStairs(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
      SECRET_LEVER = REGISTRY.register("secret_lever", () -> new SecretLever(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
      SECRET_REDSTONE = REGISTRY.register("secret_redstone", () -> new SecretRedstone(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
      ONE_WAY_GLASS = REGISTRY.register("one_way_glass", () -> new OneWayGlass(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
      SECRET_WOODEN_BUTTON = REGISTRY.register("secret_wooden_button", () -> new SecretButton(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_(), true));
      SECRET_STONE_BUTTON = REGISTRY.register("secret_stone_button", () -> new SecretButton(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_(), false));
      TORCH_LEVER = REGISTRY.register("torch_lever", () -> new TorchLever(Properties.m_60939_(Material.f_76320_).m_60953_((blockstate) -> 14).m_60955_()));
      WALL_TORCH_LEVER = REGISTRY.register("wall_torch_lever", () -> new WallTorchLever(Properties.m_60939_(Material.f_76320_).m_60953_((blockstate) -> 14).m_60955_()));
      SECRET_PRESSURE_PLATE = REGISTRY.register("secret_pressure_plate", () -> new SecretPressurePlate(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_(), (entity) -> true));
      SECRET_PLAYER_PRESSURE_PLATE = REGISTRY.register("secret_player_pressure_plate", () -> new SecretPressurePlate(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_(), (entity) -> entity instanceof Player));
      SECRET_DOOR = REGISTRY.register("secret_door", () -> new SecretDoor(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
      SECRET_IRON_DOOR = REGISTRY.register("secret_iron_door", () -> new SecretDoor(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL_IRON).m_60955_()));
      SECRET_CHEST = REGISTRY.register("secret_chest", () -> new SecretChest(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
      SECRET_TRAPDOOR = REGISTRY.register("secret_trapdoor", () -> new SecretTrapdoor(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
      SECRET_IRON_TRAPDOOR = REGISTRY.register("secret_iron_trapdoor", () -> new SecretTrapdoor(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL_IRON).m_60955_()));
      SECRET_TRAPPED_CHEST = REGISTRY.register("secret_trapped_chest", () -> new SecretTrappedChest(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
      SECRET_GATE = REGISTRY.register("secret_gate", () -> new SecretGateBlock(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
      SECRET_DUMMY_BLOCK = REGISTRY.register("secret_dummy_block", () -> new SecretBaseBlock(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_().lootFrom(() -> Blocks.f_50016_)));
      SECRET_DAYLIGHT_DETECTOR = REGISTRY.register("secret_daylight_detector", () -> new SecretDaylightDetector(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
      SECRET_OBSERVER = REGISTRY.register("secret_observer", () -> new SecretObserver(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
      SECRET_CLAMBER = REGISTRY.register("secret_clamber", () -> new SecretClamber(Properties.m_60939_(SecretBlocks.Materials.SRM_MATERIAL).m_60955_()));
   }

   public static class Materials {
      public static final Material SRM_MATERIAL;
      public static final Material SRM_MATERIAL_IRON;

      static {
         SRM_MATERIAL = (new Material.Builder(MaterialColor.f_76409_)).m_76359_();
         SRM_MATERIAL_IRON = (new Material.Builder(MaterialColor.f_76404_)).m_76359_();
      }
   }
}
