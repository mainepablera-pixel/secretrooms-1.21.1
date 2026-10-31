# Three-Phase Repair — Phase 1: Directional Face-State Integrity

## Scope
Phase 1 starts with a concrete block-state correctness fix, using the cumulative Phase 5 source package as its baseline. This is not a claim that all requested defects are repaired.

## Implemented
- `OneWayGlass` now delegates rotation and mirroring of its six directional face properties to vanilla `PipeBlock.rotate` and `PipeBlock.mirror`.
- This keeps face flags attached to physical directions when the block is rotated or mirrored by structure operations, commands, or state transformations.
- Existing interaction semantics are preserved: `true` means glass; `false` means mirrored camouflage; empty-hand interaction toggles the clicked face. This phase does not redefine the toggle.

## Remaining work
- Door/trapdoor alignment and collision: compare baked transforms against vanilla shape state-by-state.
- True Vision and torch-lever rendering: compile and runtime render tests required.
- Ghost visibility: test mesh invalidation across group and chunk boundaries.
- Gate ownership/overlap/obstruction and trapped-chest/observer timing: mechanics-specific work and runtime validation remain.

## Validation
Source change reviewed. Resource JSON validation and ZIP integrity are checked during packaging. Java compilation and Minecraft/NeoForge runtime testing were not performed; do not treat this phase as a complete fix.
