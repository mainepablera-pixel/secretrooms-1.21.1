package com.wynprice.secretrooms.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

/** Client-side connected-component check for the ghost-block transparency effect. */
public final class GhostGroupVisibility {
    private static final int MAX_COMPONENT_BLOCKS = 16384;

    private GhostGroupVisibility() {}

    public static boolean shouldHide(BlockAndTintGetter level, BlockPos start) {
        Player player = Minecraft.getInstance().player;
        if (player == null || level == null) return false;

        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(start.immutable());
        while (!queue.isEmpty()) {
            // Bound work per model-data query. On overflow, do not hide the
            // component merely because it is large: that made huge groups
            // invisible even to players standing outside them. Any visited
            // position intersecting the player has already returned true.
            // Very large groups still need in-game stress testing because a
            // bounded traversal cannot guarantee discovery of every member.
            if (visited.size() >= MAX_COMPONENT_BLOCKS) return false;
            BlockPos pos = queue.removeFirst();
            if (!visited.add(pos)) continue;
            BlockState state = level.getBlockState(pos);
            if (!state.is(com.wynprice.secretrooms.server.blocks.SecretBlocks.GHOST_BLOCK.get())) continue;
            if (player.getBoundingBox().intersects(new AABB(pos))) return true;
            for (Direction direction : Direction.values()) {
                BlockPos next = pos.relative(direction);
                if (!visited.contains(next)
                        && level.getBlockState(next).is(com.wynprice.secretrooms.server.blocks.SecretBlocks.GHOST_BLOCK.get())) {
                    queue.addLast(next);
                }
            }
        }
        return false;
    }
}
