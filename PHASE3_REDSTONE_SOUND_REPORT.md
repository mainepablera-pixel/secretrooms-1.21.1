# Phase 3 — Redstone and Sound Integrity

Target: Minecraft 1.21.1, NeoForge.

## Source changes in this phase

- `SecretButton` now records the placement face as its `FACING` state, rotates/mirrors that state, and only emits a direct redstone signal toward its facing direction. Its normal redstone signal remains 15 while pressed.
- Wooden secret buttons now remain powered while an arrow is still in their activation volume, checking periodically rather than always releasing after the first timeout.
- Breaking a powered secret button or pressure plate now notifies neighbors so downstream redstone is not left in a stale powered state.
- Secret pressure plates retain plate-specific selection/collision geometry and schedule release checks while powered. State changes continue to use block update flags 3 and notify the plate position and support position.
- Standing and wall-mounted torch levers now emit activation/deactivation game events alongside their server-side lever click sounds.
- Sound and game-event writes remain on the logical server to avoid duplicate client/server events.

## Static review checklist

- [x] Button placement face is persisted as block state.
- [x] Button direct signal is directional instead of powering every side directly.
- [x] Wooden arrow-button timeout rechecks arrows.
- [x] Powered button/pressure-plate removal sends neighbor updates.
- [x] Torch-lever toggles emit game events and sounds server-side.

## Not verified

- Java compilation was not successfully run in this environment; the Gradle wrapper distribution is not cached and network/DNS access to `services.gradle.org` is unavailable.
- In-game redstone behavior, sound audibility, waterlogged placement, multiplayer behavior, and exact vanilla parity are not verified.
- The secret button still uses camouflage-derived visual/collision behavior from the base secret block; whether this is intended for every camouflage block requires the Phase 4 whole-mod audit.

## Required gameplay tests

1. Place each button on all six support faces; press/release it; verify only the outward direct-signal side is powered and normal signal reaches redstone dust as intended.
2. Shoot a wooden button with an arrow; verify it remains powered while the arrow is present and releases after it leaves.
3. Test stone/wooden button sounds on server and multiplayer client.
4. Test both pressure plates with eligible entities, then remove the entity and confirm release after the scheduled check.
5. Confirm the player-only plate ignores non-player entities.
6. Power a redstone lamp or comparator through each secret lever and torch lever; break each while powered and confirm the circuit turns off.
7. Recheck all activation/release sounds and game events on a dedicated server.
