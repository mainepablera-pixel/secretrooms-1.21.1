package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Predicate;

public class SecretPressurePlate extends AbstractSecretPressurePlateBase {

    private final Predicate<Entity> entityPredicate;

    public SecretPressurePlate(BlockBehaviour.Properties properties) {
        this(properties, false);
    }

    /** @param playersOnly if true, only players trigger it */
    public SecretPressurePlate(BlockBehaviour.Properties properties, boolean playersOnly) {
        super(properties);
        this.entityPredicate = playersOnly
                ? e -> e instanceof Player
                : e -> e instanceof LivingEntity;
    }

    @Override
    protected int getSignalStrength(Level level, BlockPos pos) {
        return level.getEntitiesOfClass(Entity.class, getTouchAABB(pos), this.entityPredicate).isEmpty() ? 0 : 15;
    }

    @Override
    protected void playOnSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
    }

    @Override
    protected void playOffSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.5F);
    }
}
