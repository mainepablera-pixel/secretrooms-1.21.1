package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SecretItems {
    public static final DeferredRegister.Items REGISTRY =
            DeferredRegister.createItems(SecretRooms6.MODID);

    private static Item.Properties props() {
        return new Item.Properties();
    }

    public static final DeferredItem<BlockItem> GHOST_BLOCK =
            REGISTRY.register("ghost_block", () -> new SecretBlockItem(SecretBlocks.GHOST_BLOCK.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_STAIRS =
            REGISTRY.register("secret_stairs", () -> new SecretBlockItem(SecretBlocks.SECRET_STAIRS.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_LEVER =
            REGISTRY.register("secret_lever", () -> new SecretBlockItem(SecretBlocks.SECRET_LEVER.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_REDSTONE =
            REGISTRY.register("secret_redstone", () -> new SecretBlockItem(SecretBlocks.SECRET_REDSTONE.get(), props()));

    public static final DeferredItem<BlockItem> ONE_WAY_GLASS =
            REGISTRY.register("one_way_glass", () -> new SecretBlockItem(SecretBlocks.ONE_WAY_GLASS.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_WOODEN_BUTTON =
            REGISTRY.register("secret_wooden_button", () -> new SecretBlockItem(SecretBlocks.SECRET_WOODEN_BUTTON.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_STONE_BUTTON =
            REGISTRY.register("secret_stone_button", () -> new SecretBlockItem(SecretBlocks.SECRET_STONE_BUTTON.get(), props()));

    public static final DeferredItem<Item> TORCH_LEVER =
            REGISTRY.register("torch_lever", () -> new StandingAndWallBlockItem(
                    SecretBlocks.TORCH_LEVER.get(), SecretBlocks.WALL_TORCH_LEVER.get(), props(), net.minecraft.core.Direction.DOWN));

    public static final DeferredItem<BlockItem> SECRET_PRESSURE_PLATE =
            REGISTRY.register("secret_pressure_plate", () -> new SecretBlockItem(SecretBlocks.SECRET_PRESSURE_PLATE.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_PLAYER_PRESSURE_PLATE =
            REGISTRY.register("secret_player_pressure_plate", () -> new SecretBlockItem(SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_DOOR =
            REGISTRY.register("secret_door", () -> new SecretDoubleBlockItem(SecretBlocks.SECRET_DOOR.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_IRON_DOOR =
            REGISTRY.register("secret_iron_door", () -> new SecretDoubleBlockItem(SecretBlocks.SECRET_IRON_DOOR.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_CHEST =
            REGISTRY.register("secret_chest", () -> new SecretBlockItem(SecretBlocks.SECRET_CHEST.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_TRAPDOOR =
            REGISTRY.register("secret_trapdoor", () -> new SecretBlockItem(SecretBlocks.SECRET_TRAPDOOR.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_IRON_TRAPDOOR =
            REGISTRY.register("secret_iron_trapdoor", () -> new SecretBlockItem(SecretBlocks.SECRET_IRON_TRAPDOOR.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_TRAPPED_CHEST =
            REGISTRY.register("secret_trapped_chest", () -> new SecretBlockItem(SecretBlocks.SECRET_TRAPPED_CHEST.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_GATE =
            REGISTRY.register("secret_gate", () -> new SecretBlockItem(SecretBlocks.SECRET_GATE.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_DUMMY_BLOCK =
            REGISTRY.register("secret_dummy_block", () -> new BlockItem(SecretBlocks.SECRET_DUMMY_BLOCK.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_DAYLIGHT_DETECTOR =
            REGISTRY.register("secret_daylight_detector", () -> new SecretBlockItem(SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_OBSERVER =
            REGISTRY.register("secret_observer", () -> new SecretBlockItem(SecretBlocks.SECRET_OBSERVER.get(), props()));

    public static final DeferredItem<BlockItem> SECRET_CLAMBER =
            REGISTRY.register("secret_clamber", () -> new SecretBlockItem(SecretBlocks.SECRET_CLAMBER.get(), props()));

    public static final DeferredItem<Item> CAMOUFLAGE_PASTE =
            REGISTRY.register("camouflage_paste", () -> new Item(props()));

    public static final DeferredItem<Item> SWITCH_PROBE =
            REGISTRY.register("switch_probe", () -> new SwitchProbe(props()));

    public static final DeferredItem<TrueVisionGoggles> TRUE_VISION_GOGGLES =
            REGISTRY.register("true_vision_goggles", () -> new TrueVisionGoggles(props().durability(900)));
}
