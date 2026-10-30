package com.wynprice.secretrooms.server.items;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;

/** Wearable head-slot goggles; the client model layer adds the true-vision overlay. */
public class TrueVisionGoggles extends ArmorItem {
    public TrueVisionGoggles(Properties properties) {
        super(ArmorMaterials.LEATHER, Type.HELMET, properties);
    }
}
