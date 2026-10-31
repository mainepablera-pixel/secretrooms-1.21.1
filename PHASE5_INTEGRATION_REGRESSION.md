# Phase 5 — Integration and Regression Review

## Scope

This phase consolidates the Phase 1–4 source work and records final integration checks. It is not a claim that the mod is release-ready: the local environment could not download the Gradle distribution, so Java compilation and Minecraft/NeoForge runtime tests remain unverified.

## Integration findings

- **Door/trapdoor geometry:** the mapped vanilla door/trapdoor state is used for camouflage model geometry, while collision and outline behavior remain defined by the secret block classes. Regression-test all facing/open/hinge/half states and verify that the visible panel and collision shape stay aligned.
- **Model data and True Vision:** mirrored block states are stored in block entities and model data is explicitly refreshed on state changes. Test first load, chunk reload, dimension change, and goggles toggle; client/server state synchronization cannot be established from source inspection alone.
- **Torch levers:** custom torch-shaped model resources and shared quad UV safeguards are present. The safeguards avoid invalid/degenerate UV ranges, but do not prove every source model is visually correct. Test both standing and wall variants under all rotations and lighting conditions.
- **One-way glass:** face state semantics remain `true = glass`, `false = mirrored camouflage`; clicking toggles the selected face. This means clicking a camouflage face changes it to glass. This interaction was not redesigned because changing it without an explicit behavior specification could break intended gameplay.
- **Ghost groups:** connected-component traversal checks adjacent ghost blocks and hides a component when the player's bounding box intersects one of its blocks. At the 16,384-block safety limit it fails closed (returns hidden), so an exceptionally large component can remain hidden even when the player is outside. The traversal limit and chunk/model refresh behavior require runtime validation.
- **Gate segments:** dummy blocks record the owning gate position and mirror state; gates avoid replacing ordinary non-air blocks and avoid deleting dummy blocks that have another owner. Legacy dummy blocks without ownership metadata intentionally stop extension/retraction at that point rather than being claimed. Test adjacent/overlapping gates and old saves.
- **Inventory and redstone:** chest inventory and mirror state use registry-aware NBT serialization; trapped-chest output reads open count and notifies neighboring output on open/close. Multi-player open counts, comparator behavior, save/reload, and removal must be tested in-game.
- **Observer/daylight detector/buttons/pressure plates:** source implementations include scheduled state changes and neighbor updates, but pulse timing, output direction, weather response, arrow interaction, and entity filters need runtime confirmation.

## Checks performed

- All 158 JSON files under `src/main/resources` parsed successfully.
- The Phase 4 ZIP was extracted successfully and its source/report structure reviewed.
- `./gradlew compileJava --no-daemon` was attempted. It failed before Gradle started because the environment could not resolve `services.gradle.org` (`UnknownHostException`). This is an environment/network failure, not a Java compiler result.
- No Minecraft 1.21.1 NeoForge runtime test was performed.

## Required release gate

Do not treat the port as fully validated until the project's GitHub Actions build passes and the following are tested in a clean 1.21.1 NeoForge world:

1. Run the Gradle build and retain the full log; fix every compiler error before gameplay testing.
2. Test door/trapdoor hitboxes and visuals across all state combinations, including waterlogged trapdoors.
3. Test True Vision, torch lever models, one-way glass face toggles, and connected ghost groups at chunk boundaries and after moving in/out.
4. Test redstone output, scheduled release/pulse timing, gate obstruction and ownership, and neighboring gate interactions.
5. Save/reload inventories, mirror states, gate segments, and player-facing model data; test chunk unload/reload.
6. Check client logs for model bake errors, missing textures, and exceptions, and server logs for block-entity or tick errors.

## Final status

**Source/resource review: completed for this pass.**

**JSON validation: passed (158/158).**

**Java compilation: blocked before compilation by unavailable Gradle distribution download.**

**In-game behavior: not verified.**
