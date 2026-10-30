# Phase 2 — Door and Trapdoor Geometry / State Integrity

Target: Minecraft 1.21.1, NeoForge.

## Changes in this phase

- Reworked `SecretDoor`'s thin-panel voxel shapes so each cardinal direction maps to the matching block edge: north/south use thin Z-axis panels; east/west use thin X-axis panels.
- Reversed the previous open-door hinge rotation, which was selecting the opposite side for each hinge.
- Reworked `SecretTrapdoor`'s open-panel mapping so `FACING` selects the corresponding edge rather than the opposite edge.
- Kept outline and collision geometry on the same state-derived shape, avoiding a mismatch between targeting and movement.
- Changed manual and redstone door state writes to update flags `3` so neighboring/client state updates are not suppressed.
- Retained paired upper/lower door synchronization and model-data refreshes.

## Static review checklist

- [x] Door and trapdoor shapes no longer delegate to inaccessible protected vanilla methods.
- [x] Door cardinal shape axes and trapdoor open-edge mapping are explicit.
- [x] Door hinge rotation is no longer the previously inverted selection.
- [x] Door transitions update both halves and request model refreshes.
- [x] Report records remaining validation requirements.

## Not yet verified

- Java compilation: not run successfully in this environment because the Gradle wrapper distribution is not cached and `services.gradle.org` is unreachable here.
- In-game hitboxes: not verified for all four facings, both hinges, both halves, open/closed states, and client/server interaction.
- Redstone transitions: source-level review only; requires gameplay tests with power applied to either half.
- Model/collision alignment: requires a running Minecraft client with representative copied block models.

## Required in-game regression matrix

1. Place a secret door facing north/east/south/west; test closed and open with left/right hinges.
2. Target the visible panel from both sides and verify the outline is on the panel rather than the opposite edge.
3. Walk through the doorway when open and verify the closed door blocks movement.
4. Test top and bottom halves for consistent facing, hinge, open, powered, and camouflage state.
5. Repeat for secret trapdoors with top/bottom placement, all four facings, and open/closed states.
6. Apply and remove redstone power to the upper and lower door positions and to the trapdoor; verify state, sounds, and client synchronization.
7. Break either door half and confirm the other half is removed without leaving an orphaned block entity.
