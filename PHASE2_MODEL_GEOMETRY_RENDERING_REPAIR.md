# Three-Phase Repair — Phase 2: Model Geometry and Rendering

## Implemented in source
- Secret doors now delegate visual-shape queries to the same vanilla door state already used for outline and collision shapes.
- Secret trapdoors now delegate visual-shape queries to the same vanilla trapdoor state already used for outline and collision shapes.
- This makes outline, collision, and visual-shape behavior use a single state mapping (facing/open/hinge/half/powered), reducing the chance that interaction/raycast shape disagrees with the visible vanilla door/trapdoor geometry.
- Retained the Phase 1 one-way-glass directional rotation/mirroring fix.
- Reviewed the current mapped model path: it uses the mapped vanilla door/trapdoor model's directional quad buckets and avoids filtering those quads through the camouflage block's render layer. Reviewed torch-lever rendering: custom torch geometry is retained and retextured with the copied block's particle sprite, rather than importing incompatible source-block vertices.

## Not claimed fixed
- Visual alignment has not been verified against screenshots or an in-game build for every facing, open state, hinge, half, and iron/wood variant.
- True Vision rendering and torch-lever appearance have not been verified in-game.
- No changes were made to True Vision interaction semantics or to one-way-glass click semantics in this phase.

## Validation
- Parsed every resource JSON file.
- Checked ZIP integrity.
- Java compilation and Minecraft 1.21.1 NeoForge runtime tests were not available; the added `getVisualShape` overrides must be confirmed by the project's actual Gradle build.
