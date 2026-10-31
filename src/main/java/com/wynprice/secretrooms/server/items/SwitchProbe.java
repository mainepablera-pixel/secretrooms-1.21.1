package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Copies a normal block state to the probe, then applies it to secret blocks. */
public class SwitchProbe extends Item {
    private static final String STORED_STATE = "secretroomsmod:probe_state";

    public SwitchProbe(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (level.isClientSide) return InteractionResult.SUCCESS;

        CompoundTag itemTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        boolean hasStoredState = itemTag.get(STORED_STATE) instanceof CompoundTag;
        if (level.getBlockEntity(pos) instanceof SecretTileEntity secret) {
            if (hasStoredState) {
                BlockState stored = NbtUtils.readBlockState(
                        level.registryAccess().lookupOrThrow(Registries.BLOCK),
                        itemTag.getCompound(STORED_STATE));
                secret.setMirrorState(stored);
                if (player != null) {
                    player.displayClientMessage(Component.translatable(
                            "item.secretroomsmod.switch_probe.applied", stored.getBlock().getName()), true);
                }
            } else {
                saveState(stack, secret.getMirrorState());
                if (player != null) {
                    player.displayClientMessage(Component.translatable(
                            "item.secretroomsmod.switch_probe.copied", secret.getMirrorState().getBlock().getName()), true);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // Capture ordinary blocks; a later use on a secret block applies the
        // stored state. The probe remains reusable, matching the original tool.
        saveState(stack, level.getBlockState(pos));
        if (player != null) {
            player.displayClientMessage(Component.translatable(
                    "item.secretroomsmod.switch_probe.copied", level.getBlockState(pos).getBlock().getName()), true);
        }
        return InteractionResult.SUCCESS;
    }

    private static void saveState(ItemStack stack, BlockState state) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag ->
                tag.put(STORED_STATE, NbtUtils.writeBlockState(state)));
    }
}
