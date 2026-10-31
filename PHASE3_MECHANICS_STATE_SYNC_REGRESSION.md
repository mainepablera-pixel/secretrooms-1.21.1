# Three-Phase Repair — Phase 3: Mechanics, State Synchronization, and Regression

## Baseline
Built from the Phase 2 model-geometry/rendering package.

## Source changes in this phase

### 1. Secret chest inventory clearing
`SecretChestTileEntity.clearContent()` no longer clears the `NonNullList` itself. It now sets each slot to `ItemStack.EMPTY` and marks the block entity changed. Clearing the list itself removed the fixed 27-slot structure and could break later inventory/menu access.

### 2. Gate obstruction and stale-segment cleanup
`SecretGateBlock` now removes only segments whose saved owner matches the gate when an obstruction is encountered after the gate has already extended. Retraction scans the full short extension range rather than stopping at the first non-dummy block, so stale owned segments behind an obstruction can be removed. Segments owned by other gates and unowned legacy segments are not removed by this cleanup.

### 3. Ghost-group traversal overflow
When a connected ghost group exceeds the 16,384-position traversal cap, visibility now fails open instead of hiding the group automatically. This prevents a very large group from becoming invisible to players outside it solely because it exceeded the search limit. The bounded traversal still cannot guarantee that every player-inside case in an exceptionally large component is detected; this requires an in-game design/performance test.

## Reviewed but not declared fixed
- One-way glass face interaction still toggles the clicked face between glass and camouflage. If the intended interaction is to change viewing direction while preserving the copied face, that behavior needs a deliberately defined redesign rather than an inferred change.
- Trapped chest signal reads the tile entity's active open count and requests neighbor output updates; actual redstone response needs in-game testing.
- Observer uses scheduled two-tick state transitions and directional neighbor updates; pulse timing and placement need in-game testing.
- Door/trapdoor model alignment, True Vision, torch-lever rendering, and chunk mesh refresh are not confirmed by source inspection alone.

## Validation
- All 158 resource JSON files parsed successfully.
- ZIP archive integrity checked after packaging.
- Attempted `bash gradlew compileJava --no-daemon`; Gradle could not download its distribution because this environment could not resolve `services.gradle.org` (`UnknownHostException`). Java compilation was not reached.
- No Minecraft/NeoForge in-game tests were performed.
