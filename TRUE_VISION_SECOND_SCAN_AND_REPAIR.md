# True Vision — Second Source Scan and Targeted Repair

## Defects confirmed in the inspected source

1. **Initial goggles refresh was ineffective.** `TrueVisionGogglesClientHandler` initialized `wasWearing` to the current equipment state, then compared it immediately. When a world was entered already wearing the goggles, both `equipmentChanged` and `playerChangedBlock` were false and the handler returned without refreshing meshes. The initial refresh is now an explicit trigger.
2. **Overlay atlas inclusion was implicit.** The overlay texture was mentioned only as an otherwise-unused texture key in several model JSON files. That is not a sufficiently reliable atlas-inclusion mechanism for a sprite fetched at runtime. Added `assets/secretroomsmod/atlases/blocks.json` with an explicit `single` source for `secretroomsmod:block/overlay`.

## Rendering paths checked

- Door and trapdoor models use `SecretMappedModel`, which queries the mapped vanilla model's directional quads and retextures those quads for camouflage. The True Vision branch emits identifier quads only in the solid pass.
- Standing and wall torch levers use `SecretBlockModel`. The True Vision path queries the lever model's geometry without a render-layer filter, then replaces the UVs with the identifier sprite.
- One-way glass uses the vanilla glass model as its fallback silhouette for True Vision and suppresses its normal translucent/camouflage paths while goggles are active.
- `TrueVisionBakedQuad` remaps source UVs into the 16×16 overlay tile and sets full-bright light values.

## Validation

- Checked the source paths and model/texture references.
- Parsed all resource JSON files after edits.
- ZIP integrity checked after packaging.
- Java compilation and in-game rendering were not available in this pass. Therefore, this package is a targeted repair, not proof that all four model categories render correctly in Minecraft.

## Runtime regression cases

1. Enter a world already wearing goggles; confirm secret-block meshes refresh immediately.
2. Equip and remove goggles while standing still.
3. Test all four door states (upper/lower × hinge sides), open/closed and all facings.
4. Test trapdoor top/bottom/open and all facings.
5. Test standing and wall torch levers, both powered states.
6. Test one-way glass with all six face flags in both states.
7. Test blocks at chunk boundaries and relog while wearing goggles.
