package com.wynprice.secretrooms.server.tileentity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class SecretChestTileEntity extends SecretTileEntity implements Container, MenuProvider {
   private final ItemStackHandler handler = new ItemStackHandler(27);
   private int numPlayersUsing;

   public SecretChestTileEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)SecretTileEntities.SECRET_CHEST_TILE_ENTITY.get(), pos, state);
   }

   public void m_142466_(CompoundTag nbt) {
      this.handler.deserializeNBT(nbt.m_128469_("Items"));
      super.m_142466_(nbt);
   }

   public void m_183515_(CompoundTag compound) {
      compound.m_128365_("Items", this.handler.serializeNBT());
      super.m_183515_(compound);
   }

   public Component m_5446_() {
      return Component.m_237115_("secretroomsmod.container.secretchest.name");
   }

   @Nullable
   public AbstractContainerMenu m_7208_(int id, Inventory inv, Player player) {
      return ChestMenu.m_39237_(id, inv, this);
   }

   public void m_5856_(Player player) {
      if (!player.m_5833_()) {
         if (this.numPlayersUsing < 0) {
            this.numPlayersUsing = 0;
         }

         ++this.numPlayersUsing;
         this.onOpenOrClose();
      }

   }

   public void m_5785_(Player player) {
      if (!player.m_5833_()) {
         --this.numPlayersUsing;
         this.onOpenOrClose();
      }

   }

   private void onOpenOrClose() {
      this.f_58857_.m_46672_(this.f_58858_, this.m_58900_().m_60734_());
      this.f_58857_.m_46672_(this.f_58858_.m_7495_(), this.m_58900_().m_60734_());
   }

   public int m_6643_() {
      return 27;
   }

   public boolean m_7983_() {
      boolean value = true;

      for(int i = 0; i < this.handler.getSlots(); ++i) {
         value &= this.handler.getStackInSlot(i).m_41619_();
      }

      return value;
   }

   public ItemStack m_8020_(int index) {
      return this.handler.getStackInSlot(index);
   }

   public ItemStack m_7407_(int index, int count) {
      return this.handler.getStackInSlot(index).m_41620_(count);
   }

   public ItemStack m_8016_(int index) {
      ItemStack slot = this.handler.getStackInSlot(index);
      this.handler.setStackInSlot(index, ItemStack.f_41583_);
      return slot;
   }

   public void m_6836_(int index, ItemStack stack) {
      this.handler.setStackInSlot(index, stack);
   }

   public boolean m_6542_(Player player) {
      return true;
   }

   public void m_6211_() {
      for(int i = 0; i < this.handler.getSlots(); ++i) {
         this.handler.setStackInSlot(i, ItemStack.f_41583_);
      }

   }

   public int getNumPlayersUsing() {
      return this.numPlayersUsing;
   }
}
