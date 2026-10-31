# Phase 4 — Whole-mod integrity audit

Target: Minecraft 1.21.1 / NeoForge.

## Source paths reviewed

- `server/blocks`: secret base, ghost block, one-way glass, doors/trapdoors, buttons, levers, pressure plates, gate/dummy, stairs, chest/trapped chest, observer, daylight detector, redstone, clamber, and torch levers.
- `server/items`: single-block and double-high secret placement, switch probe, and True Vision Goggles.
- `server/tileentity`: camouflage state persistence, chest inventory persistence, and daylight-detector block entity.
- `client/model`: secret mapped models, one-way-glass model, model data, and quad handling.
- Registrations and resource tree.

## Changes made in this phase

1. `SecretDoubleBlockItem` now initializes the camouflage state on both the placed block entity and the upper block entity. This avoids the top half of a double-high secret block retaining the default stone camouflage.
2. `SecretGateBlock` now extends into air or an existing dummy segment rather than any generally replaceable block. Minecraft's `canBeReplaced()` includes vegetation and other world blocks; replacing those with dummy segments and later clearing them to air could destroy world content.

## Confirmed issues requiring a separate correction

- `SecretTrappedChest#getSignal` currently returns `0` unconditionally. It therefore cannot provide trapped-chest redstone output. This remains unresolved in this phase and must not be treated as fixed.
- `SecretGateBlock` has no ownership metadata for dummy segments. Multiple overlapping gates can potentially overwrite or retract another gate's dummy segments. A robust fix needs explicit ownership/segment tracking, not a guess based on the copied block state.
- `SecretDaylightDetectorTileEntity` is empty; daylight signal computation must be verified in `SecretDaylightDetector` and its update lifecycle.
- `SecretChest` directly opens its menu; inventory persistence exists, but chest open/close sounds, viewer tracking, and trapped-chest output need end-to-end validation.
- Generic camouflage shape delegation can be inappropriate for specialized blocks whose physical shape must remain independent of the copied texture (doors, trapdoors, buttons, pressure plates, stairs, ghost blocks). Each specialized class must override physical shape behavior intentionally.

## Verification status

- ZIP integrity: verified after packaging.
- Java compilation: not verified in this environment because the Gradle distribution download previously failed due to DNS resolution for `services.gradle.org`.
- In-game tests: not performed. Phase 4 is a source audit and targeted patch, not a claim that every mod behavior has passed regression testing.

## Regression checklist

- Place each double-high secret block and verify both halves retain the same camouflage after save/reload.
- Power/retract a gate across air, vegetation, a solid block, and another gate; verify no ordinary world block is deleted and overlapping gates do not erase each other's segments.
- Verify trapped chest signal strength at 0, 1, and multiple viewers; confirm neighbor updates on open/close.
- Test daylight detector output at night/day and with inverted mode.
- Verify every secret block's copied appearance is independent of its intended collision and outline geometry.
- Test inventory and camouflage persistence after chunk unload/reload and server restart.


## Pre-Phase-5 finalization update

The trapped chest now tracks active non-spectator viewers and exposes that count as redstone output; gate dummy segments carry persisted owner positions so a gate only retracts its own segments; and the daylight detector schedules periodic sky-light signal updates and supports inverted mode. See `FINALIZATION_PRE_PHASE5.md`. These changes remain uncompiled and require Phase 5 regression testing.
