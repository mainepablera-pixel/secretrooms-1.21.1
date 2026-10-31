# Phase 2 rendering follow-up

## Fixes

- `SecretMappedModel` now queries the mapped door/trapdoor model using the renderer-provided face direction. Previously it returned no directional quads and queried only the unculled bucket, which could leave the collision shape present while the visible panel disappeared.
- `TrueVisionBakedQuad` no longer offsets each vertex along the face normal. It preserves original geometry, maps the identifier texture using clamped block-local face coordinates, and uses full-bright lighting. This avoids split edges and UVs wrapping outside the intended marker tile.

## Verification

The ZIP structure is validated, but Java compilation and in-game rendering have not been run in this environment. Please test closed/open doors and trapdoors across all facings, then test True Vision against cube, stair, door, and chest camouflage. If the identifier still glitches, the next diagnostic should capture the exact model and render type rather than further changing collision geometry.
