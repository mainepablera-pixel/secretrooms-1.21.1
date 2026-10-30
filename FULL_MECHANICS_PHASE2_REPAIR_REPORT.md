# Full mechanics review and targeted repair — Phase 2

Baseline: `secretrooms-full-mechanics-phase2-repair.zip`.

## Targeted source/resource changes

1. **Torch and wall-torch lever model fallback**
   - Added local standing and wall torch models with explicit vanilla torch textures.
   - Updated every blockstate variant to use those local models, including powered states.
   - Updated `SecretBlockModel` to retrieve the mirrored source quads without applying the wrapper's render layer prematurely. `getRenderTypes` still delegates the source model's layers. This addresses a plausible failure mode where cutout source geometry is filtered by a different wrapper layer.

2. **True Vision marker**
   - Replaced the texture-strip/outline-style marker with a single flat, opaque cyan tile. The marker is intentionally texture-independent and does not repeat camouflage details across each face.
   - Retained bounded UV remapping to keep every vertex within the one tile. The marker changes the appearance of source quads, not their positions or collision geometry.

3. **One-way glass selected-face render path**
   - Retrieve directional glass quads without prematurely applying a render-layer filter, then keep glass quads in the translucent pass as intended by the model logic.
   - Existing semantics are retained: `true` means glass; `false` means camouflage. Clicking a camouflage face still changes it to glass by design. If gameplay intent is instead to toggle the opposite face, that behavior needs explicit confirmation; it should not be guessed from a rendering symptom.

## Static validation performed

- All resource JSON files parsed successfully.
- New torch model files exist and changed blockstate JSON parses.
- Overlay PNG is 16x16 RGBA with a flat opaque cyan pixel.
- ZIP integrity is checked after packaging.

## Build limitation

`./gradlew compileJava --no-daemon --console=plain` was attempted. It could not start because this environment cannot resolve `services.gradle.org` (`UnknownHostException`) while trying to download Gradle 9.2.1. This is an environment/network failure, not a Java compiler result. Compilation and runtime behavior remain unverified.

## Remaining high-risk areas requiring a real 1.21.1 NeoForge client test

- Verify torch/wall-torch appearance with both ordinary cube camouflage and torch-like camouflage; test all wall orientations and redstone output separately.
- Verify True Vision on cubes, stairs, doors, trapdoors, chests, and one-way glass; flat cyan is deliberately a solid identifier rather than an outline.
- Verify doors and trapdoors in every facing/open/hinge/half state, preserving the known-good hitboxes.
- Verify one-way glass clicked-face behavior and all six face properties, including chunk reload.
- Chest shape remains cube-based rather than a chest-specific renderer.
- Ghost group traversal remains bounded and needs tests for large groups and chunk boundaries.
- Redstone components, gate overlap/ownership, inventory persistence, daylight detector, observer, and placement/sound mechanics still require end-to-end testing.

This package is a targeted repair, not a claim that every possible failure has been eliminated. Run the repository's GitHub Actions build and in-game regression suite before treating it as release-ready.
