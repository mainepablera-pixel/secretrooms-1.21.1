package com.wynprice.secretrooms.client;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class SecretModelData {
    /** The mirrored (camouflage) block state used for rendering. */
    public static final ModelProperty<BlockState> SRM_BLOCKSTATE = new ModelProperty<>();

    /** Optional secondary mapped state used by doors/trapdoors/stairs for shape. */
    public static final ModelProperty<BlockState> MODEL_MAP_STATE = new ModelProperty<>();

    /** True while the player occupies any block in this connected ghost-block group. */
    public static final ModelProperty<Boolean> GHOST_GROUP_HIDDEN = new ModelProperty<>();
}
