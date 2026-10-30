# Secret Rooms — Phase 2 full mechanics repair

Target: Minecraft 1.21.1 / NeoForge

## Applied correction in this package

### True Vision: prevent camouflage texture fallback in mapped models

`SecretMappedModel.getQuads` previously called `TrueVisionBakedQuad.generateQuad` for mapped door/trapdoor quads even if the overlay sprite was not available. `generateQuad` returns the source quad when the sprite is missing, which can leak the original camouflage/model texture into the True Vision render pass. The mapped model now returns no quads for the True Vision solid pass until the overlay sprite is ready, and never returns original quads as an identifier fallback. This intentionally prefers a temporarily absent marker over a wall of copied textures.

### One-way glass state semantics documented

The source confirms that `true` means glass and `false` means camouflage. Placement sets the side facing the placer to `false`, and clicking a side toggles that side. This package does not blindly invert those booleans: doing so would change the intended interaction rather than repair a renderer defect. If the desired mechanic is that clicking should not switch the selected face to glass, that is a design change that needs to be specified explicitly.

## Full mechanics audit — confirmed structural concerns

### Torch lever / wall torch lever

`TorchLever` and `WallTorchLever` extend vanilla `TorchBlock` / `WallTorchBlock`, not `SecretBaseBlock`. They therefore do not expose `SecretTileEntity` mirror state, and `SecretModelHandler` wrapping them in `SecretBlockModel` cannot obtain camouflage state through the normal mirror-state pipeline. This is a confirmed architectural gap and a plausible reason these blocks do not behave like other camouflage blocks. A complete repair requires implementing and registering block-entity/model-data support or redesigning them as a compatible secret block while retaining torch placement/support/shape behavior. This package does not pretend a JSON texture edit fixes that missing data path.

### Doors and trapdoors

Keep the corrected collision and outline shapes. Their rendering is routed through `SecretMappedModel`; the model geometry must come from `MODEL_MAP_STATE`, while the copied appearance comes from `SRM_BLOCKSTATE`. Any further repair should preserve this separation and test both door halves, both hinges, trapdoor top/bottom/open states, and all four facings. No collision geometry was changed in this package.

### One-way glass

`OneWayGlassModel` gets directional quads from the mirrored model and emits vanilla glass quads for properties set to `true`; false faces emit camouflage. The click handler cycles only the clicked face's property and requests a block update. If the copied face disappears after clicking, that is consistent with the current state transition from false to true; decide whether this is intended gameplay before changing the state mapping. Rendering tests should include every direction and opaque, cutout, and translucent camouflage states.

### Ghost blocks

`GhostGroupVisibility` performs a six-direction flood-fill over connected ghost blocks, bounded at 4096 visited positions. This may fail to hide a component larger than the cap. It also needs runtime checks for cross-chunk groups and mesh refresh when the player enters/leaves the connected component. The cap is not changed in this package because removing it without a chunk/loading strategy risks expensive unbounded scans.

### Chests

The current baked-model camouflage path uses a cube fallback for chest/trapped-chest mirrors because vanilla chest rendering is handled by a block-entity renderer. This prevents unrelated chest quads from leaking but cannot reproduce the chest silhouette. A proper chest camouflage implementation needs a dedicated renderer or a deliberately documented simplified silhouette. Inventory persistence, menu interaction, sounds, and trapped-chest output still require runtime regression tests.

### Redstone and interaction checks still required

- Buttons: activation duration, click sound, reset tick, neighbor notification, wall/floor/ceiling support.
- Levers and torch levers: sound, state persistence, weak/strong power, neighbor updates, support validation.
- Pressure plates: entity filters, release delay, output strength, neighbor updates.
- Observer/daylight detector: scheduled updates, orientation, inversion, output transitions.
- Gates/dummy segments: ownership, overlap, removal, chunk reload, and avoiding replacement of unrelated blocks.
- Doors/trapdoors: manual interaction versus redstone, upper/lower-half synchronization, sounds, hinge/orientation, and collision/outline/render alignment.

## Validation status

- Source-level patch applied.
- Resource JSON validation and ZIP integrity check are performed when packaging.
- Java compilation and Minecraft runtime behavior are not verified in this environment. The package must pass the user's GitHub Actions build before gameplay testing.
