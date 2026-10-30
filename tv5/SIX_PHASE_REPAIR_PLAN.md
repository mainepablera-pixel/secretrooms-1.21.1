# Secret Rooms 1.21.1 — Six-Phase Fortified Repair Plan

## Phase 1 — Door and trapdoor mechanics (patch prepared; build/test pending)
- Use vanilla door/trapdoor state geometry for outline and collision shapes.
- Keep camouflage rendering in model data; do not merge a copied full-block shape into the physical panel.
- Emit explicit vanilla wooden/iron door and trapdoor open/close sounds from the authoritative server.
- Preserve open/closed game events and refresh model data after state changes.

## Phase 2 — Door/trapdoor state and placement audit
- Verify upper/lower door halves are placed, removed, and synchronized together.
- Verify hinge, facing, open state, powered state, and waterlogging behavior.
- Verify iron variants remain redstone-operated rather than hand-operated, matching vanilla behavior.
- Audit server/client state synchronization and chunk reload persistence.

## Phase 3 — Camouflage and model-data integrity
- Ensure the mirrored block state controls rendered texture/model only where intended.
- Ensure both halves of a secret door inherit the same mirrored state.
- Check model refresh after opening, closing, redstone changes, placement, and reload.
- Avoid recursive mirroring when a secret block copies another secret block.

## Phase 4 — Whole-mod compatibility audit
- Review secret gate, stairs, ghost blocks, one-way glass, buttons, levers, torches, and redstone interactions.
- Check collision, outline/selection, occlusion, sound delegation, waterlogging, and neighbor updates independently.
- Check item placement and block drops for state/property loss.
- Record unresolved behavior instead of marking untested features as complete.

## Phase 5 — Reproducible build and automated checks
- Run the repository's Gradle build in GitHub Actions against the declared Minecraft/NeoForge versions.
- Fix compilation errors and distinguish warnings from failures.
- Confirm the JAR artifact is produced and its metadata is valid.
- Do not call the build verified until the actual workflow log reports success for this exact commit.

## Phase 6 — In-game validation and release hardening
- Test wooden and iron doors/trapdoors with solid and non-solid camouflage blocks.
- Verify players can pass through open doors and through open trapdoors where vanilla geometry allows.
- Verify thin outline/collision geometry, correct sounds, redstone transitions, and multiplayer synchronization.
- Test save/reload and chunk unload/reload; record results and package only after tests pass.

## Verification status
The Phase 1 source changes in this archive have not yet been compiled or tested in Minecraft. Phases 2–6 are a checklist, not a claim that those checks have already passed.

## Follow-up patch: compile-error corrections (2026-10-27)

Applied source corrections for the eight compiler errors reported in the uploaded GitHub Actions log:

- Replaced protected vanilla `getShape` / `getCollisionShape` calls in `SecretDoor` and `SecretTrapdoor` with explicit thin, state-dependent voxel shapes.
- Corrected `TorchLever` and `WallTorchLever` superclass constructor argument order.
- Removed the unavailable `ChunkStatus` reference and changed the client chunk lookup to the 1.21.1 `getChunk(x, z, false)` form.

These corrections have **not been compiled or tested in-game** in this environment: the Gradle wrapper could not download Gradle because `services.gradle.org` was unreachable. The next validation step is to push this source to GitHub and inspect the Actions result for this exact revision.
