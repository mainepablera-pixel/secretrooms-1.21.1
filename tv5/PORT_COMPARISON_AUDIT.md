# Original-to-port audit summary

Reference: `secretrooms-1.19.2-1.2.0.jar_Decompiler.com.zip` supplied by the user.
Target: NeoForge 1.21.1 project under `src/main/java` and `src/main/resources`.

## Resource comparison retained from the previous audit

- 26 crafting recipes, 26 recipe advancements, 21 block loot tables, and 4 item/block tags are present.
- The project contains the original model, blockstate, texture, and language asset inventories.
- All 156 JSON files parse successfully.

## Source fixes in this pass

- Model data is merged instead of losing `SRM_BLOCKSTATE` or `MODEL_MAP_STATE` when a wrapped model returns derived data.
- Secret door upper/lower halves synchronize camouflage state, redstone state, and model refreshes.
- Secret door and trapdoor shape/collision now combines the camouflage shape with the panel shape, as in the original.
- One-way glass uses a translucent glass layer on configured faces and camouflage quads on the other faces.
- Secret stairs use vanilla precomputed stair shapes and neighbor-dependent corner shapes.
- Secret gate dummy blocks are block entities again, allowing camouflage state to be stored; gate extension/retraction behavior has been restored.
- Secret redstone now carries and propagates a 0–15 signal instead of always emitting 15.
- Torch levers are vanilla torch subclasses with powered state and redstone output, not secret-block subclasses.
- Switch Probe stores and applies a block state using item custom data.
- True Vision Goggles are wearable, the block-model overlay quad path is restored, and loaded secret-block chunks are marked dirty when the goggles are equipped/removed.

## Remaining limitations and required tests

- The Gradle wrapper cannot download Gradle in this environment because DNS resolution for `services.gradle.org` fails. The source has not been compiled here.
- No Minecraft client or dedicated-server launch has been performed.
- True Vision Goggles' overlay refresh and armor appearance need runtime verification.
- Switch Probe does not yet preserve arbitrary block-entity NBT; the original implementation supports more than just block states.
- Gate branching/retraction, redstone propagation, model culling, waterlogging, multiplayer synchronization, loot behavior, and the rest of the mod require gameplay tests.
- This audit records implemented source changes; it does not certify full original feature parity.


## Follow-up issue report: camouflage placement, stacked blocks, one-way glass, sounds

- Fixed the placement overwrite that changed a secret block's inherited camouflage to stone when the block below was another SecretBaseBlock.
- Added client-side model-data initialization in SecretBlockItem and SecretDoubleBlockItem to reduce the initial placeholder flash.
- Changed the one-way glass model's missing-data fallback to vanilla glass rather than the custom item-icon cube.
- Added mirrored-block SoundType delegation to SecretBaseBlock.
- Not yet compiled or tested in Minecraft because Gradle distribution access is unavailable in this environment.

## Follow-up: one-way glass geometry separation

- Updated `OneWayGlassModel` so configured glass faces use the vanilla glass model's own geometry and translucent render path rather than retexturing the mirrored block's quads. This targets the reported behavior where camouflage block images appeared without a proper glass layer.
- Preserved mirrored-block quads for the opposite faces and retained the existing fallback when mirror model data or the baked glass model is unavailable.
- This is a source-level correction only; it still requires compilation and in-game checks for each face direction, transparency, and camouflage blocks with non-cube geometry.

## Sound configuration investigation

- Searched the extracted original Java source and current port for the reported `sound_set_*` and `sound_use_*` configuration keys. Those keys and a configuration implementation were not present in the extracted source tree. The original door/trapdoor sound event IDs are present, but configurable camouflage sounds cannot safely be restored from the current extracted evidence alone.

