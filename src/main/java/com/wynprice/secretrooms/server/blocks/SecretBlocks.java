package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SecretBlocks {
    public static final DeferredRegister.Blocks REGISTRY =
            DeferredRegister.createBlocks(SecretRooms6.MODID);

    private static BlockBehaviour.Properties secretProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(0.5F)
                .sound(SoundType.STONE)
                .noOcclusion();
    }

    public static final DeferredBlock<Block> GHOST_BLOCK =
            REGISTRY.register("ghost_block", () -> new GhostBlock(secretProps()));

    public static final DeferredBlock<Block> SECRET_STAIRS =
            REGISTRY.register("secret_stairs", () -> new SecretStairs(secretProps()));

    public static final DeferredBlock<Block> SECRET_LEVER =
            REGISTRY.register("secret_lever", () -> new SecretLever(secretProps()));

    public static final DeferredBlock<Block> SECRET_REDSTONE =
            REGISTRY.register("secret_redstone", () -> new SecretRedstone(secretProps()));

    public static final DeferredBlock<Block> ONE_WAY_GLASS =
            REGISTRY.register("one_way_glass", () -> new OneWayGlass(secretProps()));

    public static final DeferredBlock<Block> SECRET_WOODEN_BUTTON =
            REGISTRY.register("secret_wooden_button", () -> new SecretButton(secretProps(), true));

    public static final DeferredBlock<Block> SECRET_STONE_BUTTON =
            REGISTRY.register("secret_stone_button", () -> new SecretButton(secretProps(), false));

    public static final DeferredBlock<Block> TORCH_LEVER =
            REGISTRY.register("torch_lever", () -> new TorchLever(BlockBehaviour.Properties.of()
                    .noCollission().instabreak().sound(SoundType.WOOD).lightLevel(s -> 14)));

    public static final DeferredBlock<Block> WALL_TORCH_LEVER =
            REGISTRY.register("wall_torch_lever", () -> new WallTorchLever(BlockBehaviour.Properties.of()
                    .noCollission().instabreak().sound(SoundType.WOOD).lightLevel(s -> 14).dropsLike(TORCH_LEVER.get())));

    public static final DeferredBlock<Block> SECRET_PRESSURE_PLATE =
            REGISTRY.register("secret_pressure_plate", () -> new SecretPressurePlate(secretProps()));

    public static final DeferredBlock<Block> SECRET_PLAYER_PRESSURE_PLATE =
            REGISTRY.register("secret_player_pressure_plate", () -> new SecretPressurePlate(secretProps(), true));

    public static final DeferredBlock<Block> SECRET_DOOR =
            REGISTRY.register("secret_door", () -> new SecretDoor(secretProps(), false));

    public static final DeferredBlock<Block> SECRET_IRON_DOOR =
            REGISTRY.register("secret_iron_door", () -> new SecretDoor(secretProps().strength(5.0F), true));

    public static final DeferredBlock<Block> SECRET_CHEST =
            REGISTRY.register("secret_chest", () -> new SecretChest(secretProps()));

    public static final DeferredBlock<Block> SECRET_TRAPDOOR =
            REGISTRY.register("secret_trapdoor", () -> new SecretTrapdoor(secretProps(), false));

    public static final DeferredBlock<Block> SECRET_IRON_TRAPDOOR =
            REGISTRY.register("secret_iron_trapdoor", () -> new SecretTrapdoor(secretProps().strength(5.0F), true));

    public static final DeferredBlock<Block> SECRET_TRAPPED_CHEST =
            REGISTRY.register("secret_trapped_chest", () -> new SecretTrappedChest(secretProps()));

    public static final DeferredBlock<Block> SECRET_GATE =
            REGISTRY.register("secret_gate", () -> new SecretGateBlock(secretProps()));

    // The gate's extension blocks must carry camouflage data, just like the
    // original implementation; a plain Block cannot store the mirrored state.
    public static final DeferredBlock<SecretBaseBlock> SECRET_DUMMY_BLOCK =
            REGISTRY.register("secret_dummy_block", () -> new SecretBaseBlock(secretProps()));

    public static final DeferredBlock<Block> SECRET_DAYLIGHT_DETECTOR =
            REGISTRY.register("secret_daylight_detector", () -> new SecretDaylightDetector(secretProps()));

    public static final DeferredBlock<Block> SECRET_OBSERVER =
            REGISTRY.register("secret_observer", () -> new SecretObserver(secretProps()));

    public static final DeferredBlock<Block> SECRET_CLAMBER =
            REGISTRY.register("secret_clamber", () -> new SecretClamber(secretProps()));
}
