# 2026-10-30 — In-game rendering fixes (from first successful build)

Source of truth: screenshots from a real run of the compiled jar (MC 1.21.1 / NeoForge 21.1.x).

## 1. True Vision showed the whole block/item atlas; doors & trapdoors showed the atlas
**Cause:** `TrueVisionBakedQuad` and `NoTintBakedQuadRetextured` remapped UVs with
`sprite.getU(local * 16.0F)`. In 1.21.1 `getU/getV` take a 0..1 fraction, so each face sampled a
16x16-sprite area of the atlas. Doors/trapdoors use the same retexture path, so they looked identical.
**Fix:** new `QuadUvRemapper` interpolates directly between `getU0/getU1/getV0/getV1` (version-proof).
Both quad classes now use it. Geometry/hitboxes were never touched.

## 2. Torch lever rendered as a large black cross/star
**Cause:** vanilla's torch model is two full 16x16 crossed planes with a mostly transparent texture.
`torch_lever` / `wall_torch_lever` are not in vanilla's cutout list, so they were drawn in the SOLID
layer where transparent pixels are black. (The wrapper also forced SOLID while goggles were worn.)
**Fix:** `SecretBlockModel` now returns CUTOUT for torch levers and emits their vanilla quads untouched
(they are ordinary visible torches: no block entity, no camouflage, no True Vision marker).
`render_type: minecraft:cutout` was also added to `torch_lever_standing.json` / `torch_lever_wall.json`.

## 3. One-way glass: clicking made the copied face vanish, click again made it return
**Cause:** a click toggled the clicked face, so the copied face became clear glass.
**Fix (final):** the empty-hand click handler was removed. The copied face chosen at placement is
constant; clicking never toggles, moves or hides any face.

## 4. Door / trapdoor stale shape
`SecretMappedModel` now derives the vanilla door/trapdoor state from the live block state passed to the
renderer instead of only the cached ModelData, so open/close/hinge/power changes cannot lag.

## Not verified
No Minecraft/NeoForge jars or network were available when these edits were made. The changed Java files
were parse-checked only (no syntax errors); they were not compiled or run. Test in game:
goggles on/off, all door + trapdoor states, standing + wall torch levers (both powered states),
one-way glass click on every face.

## 5. Doors could not be placed on leaves
`SecretDoor.canSurvive` required a sturdy top face below the lower half; leaves are not sturdy. It now also
accepts leaves and any block with a full-height collision box.
