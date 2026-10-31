# Build Error Correction — One-Way Glass Direction Transform

## Trigger
The uploaded GitHub Actions log `logs_110804425849.zip` shows that `:compileJava` failed with exactly two Java errors in `OneWayGlass.java`:
- `PipeBlock.rotate(state, rotation)` was inaccessible because the inherited helper is protected.
- `PipeBlock.mirror(state, mirror)` was inaccessible for the same reason.

These errors were introduced by the earlier directional-face-state patch. The prior claim that the repair package was ready to build was incorrect.

## Correction
Replaced calls to those protected helpers with explicit directional-property remapping:
- `rotate` maps each of the six face properties to the direction returned by `Rotation.rotate`.
- `mirror` maps each of the six face properties to the direction returned by `Mirror.mirror`.
- Both transformations read property values from the original state and apply them to the transformed directions.

The change is applied to the compiled source and the clean-source copy.

## Validation
- The uploaded log was inspected; the failure is a Java compile error, not a Gradle download/network error.
- Resource JSON files were parsed.
- ZIP integrity was checked.
- This environment does not have the full NeoForge Gradle dependency cache needed to confirm compilation. Run GitHub Actions again to verify the fix. If compilation reports a method-signature issue for `Mirror.mirror(Direction)` in the project's mappings, use that new log to correct it rather than assuming success.
