# Secret Rooms 1.21.1 — Full Mechanics Audit (Phase 1: Analysis Only)

## Scope and evidence

Baseline: `secretrooms-phase2-targeted-rendering-fixes.zip` (`secretrooms-github-final/`). This phase changes no Java, JSON, texture, or build files. Findings are source-level; runtime behavior and compilation are not certified by this report.

## Priority findings

### P0 — True Vision can fall back to the original model
- `src/main/java/com/wynprice/secretrooms/client/model/quads/TrueVisionBakedQuad.java`: `generateQuad` returns the original source quad if `overlaySprite == null`.
- `SecretBlockModel.identifierQuads` guards the overlay sprite, but `SecretMappedModel.getQuads` directly calls `generateQuad` for mapped door/trapdoor quads and does not apply the same readiness guard. That means a missing/uninitialized overlay sprite can leak the copied model instead of the identifier.
- Identifier rendering uses the fallback model's solid quads in `SecretBlockModel`, while mapped models use the mapped door/trapdoor model. The silhouette source is inconsistent across model classes.
- `TrueVisionGogglesClientHandler` rebuilds chunks only when equipment or player block position changes. It does not explicitly refresh when atlas/model resources reload, when mirror state changes without a blockstate change, or on every state transition relevant to identifier data. Must verify with resource reload and chunk-rebuild tests.
- Recommended fix: centralize identifier generation; if the overlay sprite is unavailable, return no quads (never source/camouflage quads); use the same explicit identifier material/render path for all secret model wrappers; request model/chunk rebuilds for relevant lifecycle changes. Avoid generating identifier quads by retexturing arbitrary camouflage geometry.

### P0 — Torch lever rendering is not camouflage-aware in blockstate definitions
- `assets/secretroomsmod/blockstates/torch_lever.json` points directly to `minecraft:block/torch` for both `powered` values.
- `wall_torch_lever.json` points directly to `minecraft:block/wall_torch` for all facings/power states.
- Those blockstate definitions only select vanilla torch geometry. They do not by themselves establish that the torch geometry is correctly retextured using the block entity's mirror state. The reported all-black torch appearance is consistent with an invalid/missing texture/model-data route, but requires runtime inspection to distinguish a missing texture from light/tint/atlas issues.
- `TorchLever` and `WallTorchLever` redstone state logic is separate from their render path. Do not treat powered variants in JSON as a fix for camouflage rendering.
- Recommended fix: use a dedicated torch-shaped camouflage model wrapper that retains vanilla standing/wall torch geometry and applies the copied block texture to its quads while preserving valid UVs, tint, lighting, and model data. Validate all orientations and both powered states.

### P0 — Door/trapdoor visible model and hitbox are separate paths
- `SecretDoor` and `SecretTrapdoor` calculate collision/outline geometry from vanilla states; this is why hitboxes can be correct even when rendered panels glitch or disappear.
- `SecretMappedModel` separately chooses `MODEL_MAP_STATE`, obtains a baked model, then retextures its quads with the mirror model's particle icon. It must receive correct mapped state for each half/facing/hinge/open combination and correct mirror state on both door halves.
- `SecretMappedModel` true-vision path returns `identifierQuads` if mapped state is missing, but otherwise calls `generateQuad` without checking `TrueVisionBakedQuad.isReady()`; this is the specific fallback inconsistency noted above.
- Door blockstate JSON enumerates facing/half/hinge/open variants; trapdoor JSON enumerates facing/half/open variants. All combinations need automated JSON/variant coverage and in-game tests. Current hitbox success must be preserved.
- Recommended fix: keep shape methods unchanged unless a failing test proves otherwise; test model data for lower/upper door halves and trapdoor states; verify directional and unculled quads and render-layer calls separately.

### P1 — One-way glass face transition is ambiguous and rendering depends on render pass
- `OneWayGlass.useWithoutItem` cycles the clicked face's boolean. `OneWayGlassModel` interprets `true` as glass and `false` as camouflage. Placement sets the face toward the placer to `false`. This may be intentional, but the comments call the same states “open” and “closed” inconsistently; user expectation should be written explicitly before changing polarity.
- `OneWayGlassModel` queries camouflage quads with the current render type and only filters directional quads by face property. It also relies on the mirror model's render-type set. For models with unculled quads (`side == null`) or complex multi-layer models, face-specific replacement may be incomplete or duplicate geometry.
- `level.sendBlockUpdated(..., 8)` plus model-data update is present; the persistent disappearing face is therefore not safely explained as merely a missing refresh. The face-state/render-pass selection is the primary path to inspect.
- Recommended fix: specify the contract (“face toward placer shows copied block” vs “face toward placer is one-way glass”), then build a deterministic per-face replacement path that avoids duplicate unculled quads and handles cutout/translucent source models.

### P1 — Ghost group visibility is bounded and refresh behavior needs validation
- `GhostGroupVisibility` uses a bounded connected-component traversal. A large group can exceed the cap, so some connected ghost blocks can remain visible.
- `SecretBlockModel.getModelData` caches `GHOST_GROUP_HIDDEN` as model data. Any player movement that changes the connected group’s visibility must invalidate all affected chunks/sections; inspect `GhostGroupVisibility` and its event hooks together. A local position-only refresh is not enough for a group spanning chunks.
- Recommended fix: use a deterministic group query with explicit safety bounds and cross-chunk invalidation. Test vertical stacks, horizontal chains, groups crossing chunk borders, groups larger than the current cap, player entering/leaving, and multiplayer client separation.

### P1 — Chest camouflage uses cube geometry as a fallback
- `SecretBlockModel.getQuads` special-cases chest/trapped chest by retexturing the fallback model's cube quads with the chest particle icon. This avoids importing the vanilla chest model's special renderer quads, but it cannot reproduce the chest silhouette and may appear as a cube/texture patch rather than a chest.
- The special vanilla chest block-entity renderer and the secret chest's own block entity need to be considered together. Current source-level fallback does not constitute a chest-shaped camouflage renderer.
- Recommended fix: choose a deliberate chest strategy: a dedicated chest-shaped baked model or a custom renderer using the copied texture, while preserving inventory rendering, lid animation, sounds, viewer tracking, and trapped-chest redstone output.

### P1 — Torch lever redstone mechanics require behavioral tests
- `TorchLever` and `WallTorchLever` manually toggle `POWERED`, emit lever-click sounds and game events, and notify neighbors in all six directions.
- `WallTorchLever.getDirectSignal` returns power when `side == HORIZONTAL_FACING`; this direction convention needs an in-game redstone test for each wall orientation. A wall torch's facing convention can differ from the direction toward which it emits power.
- Their superclass torch behavior may include support validation and particles. The powered state should not suppress the torch model or break support checks.
- Test power levels, direct power direction, neighbor updates, removal while powered, support block removal, rotation/mirroring, and save/reload.

## Whole-mod mechanics checklist

- **Secret buttons:** verify press duration, sound, facing, power output, neighbor notification, and release scheduling for wood/stone variants.
- **Secret levers:** verify powered state persistence, mounting direction, direct/indirect signal, sound, and removal updates.
- **Pressure plates:** verify player-only versus all-entity detection, weighted strength if applicable, tick scheduling, release timing, and redstone notification.
- **Secret redstone:** verify dust connectivity, powered state, weak/strong power, repeater/comparator interaction, and waterlogging.
- **Observer:** verify orientation at placement, pulse length, change detection, and no duplicate scheduled pulses.
- **Daylight detector:** verify day/night/weather updates, inversion, output, chunk loading and state persistence.
- **Secret gate/dummy segments:** verify maximum length, obstruction handling, removal ownership, overlapping gates, unloaded chunks, and cleanup of orphan dummy blocks.
- **Secret chest/trapped chest:** verify inventory persistence, open/close sounds, viewer counts, comparator output, trapped-chest signal, and multiplayer viewers.
- **Secret doors:** verify upper/lower state synchronization, hinge selection, redstone, manual interaction, support removal, waterlogging if supported, and mirror state on both halves.
- **Secret trapdoors:** verify facing, top/bottom half, manual interaction, iron-door/trapdoor restrictions, redstone, sound, and waterlogging.
- **Clamber:** verify climbing attachment rules, collision, movement, and removal behavior.
- **Ghost blocks:** verify player-in-block visibility, connected groups, chunk boundaries, and collision/selection expectations.
- **All block items:** verify placement state, mirror-state inheritance, double-block placement rollback, drops, and item-model appearance.
- **Persistence/network:** verify mirror state NBT, update packets, chunk reload, server/client synchronization, and old-world compatibility.

## Phase 2 implementation order

1. Fix and centralize True Vision identifier rendering, including safe behavior when the atlas sprite is unavailable.
2. Fix torch and wall-torch camouflage model wrappers while leaving redstone logic separate.
3. Stabilize door/trapdoor mapped-state and directional quad selection without changing proven collision shapes.
4. Rework one-way glass face-state contract and quad filtering.
5. Correct ghost-group visibility invalidation and traversal policy.
6. Implement chest-appropriate camouflage rendering.
7. Audit redstone/sounds/state persistence across all registered blocks and run compile + in-game regression matrix.

## Validation status

- Source and resource inspection performed.
- This phase is analysis-only; no project source files were changed.
- No successful Gradle compilation or Minecraft runtime session is claimed.
