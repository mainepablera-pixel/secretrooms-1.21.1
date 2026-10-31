# Secret Rooms 1.21.1 NeoForge — Pre-Phase-5 Finalization

This package consolidates the source-level corrections made before the owner's Phase 5 work. Phase 5 is intentionally left to the project owner.

## Corrections included in this finalization

1. **Trapped chest redstone output:** the secret trapped chest now reports a signal based on the number of non-spectator players using its container (clamped to 0–15). The chest tile entity tracks open/close callbacks and notifies neighboring redstone components when the count changes.
2. **Gate dummy ownership:** secret tile entities can persist the owning gate position. Gates tag the dummy segments they create, reuse only their own segments, and retract only segments they own. Untagged/legacy segments are left untouched rather than being claimed or deleted, avoiding destructive assumptions for old saves.
3. **Daylight detector lifecycle:** the detector now schedules periodic updates, recalculates its sky-light signal, notifies redstone neighbors when the signal changes, and supports right-click toggling of inverted mode.
4. **Earlier phase corrections retained:** double-height secret blocks initialize mirror state on both positions, and gate extension no longer overwrites replaceable non-air world blocks.

## Phase 5 verification still required

- Run `./gradlew clean build` in an environment that can download the Gradle 9.2.1 distribution and NeoForge/Minecraft dependencies.
- Fix any compiler or mapping/API errors reported by that build; this environment could not reach `services.gradle.org` (`UnknownHostException`), so compilation was not verified here.
- In-game, test trapped chest output at 0, 1, and multiple viewers, including close/disconnect cases.
- Test two overlapping/adjacent gates, gate break/retract, chunk unload/reload, and worlds containing legacy untagged dummy blocks.
- Test daylight detector values during day/night, weather changes, inversion toggling, and redstone consumers.
- Re-test door/trapdoor geometry, one-way glass orientation, all button/lever/pressure-plate sounds and output, gate/dummy camouflage, inventories, and every registered block/item.

These changes are source-level repairs, not a claim of a successful build or in-game verification. Phase 5 should be treated as the final compile-and-regression pass.
