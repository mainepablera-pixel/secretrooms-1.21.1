# Phase 2 — Rendering Repair Follow-up

Target: Minecraft 1.21.1 / NeoForge.

## Changes

- `SecretMappedModel` now requests vanilla door/trapdoor geometry with a null render-type filter. The copied block may be solid while vanilla door/trapdoor models use cutout geometry; passing the copied block's render layer can return zero panel quads. The secret block's render layer selection remains handled separately by the model.
- `TrueVisionBakedQuad` now remaps the source quad's UV coordinates into the identifier sprite instead of projecting UVs from vertex positions. This avoids stretched texture strips on rotated and non-cube geometry.
- Replaced the 16×256 animated strip texture with a single 16×16 opaque identifier tile. The old strip was being interpreted as animation frames, which produced repeated collage-like marker rendering. Removed its obsolete animation metadata.

## Validation

- Resource JSON parsing and ZIP integrity should be checked before use.
- Java compilation and Minecraft runtime rendering were not available for verification in this environment.

## Regression tests

1. Test secret wooden/iron doors and trapdoors while copying stone (solid), glass (translucent/cutout), and planks. Confirm visible panel and collision outline both remain.
2. Test open and closed doors with all four facings and both hinges; test trapdoors with all four facings and both halves.
3. Equip True Vision and inspect full cubes, stairs, glass, doors, trapdoors, and chests. The identifier should be a stable cyan marker without repeated vertical strips.
4. Test entering and leaving True Vision range and changing head equipment to ensure section refresh works.
