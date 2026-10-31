# Secret Rooms — Minecraft 1.21.1 / NeoForge

This repository contains a source port of Secret Rooms targeting Minecraft 1.21.1 and NeoForge.

## Requirements

- Java 21
- Internet access for Gradle and Minecraft/NeoForge dependency downloads

## Build

Linux/macOS:

```sh
chmod +x gradlew
./gradlew build --no-daemon
```

Windows:

```bat
gradlew.bat build --no-daemon
```

The built JAR should be produced under `build/libs/` after a successful build.

## Current status

This is a **source port under validation**, not a verified release. The source includes fixes and compatibility work for Minecraft 1.21.1, including camouflage state handling, secret door/trapdoor behavior and sounds, one-way glass rendering, and other ported features. The full Gradle build and in-game behavior have not been verified in the current environment because the Gradle distribution download could not resolve `services.gradle.org`.

Before publishing a release, run the build workflow and test at minimum: camouflage placement and stacked secret blocks; directional blocks such as stairs; one-way glass; secret doors and trapdoors with manual and redstone activation; gate behavior; redstone propagation; Switch Probe; and True Vision Goggles. See `BUILD_NOTES.md` and `PORT_COMPARISON_AUDIT.md` for known limitations.

## Automated build

A GitHub Actions workflow is provided at `.github/workflows/build.yml`. It builds with Java 21 and uploads the resulting JAR as a workflow artifact.

## Licensing

The mod metadata currently declares `All Rights Reserved`. Confirm you have permission to publish and distribute this source and any derived assets before making the repository public. Do not change the license declaration unless you have authorization to do so.
