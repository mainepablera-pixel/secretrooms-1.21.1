# Phase 4 — Mechanics and Persistence Audit

## Applied source correction

- **Secret Observer placement direction:** removed a redundant double `getOpposite()` in `SecretObserver#getStateForPlacement`. The old expression simplified to the player's nearest looking direction, so the observer could face away from the intended placement direction. It now uses the opposite of that direction, consistent with the observer's output-facing convention. This is a source-level correction; it has not yet been confirmed in a running game.

## Reviewed systems and source-level status

- **Torch levers:** both standing and wall variants have a persisted `POWERED` block-state property, server-authoritative toggling, click sounds/game events, signal methods, and neighbor notifications on toggle/removal. Their precise direct-signal direction and model rendering still require in-game redstone and visual tests.
- **Buttons:** state changes and delayed release are implemented. Wooden buttons recheck for arrows while powered; stone buttons use the shorter delay. Test on all mounting faces and with arrows in a running game.
- **Pressure plates:** entity filtering distinguishes living entities from players-only mode; scheduled checks release the plate after entities leave. The plate retains its own thin shape instead of inheriting the camouflage block's collision. Test entity entry/exit and redstone propagation.
- **Trapped chest:** redstone output reads the chest block entity's open count, capped at 15. The chest updates neighboring output when viewers open/close it. Multi-player open/close behavior and reload behavior still require gameplay testing.
- **Chest inventory:** items are serialized with the current registry-aware NBT methods; mirror state is also serialized. Open count is intentionally transient rather than saved. Test inventory persistence across save/reload and block removal.
- **Daylight detector:** power is recalculated on a scheduled interval and after neighbor changes; inversion toggles the state and schedules an immediate recalculation. Its implementation is simplified compared with vanilla weather-sensitive behavior; clouds/weather and time-of-day behavior should be tested and compared against the intended design.
- **Gate:** dummy segments store their owning gate position and mirror state; the gate refuses to overwrite non-air world blocks and will not claim unowned/foreign dummy segments. Gate length, connected-gate behavior, overlapping gates, save/reload, and power transitions need runtime tests.
- **Observer:** pulse timing, front-side neighbor notifications, and output direction need runtime testing after the placement correction.

## Not claimed as verified

This phase does not establish that the project compiles or that all mechanics are fixed. No successful Gradle build or Minecraft/NeoForge runtime test was available during this pass. Validate in the project's GitHub Actions environment and in a test world before treating this phase as complete.

## Suggested regression checklist

1. Place observers on each of the six faces; confirm the output face and pulse timing.
2. Toggle standing and wall torch levers; verify the intended adjacent redstone components respond and turn off correctly when removed.
3. Test stone/wood buttons, arrow activation, and all pressure-plate entity modes.
4. Open/close trapped chests with one and multiple players; confirm comparator and redstone output updates.
5. Fill a secret chest, save/reload, and confirm all contents remain.
6. Test daylight-detector output through day/night, weather changes, inversion, and nearby block updates.
7. Power/unpower gates at lengths 1–9; test obstruction safety, adjacent gates, overlapping gates, save/reload, and breaking the owner gate.
8. Recheck door/trapdoor collision and camouflage rendering to ensure this phase did not regress earlier work.
