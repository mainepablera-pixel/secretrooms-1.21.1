package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import java.util.Objects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SecretItems {
   public static final DeferredRegister<Item> REGISTRY;
   public static final RegistryObject<Item> GHOST_BLOCK;
   public static final RegistryObject<Item> SECRET_STAIRS;
   public static final RegistryObject<Item> SECRET_LEVER;
   public static final RegistryObject<Item> SECRET_REDSTONE;
   public static final RegistryObject<Item> ONE_WAY_GLASS;
   public static final RegistryObject<Item> SECRET_WOODEN_BUTTON;
   public static final RegistryObject<Item> SECRET_STONE_BUTTON;
   public static final RegistryObject<Item> TORCH_LEVER;
   public static final RegistryObject<Item> SECRET_PRESSURE_PLATE;
   public static final RegistryObject<Item> SECRET_PLAYER_PRESSURE_PLATE;
   public static final RegistryObject<Item> SECRET_DOOR;
   public static final RegistryObject<Item> SECRET_IRON_DOOR;
   public static final RegistryObject<Item> SECRET_CHEST;
   public static final RegistryObject<Item> SECRET_TRAPDOOR;
   public static final RegistryObject<Item> SECRET_IRON_TRAPDOOR;
   public static final RegistryObject<Item> SECRET_TRAPPED_CHEST;
   public static final RegistryObject<Item> SECRET_GATE;
   public static final RegistryObject<Item> SECRET_DUMMY_BLOCK;
   public static final RegistryObject<Item> SECRET_DAYLIGHT_DETECTOR;
   public static final RegistryObject<Item> SECRET_OBSERVER;
   public static final RegistryObject<Item> SECRET_CLAMBER;
   public static final RegistryObject<Item> CAMOUFLAGE_PASTE;
   public static final RegistryObject<Item> SWITCH_PROBE;
   public static final RegistryObject<TrueVisionGoggles> TRUE_VISION_GOGGLES;

   private static Item.Properties prop() {
      return (new Item.Properties()).m_41491_(SecretRooms6.TAB);
   }

   static {
      REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, "secretroomsmod");
      GHOST_BLOCK = REGISTRY.register("ghost_block", () -> new SecretBlockItem((Block)SecretBlocks.GHOST_BLOCK.get(), prop()));
      SECRET_STAIRS = REGISTRY.register("secret_stairs", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_STAIRS.get(), prop()));
      SECRET_LEVER = REGISTRY.register("secret_lever", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_LEVER.get(), prop()));
      SECRET_REDSTONE = REGISTRY.register("secret_redstone", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_REDSTONE.get(), prop()));
      ONE_WAY_GLASS = REGISTRY.register("one_way_glass", () -> new SecretBlockItem((Block)SecretBlocks.ONE_WAY_GLASS.get(), prop()));
      SECRET_WOODEN_BUTTON = REGISTRY.register("secret_wooden_button", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_WOODEN_BUTTON.get(), prop()));
      SECRET_STONE_BUTTON = REGISTRY.register("secret_stone_button", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_STONE_BUTTON.get(), prop()));
      TORCH_LEVER = REGISTRY.register("torch_lever", () -> new StandingAndWallBlockItem((Block)Objects.requireNonNull((Block)SecretBlocks.TORCH_LEVER.get()), (Block)Objects.requireNonNull((Block)SecretBlocks.WALL_TORCH_LEVER.get()), prop()));
      SECRET_PRESSURE_PLATE = REGISTRY.register("secret_pressure_plate", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_PRESSURE_PLATE.get(), prop()));
      SECRET_PLAYER_PRESSURE_PLATE = REGISTRY.register("secret_player_pressure_plate", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get(), prop()));
      SECRET_DOOR = REGISTRY.register("secret_door", () -> new SecretDoubleBlockItem((Block)SecretBlocks.SECRET_DOOR.get(), prop()));
      SECRET_IRON_DOOR = REGISTRY.register("secret_iron_door", () -> new SecretDoubleBlockItem((Block)SecretBlocks.SECRET_IRON_DOOR.get(), prop()));
      SECRET_CHEST = REGISTRY.register("secret_chest", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_CHEST.get(), prop()));
      SECRET_TRAPDOOR = REGISTRY.register("secret_trapdoor", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_TRAPDOOR.get(), prop()));
      SECRET_IRON_TRAPDOOR = REGISTRY.register("secret_iron_trapdoor", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_IRON_TRAPDOOR.get(), prop()));
      SECRET_TRAPPED_CHEST = REGISTRY.register("secret_trapped_chest", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_TRAPPED_CHEST.get(), prop()));
      SECRET_GATE = REGISTRY.register("secret_gate", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_GATE.get(), prop()));
      SECRET_DUMMY_BLOCK = REGISTRY.register("secret_dummy_block", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_DUMMY_BLOCK.get(), prop()));
      SECRET_DAYLIGHT_DETECTOR = REGISTRY.register("secret_daylight_detector", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get(), prop()));
      SECRET_OBSERVER = REGISTRY.register("secret_observer", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_OBSERVER.get(), prop()));
      SECRET_CLAMBER = REGISTRY.register("secret_clamber", () -> new SecretBlockItem((Block)SecretBlocks.SECRET_CLAMBER.get(), prop()));
      CAMOUFLAGE_PASTE = REGISTRY.register("camouflage_paste", () -> new Item(prop()));
      SWITCH_PROBE = REGISTRY.register("switch_probe", () -> new SwitchProbe(prop()));
      TRUE_VISION_GOGGLES = REGISTRY.register("true_vision_goggles", () -> new TrueVisionGoggles(prop().m_41503_(900)));
   }
}
