# Secret Rooms 1.21.1 NeoForge Integrity Revision

This revision is organized into five phases. A phase is complete only when its source changes are reviewed, the project compiles, and its relevant runtime checks pass. A source ZIP or a successful ZIP integrity check is not evidence of a successful Java build or working gameplay.

## Phase 1 — Source integrity and camouflage state

Scope: compare the port's intended behavior against the original Secret Rooms feature set; audit block/item registration, placement context, block-entity persistence, model data, and state updates. Fix the one-way-glass placement-face inversion and the pressure-plate state/collision path. Establish regression cases for gates and dummy blocks.

Initial source corrections in this package:
- One-way glass derives the camouflage-facing side from the direction toward the placer, rather than using the support block face.
- Secret pressure plates use plate-sized selection/collision geometry instead of inheriting a full-cube camouflage collision shape.
- Pressure-plate state transitions use neighbor/client update flags so redstone consumers are notified.
- Button sounds and activation/deactivation game events are emitted by the logical server using a broadcast sound source, avoiding the player-exclusion behavior of a player-sourced sound call.
- Lever sounds and game events are emitted by the logical server to avoid client/server double processing.

Status: source edits applied; compilation and in-game behavior are not verified in this environment.

## Phase 2 — Doors and trapdoors

Audit every facing, hinge, half, open, powered, placement, and neighbor-update combination. Align outline/collision shapes with the vanilla orientation rules. Verify both door halves synchronize and do not produce inverted or offset hitboxes. Validate camouflage rendering separately from physical geometry.

Status: source corrections applied in this package; compilation and in-game orientation tests remain pending.

## Phase 3 — Redstone and sound behavior

Audit buttons, levers, wall/torch levers, normal and player-only pressure plates, secret redstone, observer/daylight detector, and gate activation. Check signal strength, direct signal, neighbor notifications, scheduled release ticks, sound events, and multiplayer authority.

Status: source corrections applied in `PHASE3_REDSTONE_SOUND_REPORT.md`; compilation and gameplay tests remain pending.

## Phase 4 — Full block/item/resource audit

Audit secret gate and dummy-block lifecycle; ghost blocks; stairs; one-way glass rendering; chests and trapped chests; clamber; camouflage paste and switch probe; goggles; item placement and drops; all recipes, blockstates, models, textures, loot tables, tags, language entries, and registrations. Check block-entity NBT/model data and whether camouflage survives reloads and stacked placement.

Status: pending.

## Phase 5 — Build and regression validation

Compile with the repository's intended Gradle/NeoForge toolchain; fix all compile errors and resource validation errors; verify the produced JAR and GitHub Actions artifact. Test a fresh world and an existing test world, including reloads and multiplayer/server behavior.

Status: pending. The current environment cannot resolve services.gradle.org, so the Gradle wrapper cannot download Gradle 9.2.1 and no compile result is available.

## Regression checklist

- One-way glass: each of six placement faces; toggle each face; all sides render correctly after chunk reload.
- Door/trapdoor: every facing; open and closed; both hinges; upper/lower halves; manual and redstone operation; collision and selection shape match the visible panel.
- Buttons/levers/plates: audible activation and release; redstone output; neighbor consumers update; release tick works; player-only plate ignores non-players.
- Gate/dummy: copy the gate's camouflage; extend and retract; do not overwrite non-replaceable blocks; inspect behavior near other gates and after reload/breaking the gate.
- Every block entity: place, set camouflage, save, leave/re-enter world, verify model and behavior persist.
- Items/resources: registration, recipe, placement, survival, drops, model/texture, and no missing-resource log entries.

## Phase 4 status (current package)

The whole-mod source audit and targeted fixes are recorded in `PHASE4_WHOLE_MOD_AUDIT.md`. This phase initializes both halves of double-high camouflage blocks and prevents gates from overwriting general replaceable world blocks. Trapped-chest output and gate-segment ownership are explicitly unresolved and require further implementation and tests. Build and in-game validation remain pending.


## Pre-Phase-5 finalization (2026-10-27)

Implemented source-level fixes for trapped-chest output, gate-segment ownership, and daylight-detector signal lifecycle. The project owner will perform Phase 5. Build execution is blocked in this environment because `services.gradle.org` cannot be resolved; do not treat ZIP validation as compilation validation. See `FINALIZATION_PRE_PHASE5.md`.


## Five-phase program — Phase 2

Core rendering repair applied in `PHASE2_CORE_RENDERING_REPAIR.md`: hardened quad UV remapping and retained torch-specific geometry/render layers for torch levers. Java compilation and in-game behavior remain unverified.
