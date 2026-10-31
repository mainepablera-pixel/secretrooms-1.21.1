# True Vision Rendering Follow-up — Targeted Source Repair

## Defects addressed

1. **Render-layer filtered silhouettes.** `SecretBlockModel.identifierQuads` previously requested source quads with `RenderType.solid()`. That can filter out models whose geometry is authored for cutout or translucent rendering, including torch and one-way-glass fallback models. It now queries source geometry without a render-layer filter; True Vision still emits the identifier in the solid pass.
2. **Overlay atlas inclusion.** The overlay texture was loaded with `TextureAtlasStitchedEvent#getSprite`, but no guarantee existed that `secretroomsmod:block/overlay` had been included in the block atlas. The overlay texture is now declared in the texture maps of loaded block models for one-way glass, torch levers, doors, and trapdoors, so the model baker gathers it for atlas stitching.
3. **Initial equipment refresh.** The goggles handler previously returned on its first frame even when the player was already wearing goggles. It now proceeds with the refresh in that case, instead of waiting for an equipment change or movement.

## Areas affected
- True Vision silhouettes for general secret blocks, torch levers, and one-way glass.
- Door and trapdoor True Vision models share the same atlas overlay; their mapped geometry path already queries the mapped model without a render-layer filter.

## Validation
- Changed model JSON files parsed successfully.
- ZIP integrity checked after packaging.
- Java compilation and Minecraft/NeoForge rendering were not run in this environment. These are targeted source fixes, not a claim of in-game confirmation.
