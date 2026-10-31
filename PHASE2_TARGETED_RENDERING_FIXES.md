# Phase 2 targeted rendering fixes

## Included source changes

- True Vision now requests only the solid render pass while goggles are active. This prevents copied-model cutout/translucent passes from rendering over the identifier.
- The mapped door/trapdoor renderer now builds its True Vision silhouette from the mapped vanilla door/trapdoor model, rather than the secret block's fallback model.
- One-way glass also uses the exclusive identifier path while True Vision is active.

## Important limitations

- These are source-level changes. A successful Gradle build and in-game verification have not been performed in this environment.
- The torch lever's black appearance cannot be conclusively diagnosed from its blockstate JSON alone. Inspect latest client logs and compare its mirror-state/model-data path in game before making a texture-only change.
- One-way glass currently uses `true` for glass and `false` for camouflage in `OneWayGlassModel`. Its click behavior changes that property. If the intended design is different, confirm the intended interaction semantics before changing this mapping; blindly reversing it would change placement behavior too.
