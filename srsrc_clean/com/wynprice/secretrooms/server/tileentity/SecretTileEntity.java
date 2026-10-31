package com.wynprice.secretrooms.server.tileentity;

import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class SecretTileEntity extends BlockEntity {
   private final SecretData data = new SecretData(this);

   public SecretTileEntity(BlockPos p_155115_, BlockState p_155116_) {
      super((BlockEntityType)SecretTileEntities.SECRET_TILE_ENTITY.get(), p_155115_, p_155116_);
   }

   public SecretTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   public void m_183515_(CompoundTag compound) {
      compound.m_128365_("secret_data", this.data.writeNBT(new CompoundTag()));
   }

   public void m_142466_(CompoundTag nbt) {
      super.m_142466_(nbt);
      this.data.readNBT(nbt.m_128469_("secret_data"));
   }

   public CompoundTag m_5995_() {
      return this.m_187482_();
   }

   public void handleUpdateTag(CompoundTag tag) {
      this.m_142466_(tag);
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.m_195642_(this, (be) -> this.data.writeNBT(new CompoundTag()));
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
      this.data.readNBT(pkt.m_131708_());
   }

   @Nonnull
   public ModelData getModelData() {
      if (this.f_58859_) {
         return super.getModelData();
      } else {
         ModelData.Builder builder = ModelData.builder().with(SecretModelData.SRM_BLOCKSTATE, this.data.getBlockState());
         BlockState state = this.f_58857_.m_8055_(this.f_58858_);
         if (state.m_60734_() instanceof SecretBaseBlock) {
            ((SecretBaseBlock)state.m_60734_()).applyExtraModelData(this.f_58857_, this.f_58858_, state, builder);
         }

         return builder.build();
      }
   }

   public SecretData getData() {
      return this.data;
   }
}
