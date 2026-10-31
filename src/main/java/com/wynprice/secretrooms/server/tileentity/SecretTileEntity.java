package com.wynprice.secretrooms.server.tileentity;

import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.server.blocks.SecretDoor;
import com.wynprice.secretrooms.server.blocks.SecretTrapdoor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public class SecretTileEntity extends BlockEntity {

    private BlockState mirrorState = Blocks.STONE.defaultBlockState();
    @Nullable
    private BlockPos gateOwner;

    public SecretTileEntity(BlockPos pos, BlockState state) {
        super(SecretTileEntities.SECRET_TILE_ENTITY.get(), pos, state);
    }

    public BlockState getMirrorState() {
        return mirrorState;
    }

    /** Records which gate created a dummy segment, preventing unrelated gates from deleting it. */
    @Nullable
    public BlockPos getGateOwner() {
        return gateOwner;
    }

    public void setGateOwner(@Nullable BlockPos owner) {
        this.gateOwner = owner == null ? null : owner.immutable();
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setMirrorState(BlockState state) {
        this.mirrorState = state == null ? Blocks.STONE.defaultBlockState() : state;
        setChanged();
        requestModelDataUpdate();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public ModelData getModelData() {
        ModelData.Builder builder = ModelData.builder()
                .with(SecretModelData.SRM_BLOCKSTATE, this.mirrorState);

        // Door/trapdoor camouflage must use the vanilla thin model's geometry,
        // while retaining the mirrored block as the source of its texture quads.
        BlockState ownState = getBlockState();
        if (ownState.getBlock() instanceof SecretDoor) {
            BlockState mapped = Blocks.OAK_DOOR.defaultBlockState()
                    .setValue(DoorBlock.FACING, ownState.getValue(SecretDoor.FACING))
                    .setValue(DoorBlock.OPEN, ownState.getValue(SecretDoor.OPEN))
                    .setValue(DoorBlock.HINGE, ownState.getValue(SecretDoor.HINGE))
                    .setValue(DoorBlock.POWERED, ownState.getValue(SecretDoor.POWERED))
                    .setValue(DoorBlock.HALF, ownState.getValue(SecretDoor.HALF));
            builder.with(SecretModelData.MODEL_MAP_STATE, mapped);
        } else if (ownState.getBlock() instanceof SecretTrapdoor) {
            BlockState mapped = Blocks.OAK_TRAPDOOR.defaultBlockState()
                    .setValue(TrapDoorBlock.FACING, ownState.getValue(SecretTrapdoor.FACING))
                    .setValue(TrapDoorBlock.OPEN, ownState.getValue(SecretTrapdoor.OPEN))
                    .setValue(TrapDoorBlock.HALF, ownState.getValue(SecretTrapdoor.HALF))
                    .setValue(TrapDoorBlock.POWERED, ownState.getValue(SecretTrapdoor.POWERED))
                    .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED, ownState.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED));
            builder.with(SecretModelData.MODEL_MAP_STATE, mapped);
        }
        return builder.build();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Mirror", NbtUtils.writeBlockState(mirrorState));
        if (gateOwner != null) tag.putLong("GateOwner", gateOwner.asLong());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.gateOwner = tag.contains("GateOwner") ? BlockPos.of(tag.getLong("GateOwner")).immutable() : null;
        if (tag.contains("Mirror")) {
            this.mirrorState = NbtUtils.readBlockState(
                    registries.lookupOrThrow(Registries.BLOCK),
                    tag.getCompound("Mirror"));
            if (level != null && level.isClientSide) {
                requestModelDataUpdate();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 8);
            }
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
