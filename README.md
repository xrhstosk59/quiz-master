# quiz-master

A small JavaFX quiz application used as a learning project and migrated from a legacy NetBeans/Ant setup to a portable Maven build.

## Requirements

- Java 24
- `curl` or `wget`

## Run

```bash
./mvnw javafx:run
```

## Build

```bash
./mvnw clean package
```

## Notes

- This repository is focused on cleanup, portability, and easier local execution.
- The project uses OpenJFX `24.0.1`.
- Audio files and FXML views are loaded from the classpath, so the app no longer depends on IDE-specific working directories.
