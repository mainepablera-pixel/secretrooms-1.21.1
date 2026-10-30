# Phase 2 — Core Rendering Repair

## Scope
This phase targets the recurring torch-lever black silhouette and texture corruption in mapped door/trapdoor quads. The already-correct door/trapdoor collision and outline geometry was not modified.

## Implemented changes

1. `NoTintBakedQuadRetextured` now guards zero UV spans, rejects non-finite UV values, and clamps normalized UV coordinates to the target sprite. This prevents NaN/Infinity and prevents sampling outside the destination sprite tile.
2. `SecretBlockModel` now preserves the baked torch/wall-torch geometry for `TorchLever` and `WallTorchLever`, retexturing that geometry with the copied block's particle sprite instead of substituting the copied block's geometry.
3. Torch lever render types now follow the torch model's own render layer instead of inheriting the copied block's render layer. This avoids submitting cutout torch geometry in an incompatible opaque pass.

## Deliberate limitations

- Torch camouflage uses the copied block's particle sprite across the torch-shaped geometry. This is a stable fallback, not face-accurate projection of every copied block texture.
- This phase does not change one-way-glass click semantics. In the current source, clicking cycles the clicked face property; `true` selects glass and `false` selects camouflage. The user's intended interaction semantics should be confirmed before changing that behavior.
- True Vision's identifier path still requires in-game verification across opaque, cutout, translucent, door, trapdoor, and torch models.

## Validation performed

- Source edits checked for expected target files and changed sections.
- Resource JSON parsing and ZIP integrity checked when packaging.
- Java compilation and Minecraft runtime tests were not available in this environment; do not treat this package as compile- or gameplay-verified.

## Regression checklist

1. Place both torch lever variants while mirroring stone, grass, planks, and glass. Confirm torch silhouette is visible and no black shape appears.
2. Toggle each torch lever twice; verify sound and redstone output separately from rendering.
3. Test mapped doors/trapdoors with stone, grass, glass, stairs, and slabs; verify no texture atlas bleed and retain current hitboxes.
4. Test True Vision on full cubes, transparent blocks, doors, trapdoors, and torch levers.
5. Check one-way glass state semantics independently; no interaction behavior was changed in this phase.
