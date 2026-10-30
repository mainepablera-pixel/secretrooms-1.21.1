# Phase 2 — Source-level fixes

This package implements the Phase 1 analysis recommendations against the uploaded `secretrooms-rendering-interaction-fixes-compile-corrected.zip` source.

## Implemented

- **True Vision:** while the goggles are equipped, secret baked models render the dedicated marker texture on their own fallback silhouette instead of rendering copied camouflage quads underneath the marker. Door/trapdoor mapped models use their mapped silhouette for the marker as well. If the marker sprite has not stitched yet, the identifier pass returns no quads instead of leaking the fallback or copied texture.
- **Door/trapdoor collision and outline:** both delegate their shape to a vanilla oak door/trapdoor state with the same facing/open/hinge/half/powered/waterlogged properties. This uses the same vanilla state mapping as the rendered geometry.
- **Door/trapdoor rendered geometry:** `SecretMappedModel` now takes vertex positions from the vanilla mapped door/trapdoor baked model and retextures those quads using the copied model's particle sprite. It no longer clamps arbitrary copied-block vertices into a bounding box, which was not a reliable geometry transformation.
- **Chest camouflage:** chest/trapped-chest camouflage avoids importing vanilla chest baked-model quads (which are not the chest's full geometry because vanilla uses a block-entity renderer). It uses the secret block's cube silhouette and the copied chest model's particle sprite. This is a stable fallback, not a fully shaped chest camouflage renderer.
- **One-way glass:** removed the extra unculled-quad merge that could duplicate faces already returned by directional model queries.
- **Connected ghost groups:** raised the traversal limit from 512 to 4096 blocks to reduce partial visibility on larger connected structures. This remains a bounded traversal and very large components still need runtime testing.
- **Chest base models:** changed the secret chest/trapped chest fallback model to use a block cube parent and a block texture, rather than an item texture as the `cube_all` texture.

## Not claimed as resolved

- Torch-lever black rendering still requires in-game testing and may depend on its blockstate/model JSON or a renderer path not fully represented by the current source audit.
- Chest camouflage uses a cube silhouette, not a chest-shaped custom renderer; this prevents the reported collage/duplicate-model issue but does not preserve a chest-shaped silhouette.
- Connected ghost groups above 4096 blocks are still bounded; use a dedicated cache or chunk-aware component system if such group sizes are required.
- Door manual-click/redstone interactions, one-way glass face toggles, overlay appearance, and all render layers require runtime verification.

## Validation status

- Source archive was based on the user's latest provided corrected ZIP.
- JSON files are parsed as a validation step when packaging.
- No successful Gradle compile or Minecraft client run is claimed. Run the GitHub Actions build and then perform the in-game regression checks from `RENDERING_AND_INTERACTION_FIXES.md`.
