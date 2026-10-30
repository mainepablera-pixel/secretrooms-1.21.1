# Initial Integrity Audit Findings

This is an initial static-source audit, not a declaration that the whole mod is fixed.

## Confirmed source-level issues addressed in Phase 1

1. `OneWayGlass#getStateForPlacement` selected the support block face rather than the face toward the placer. It now derives the outward-facing side from `getNearestLookingDirection().getOpposite()`. Rendering orientation still needs in-game verification.
2. `AbstractSecretPressurePlateBase` inherited the mirrored block's collision shape from `SecretBaseBlock`. A full-cube camouflage state could therefore make a secret pressure plate physically behave like a full cube instead of a thin plate. It now supplies thin plate geometry.
3. `AbstractSecretPressurePlateBase#checkPressed` used update flag `2`, which does not request the normal neighbor-update path. It now uses flag `3` so redstone neighbors and clients are notified.
4. `SecretButton` used the activating player as the sound source. Server-side `Level#playSound(player, ...)` excludes that player from the broadcast; a client-side call with the local player can also be suppressed. The button now emits the sound with a null source on the server.
5. `SecretLever` performed sound/game-event handling on both logical sides. It now emits these effects on the server only.

## High-risk areas requiring later phases

- `SecretDoor` and `SecretTrapdoor` have custom shape lookup tables and a custom mapped-model pipeline. These require exhaustive orientation checks; visual and collision orientation must be validated independently.
- `SecretGateBlock` creates and removes dummy blocks while copying the gate's mirrored state. Its replaceable-block handling, line termination, recursive neighboring-gate behavior, and cleanup need tests before changing lifecycle logic.
- `SecretBlockItem` / `SecretDoubleBlockItem`, `SecretTileEntity`, and `SecretModelData` jointly determine copied-state selection, persistence, and rendering. They must be checked together for face placement, stacking, double-block synchronization, and reload behavior.
- Model wrappers (`SecretMappedModel`, `OneWayGlassModel`, `SecretBlockModel`) depend on baked-model quads and model data. Their fallback behavior, render-type handling, quad geometry, tinting, and reload updates need validation.
- The registered block/item/resource matrix has not yet been fully compared with the original archive.

## Validation limitation

`./gradlew compileJava --offline` could not start compilation because the wrapper attempted to download Gradle 9.2.1 from `services.gradle.org`, and DNS resolution failed (`UnknownHostException`). No claim of successful compilation or in-game validation is made.


## Follow-up corrections in this package

- `SecretDoor` now uses directional thin-panel shapes with the correct axis mapping and hinge-dependent swing rotation instead of assigning the front/back direction to the wrong coordinate axis.
- `SecretTrapdoor` open-state shape selection now uses the edge opposite its facing direction, and state changes use neighbor/client update flags. The exact orientation must still be confirmed in-game against the visible model.
- No compilation or runtime test has passed in this environment. Treat these as source-level corrections, not verified gameplay fixes.
