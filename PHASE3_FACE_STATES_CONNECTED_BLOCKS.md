# Phase 3 — Face states and connected blocks

## Changes

- Increased the ghost connected-component traversal budget from 4,096 to 16,384 blocks.
- Made the traversal fail closed if the component exceeds the budget. This avoids a partial result in which some connected ghost blocks stay visible while others are hidden, at the cost of hiding an exceptionally large component even when the player is not inside it.
- Kept one-way-glass state semantics unchanged: `true` means that face uses glass; `false` means it displays the mirrored block. Right-clicking a face toggles that face. Thus, clicking a camouflage face will intentionally change it to glass under the current design. This does not resolve a different intended interaction where clicking should preserve camouflage; that behavior must be specified before changing semantics.

## Important limitation

Ghost visibility is computed during model-data generation. This change does not by itself guarantee mesh invalidation whenever the player moves into or out of a connected component. Test movement, chunk boundaries, and reconnects in-game.

## Validation

Resource JSON and ZIP integrity can be checked locally. Java compilation and in-game behavior have not been verified in this environment.
