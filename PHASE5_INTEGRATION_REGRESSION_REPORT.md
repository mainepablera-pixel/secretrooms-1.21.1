# Phase 5 — Integration and Regression Review

## Scope

This phase reviews the cumulative Phase 1–4 source package and runs checks that can be completed without downloading the Gradle distribution or launching Minecraft. No speculative Java edits were made during integration review: the remaining high-risk behaviors require an actual NeoForge compile and runtime reproduction before they can be safely changed.

## Checks completed

- Parsed all **158 resource JSON files**: no JSON syntax errors.
- Checked the **26 recipes**: all parse, and each object-shaped recipe result uses the 1.21.1 `result.id` form.
- Checked the **26 recipe advancements**: all parse.
- Checked the **21 loot tables**: all parse.
- Confirmed the Phase 3 connected-ghost traversal guard and the Phase 4 observer placement expression are present in the cumulative source.
- Confirmed the trapped chest reads its block entity's open count and caps output at 15 in source.
- Checked ZIP integrity after packaging.

## Build attempt and limitation

Attempted `./gradlew --offline compileJava`. It did not reach Java compilation: the Gradle wrapper attempted to download Gradle 9.2.1 and failed because `services.gradle.org` could not be resolved (`UnknownHostException`). Therefore **compilation has not been verified**. JSON checks and archive integrity do not prove Java API compatibility, mod loading, rendering, or gameplay correctness.

## Regression checklist for the user's GitHub Actions / test world

1. **Build and load:** run the repository's GitHub Actions workflow; resolve any compiler errors before testing in-game.
2. **Door/trapdoor:** verify visible panel, copied texture, upper/lower state synchronization, collision and outline from every facing/open/powered state.
3. **One-way glass:** verify both face states, clicking each face, translucent rendering, and True Vision behavior. Current source semantics are `true` = glass and `false` = mirrored camouflage; clicking toggles the selected face.
4. **Torch levers:** test standing and wall variants for normal appearance, activation, direct/side redstone output, neighbor updates, and removal.
5. **Connected ghost blocks:** test inside/outside visibility, groups crossing chunk boundaries, and components exceeding 16,384 blocks. The overflow guard intentionally hides on overflow to avoid partially visible connected groups.
6. **Chests:** verify model geometry, inventory save/reload, opening/closing sounds, trapped-chest signal with one and multiple viewers, and removal behavior.
7. **Gate:** test lengths and obstruction safety, save/reload, overlapping or adjacent gates, and breaking a gate while extended.
8. **Redstone components:** test buttons, all pressure-plate modes, observer direction/pulse, secret redstone propagation, and daylight detector output during time/weather changes.
9. **Multiplayer:** repeat state-sensitive tests on a dedicated server with a remote client to catch synchronization issues.
10. **Data and recipes:** load every recipe and loot table in-game and check logs for missing models, textures, registry names, or data warnings.

## Release status

**Not release-verified.** This is the cumulative Phase 1–5 source package with static resource validation. It must not be described as fully fixed or ready for release until a successful Java/NeoForge build and the gameplay regression checklist have passed. Confirm source and asset redistribution rights before publishing; the project metadata currently states `All Rights Reserved`.
