package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import com.wynprice.secretrooms.server.tileentity.SecretChestTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SecretTrappedChest extends SecretChest {

    public SecretTrappedChest(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof SecretChestTileEntity chest)) return 0;
        return Math.min(15, chest.getOpenCount());
    }
}
