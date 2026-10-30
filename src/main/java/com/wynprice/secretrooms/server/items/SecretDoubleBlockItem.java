package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SecretDoubleBlockItem extends DoubleHighBlockItem {
    public SecretDoubleBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        boolean placed = super.placeBlock(context, state);
        if (!placed) return false;

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockPos clicked = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockPos adjacentPos = context.isInside() ? clicked : clicked.relative(face.getOpposite());
        BlockState mirror = level.getBlockState(adjacentPos);
        BlockEntity adjacentEntity = level.getBlockEntity(adjacentPos);
        if (adjacentEntity instanceof SecretTileEntity secretTe) {
            mirror = secretTe.getMirrorState();
        }
        if (mirror.isAir() || (mirror.getBlock() instanceof SecretBaseBlock
                && !(adjacentEntity instanceof SecretTileEntity))) {
            mirror = Blocks.STONE.defaultBlockState();
        }

        // Double-high secret blocks (notably doors) create a block entity for
        // each half. Initialize both halves with the same camouflage state;
        // otherwise the upper half falls back to the placeholder texture after
        // placement or a chunk reload.
        BlockPos[] positions = {pos, pos.above()};
        for (BlockPos halfPos : positions) {
            if (level.getBlockEntity(halfPos) instanceof SecretTileEntity te) {
                te.setMirrorState(mirror);
                te.requestModelDataUpdate();
                BlockState halfState = level.getBlockState(halfPos);
                level.sendBlockUpdated(halfPos, halfState, halfState, 3);
            }
        }
        return true;
    }
}
