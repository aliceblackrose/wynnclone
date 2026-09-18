# Template architecture

## Purpose

This repository is intentionally a small Paper plugin skeleton. It provides a build, test, packaging, and CI baseline without imposing application architecture before a plugin has real requirements.

## Current structure

- `TemplatePlugin` is the Paper entry point.
- `src/main/resources/plugin.yml` is the authoritative Paper plugin descriptor.
- `src/main/resources/config.yml` is the source configuration resource.
- `src/test/java` contains executable project-contract and behavior tests.
- `build.gradle` owns Java, Paper, testing, run-server, packaging, and publishing configuration.
- `.github/workflows/main.yml` runs the gated build in GitHub Actions.

Generated output is disposable and must not be committed. In particular, copied resources or compiled output under `bin/`, `build/`, IDE output directories, or development-server directories are not source-of-truth files.

## Design rules

1. Keep the plugin entry point small. It should coordinate lifecycle wiring rather than accumulate gameplay or business logic.
2. Add abstractions only when an actual feature requires a boundary. Prefer the simplest adequate design over speculative framework code.
3. Keep Paper/Bukkit integration at explicit boundaries where practical so domain logic can be tested without a live server.
4. Keep persistent/configuration formats centralized rather than duplicating the same concept in multiple files.
5. Prefer Java standard-library functionality and existing project dependencies before adding a third-party dependency.
6. Treat tests and CI as part of the architecture. A change is not complete until the relevant test/build gates pass.
7. Preserve one authoritative source location for each resource. Build tools may copy files into output directories, but those copies are generated artifacts.

## Testing boundary

The initial contract test verifies that the packaged `plugin.yml` continues to identify the expected plugin and entry point. As features are added, add focused tests at the lowest practical level and regression tests for defects.

Code that depends directly on a running Paper server may require integration-oriented testing, but ordinary domain logic should remain testable without starting a server whenever practical.

## Evolution of the architecture

Do not select a larger architecture style in advance. When the plugin gains meaningful subsystems, structure them around the changes the code actually needs to support. If a module becomes expensive to modify, use repository history, tests, and code evidence to decide whether to wrap, restructure, or replace it incrementally.
