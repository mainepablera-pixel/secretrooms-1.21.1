# Secret Rooms 1.19.2 → NeoForge 1.21.1 repair notes

## Changes made in this repair pass

- Preserved secret block entity model data when a wrapped baked model derives its own `ModelData`.
- Added cached, deterministic bounds for mapped door/trapdoor geometry and kept camouflage quads on the unculled path.
- Kept the upper/lower secret-door mirror state synchronized, and synchronized both door halves' powered/open states on redstone updates.
- Restored the original collision-shape behavior for secret doors and trapdoors by combining the camouflaged block shape with the thin panel shape.
- Restored one-way-glass translucent face rendering and retexturing, including the translucent render layer.
- Restored vanilla stair geometry and neighbor-dependent inner/outer stair shape selection.
- Changed gate extension dummy blocks back to `SecretBaseBlock` instances so they can store camouflage state; added extension/retraction and cleanup behavior.
- Replaced the secret-redstone constant-15 placeholder with a 0–15 wire-style propagation implementation.
- Restored standing and wall torch levers as actual vanilla torch block subclasses with toggled redstone output.
- Reworked Switch Probe to persist a block state in the item's 1.21.1 custom-data component and apply it to secret blocks.
- Made True Vision Goggles wearable in the head slot and restored the client-side overlay quad path.
- Kept all 26 crafting recipes, 26 recipe advancements, 21 loot tables, and existing resource inventory from the previous port.

## Validation completed

- All 156 JSON resources parse successfully.
- All 26 recipes use the 1.21.1 `result.id` output format.
- All 26 recipe advancements remain present.
- Archive integrity is checked after packaging.

## Validation not completed

A full Gradle build could not start because this execution environment cannot resolve `services.gradle.org` (`UnknownHostException`). No compile or in-game test has been performed on this repair pass. The changes therefore remain **source-level repairs, not a verified release build**.

Known items that still require a successful build and gameplay test:

- True Vision Goggles' overlay and automatic refresh path need an in-game check, and the current wearable armor appearance may differ from the original custom goggles model.
- Switch Probe currently copies/applies block state, but does not yet preserve arbitrary block-entity NBT like the original `SecretData` implementation.
- Redstone propagation, gate length/branching, door/trapdoor rendering, and one-way glass should be tested in both single-player and a dedicated server.
- Other original behaviors that were simplified elsewhere in the port may still differ; this archive must not be treated as complete feature parity until those are tested.


## 2026-10-26 follow-up fixes (placement, one-way glass, sounds)

- Secret block items now initialize camouflage state on both the client prediction path and server placement path, reducing the placeholder-model interval while waiting for block-entity synchronization.
- Secret blocks placed above other secret blocks inherit the existing mirrored state instead of being reset to stone.
- Double-high secret door placement initializes the lower-half camouflage state immediately; server placement synchronizes the upper half.
- One-way glass uses the vanilla glass model as its fallback instead of the block's item-icon texture, preventing the item icon from appearing when model data is not yet available.
- SecretBaseBlock delegates its sound type to the mirrored block so footsteps, hits, breaking, and placement use the camouflage material's sounds.
- These are source changes. Compilation and runtime behavior still require a successful Gradle build and in-game test.

## Final repository packaging (2026-10-26)

- Added a repository README with Java 21 / Gradle build instructions, CI workflow information, validation status, and a pre-release test checklist.
- Retained the existing GitHub Actions build workflow.
- Final archive integrity and resource JSON checks are run during packaging.
- **Release status: not build-verified.** The Gradle wrapper distribution could not be downloaded in this environment because `services.gradle.org` DNS resolution failed. Source-level changes and static resource checks do not establish successful compilation or correct in-game behavior.
- Before publishing publicly, confirm permission to redistribute the mod source/assets; metadata currently specifies `All Rights Reserved`.

## 2026-10-30 — True Vision overlay + torch lever
- True Vision: restored the original animated 16x256 `overlay.png` (+ `overlay.png.mcmeta`) instead of the solid cyan tile.
  Like the original 1.19.2 mod, the camouflage is drawn normally and the overlay is added on top of every face in the
  translucent layer (full-bright, 0.001 outward, position-based projection). Doors/trapdoors/one-way glass use the same path.
  Subclasses now override `camouflageQuads(...)` (and `baseRenderTypes`) instead of `getQuads`.
- Torch levers: click now flips POWERED on both sides (like the vanilla lever), sound + game event on the server, then
  notifies redstone neighbours. Powered state shows the redstone-torch texture (`torch_lever_*_on.json`) so the toggle is visible.
- Not compiled or run in the authoring environment.
- Torch lever look (per user): powered=false = normal lit torch; powered=true = unlit stick (no flame, `torch_lever_off.png`). Light 14 and particles unchanged from the original.
- Wall torch lever ON: same unlit model flipped with blockstate x=180 so the stick tilts outward-and-down like a flipped lever; OFF keeps vanilla upward tilt.
