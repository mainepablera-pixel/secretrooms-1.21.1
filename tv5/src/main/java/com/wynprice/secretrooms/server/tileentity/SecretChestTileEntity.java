package com.wynprice.secretrooms.server.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.wynprice.secretrooms.client.SecretModelData;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public class SecretChestTileEntity extends BlockEntity implements Container, MenuProvider {

    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private BlockState mirrorState = net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState();
    private int openCount;

    public SecretChestTileEntity(BlockPos pos, BlockState state) {
        super(SecretTileEntities.SECRET_CHEST_TILE_ENTITY.get(), pos, state);
    }

    public BlockState getMirrorState() {
        return mirrorState;
    }

    public void setMirrorState(BlockState state) {
        this.mirrorState = state == null ? net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState() : state;
        setChanged();
        requestModelDataUpdate();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(SecretModelData.SRM_BLOCKSTATE, mirrorState).build();
    }

    @Override
    public int getContainerSize() { return 27; }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) { return this.items.get(slot); }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(this.items, slot, amount);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void startOpen(Player player) {
        if (player.isSpectator()) return;
        openCount = Math.min(15, openCount + 1);
        updateTrappedChestSignal();
    }

    @Override
    public void stopOpen(Player player) {
        if (player.isSpectator()) return;
        openCount = Math.max(0, openCount - 1);
        updateTrappedChestSignal();
    }

    public int getOpenCount() {
        return openCount;
    }

    private void updateTrappedChestSignal() {
        if (level != null && !level.isClientSide) {
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void clearContent() {
        // NonNullList has a fixed inventory size. Clearing the list itself
        // removes all 27 slots and can break later menu access; empty each slot
        // instead and mark the block entity dirty for persistence.
        for (int slot = 0; slot < this.items.size(); slot++) {
            this.items.set(slot, ItemStack.EMPTY);
        }
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.put("Mirror", net.minecraft.nbt.NbtUtils.writeBlockState(mirrorState));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        if (tag.contains("Mirror")) {
            this.mirrorState = net.minecraft.nbt.NbtUtils.readBlockState(
                    registries.lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK),
                    tag.getCompound("Mirror"));
            requestModelDataUpdate();
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.chest");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return ChestMenu.threeRows(id, inv, this);
    }
}
