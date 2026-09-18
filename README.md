# Paper Plugin Template

A minimal Java 25 template for building Paper plugins with Gradle and GitHub Actions.

## Requirements

- Java 25
- Paper 26.1
- Gradle wrapper included in the repository

## Project layout

```text
src/main/java/        Plugin source code
src/main/resources/   Paper resources such as plugin.yml and config.yml
src/test/java/        JUnit tests
.github/workflows/    CI configuration
```

The plugin entry point is `io.github.aliceblackrose.template.TemplatePlugin`, declared in `src/main/resources/plugin.yml`.

## Build and test

Run the same build used by CI:

```bash
./gradlew clean build --warning-mode all --stacktrace
```

The generated JARs are written to `build/libs/`.

## Run a development server

The project uses the `xyz.jpenilla.run-paper` Gradle plugin. Start a local Paper server with:

```bash
./gradlew runServer
```

The configured development server targets Minecraft/Paper 26.1.

## Creating a plugin from this template

1. Change `rootProject.name` in `settings.gradle`.
2. Change the Gradle `group` and `version` values in `build.gradle` as needed.
3. Rename the Java package and `TemplatePlugin` class.
4. Update `main`, `name`, `description`, and other metadata in `src/main/resources/plugin.yml`.
5. Replace the example configuration in `src/main/resources/config.yml`.
6. Add behavior under `src/main/java` and corresponding tests under `src/test/java`.
7. Run `./gradlew clean build` before opening a pull request.

Keep generated build output out of version control. Source resources belong only under `src/main/resources`.

## Continuous integration

GitHub Actions runs the Gradle build on pushes and pull requests targeting `main` or `master`. A successful workflow compiles the plugin, runs the test suite, builds documentation/source artifacts, and uploads the plugin JARs.

## License

GPL-3.0. See `LICENSE`.
