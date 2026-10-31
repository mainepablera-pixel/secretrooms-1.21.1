# Rendering and interaction fixes (pre-Phase 5)

This package applies a source-level repair pass for the reported Minecraft 1.21.1 NeoForge issues.

## Changes

- Door manual toggling no longer treats `OPEN != POWERED` as evidence of a redstone change. Only a change in the stored `POWERED` state forces the door's open state to follow redstone, preventing an ordinary click from immediately closing the door again.
- Door open-panel hinge rotation and trapdoor open-panel geometry were corrected to align the thin collision/outline panel with the facing and hinge state.
- One-way glass now explicitly requests model-data refresh and a block update after changing a face property.
- Mirrored block models now receive `ModelData.EMPTY`, avoiding secret-block model properties leaking into the copied block's model queries.
- True Vision now overlays only the quads selected for the current solid render pass, rather than querying and appending additional quads from unrelated render passes. This is intended to prevent doubled geometry and visual interference.
- Secret chest block entities now expose their camouflage state through model data, refresh it when set/loaded, and participate in the common mirror-state lookup. The placement item initializes chest camouflage as well as ordinary secret-block camouflage.
- Torch lever and wall torch lever were added to the secret-block model/color handling list.
- Ghost-block model data now checks the connected ghost-block component around a block. When the player's bounding box intersects any ghost block in that connected component, all blocks in that component are hidden. The client refreshes nearby secret-block meshes when the player changes block position.

## Verification status

- ZIP structure and resource JSON validation should be run after packaging.
- Compilation was attempted with `./gradlew compileJava --no-daemon --console=plain` but could not start because this environment cannot resolve `services.gradle.org` while downloading Gradle 9.2.1 (`UnknownHostException`).
- Therefore these are source-level changes, not a claim of successful compilation or gameplay verification. Run the GitHub Actions workflow and test the listed behaviors in a client before treating the fixes as confirmed.

## Required in-game checks

1. Open/close wooden and iron doors by clicking either half; test both hinge sides and redstone transitions.
2. Open/close trapdoors in all four facing directions and verify the outline/collision panel matches the rendered panel.
3. Toggle each one-way-glass face and confirm only that face changes; verify the other copied faces remain visible.
4. Place chests against multiple camouflage blocks, reload the chunk, and verify the chest texture follows the intended copied block without duplicate geometry.
5. Equip True Vision and inspect cube, stairs, doors, trapdoors, glass, chest, and torch-lever camouflage for a stable identifier with no flicker.
6. Build connected ghost blocks in a line and a 3D cluster; enter any block and verify all connected blocks become invisible, then leave the cluster and verify they return.
