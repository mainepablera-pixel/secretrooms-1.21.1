package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import com.wynprice.secretrooms.server.tileentity.SecretChestTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SecretBlockItem extends BlockItem {

    public SecretBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        boolean placed = super.placeBlock(context, state);
        if (!placed) return false;

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        // Initialize the client-side block entity immediately so the placed
        // block does not briefly show its placeholder model while waiting for
        // the server's block-entity update packet.
        BlockPos clicked = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockPos adjacentPos = context.isInside() ? clicked : clicked.relative(face.getOpposite());
        BlockState mirror = level.getBlockState(adjacentPos);

        // Stacking secret blocks must preserve the actual camouflage state,
        // not copy the secret block's own placeholder state.
        BlockEntity be = level.getBlockEntity(adjacentPos);
        if (be instanceof SecretTileEntity secretTe) {
            mirror = secretTe.getMirrorState();
        } else if (be instanceof SecretChestTileEntity chestTe) {
            mirror = chestTe.getMirrorState();
        }
        if (mirror.isAir() || (mirror.getBlock() instanceof SecretBaseBlock
                && !(be instanceof SecretTileEntity) && !(be instanceof SecretChestTileEntity))) {
            mirror = Blocks.STONE.defaultBlockState();
        }

        BlockEntity placedEntity = level.getBlockEntity(pos);
        if (placedEntity instanceof SecretTileEntity te) {
            te.setMirrorState(mirror);
            te.requestModelDataUpdate();
            level.sendBlockUpdated(pos, state, state, 3);
        } else if (placedEntity instanceof SecretChestTileEntity chestTe) {
            chestTe.setMirrorState(mirror);
            chestTe.requestModelDataUpdate();
            level.sendBlockUpdated(pos, state, state, 3);
        }

        return true;
    }
}
