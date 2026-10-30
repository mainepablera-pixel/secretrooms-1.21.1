# Ghost group compile fix

The GitHub Actions log showed one Java compilation error: `BlockAndTintGetter` does not define `hasChunkAt(BlockPos)` in this Minecraft/NeoForge version. The connected-component traversal now checks whether each neighboring state is a ghost block before enqueuing it, avoiding that unsupported method and avoiding unnecessary traversal through non-ghost positions.

This is a source-level correction. Run the GitHub Actions build again to confirm compilation and then test connected ghost blocks in-game.
