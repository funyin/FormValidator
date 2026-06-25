# Contributing

## Prerequisites

- JDK 17+
- Android SDK (API 35)
- Kotlin 2.0+

## Local setup

```bash
git clone https://github.com/funyin/FormValidator.git
cd FormValidator

# Build the library
./gradlew :FormValidator:build

# Run JVM unit tests
./gradlew :FormValidator:test

# Run Android instrumented tests (requires a connected device or emulator)
./gradlew connectedAndroidTest
```

## Running the demo app

Open the project in Android Studio and run the `app` configuration on any API 21+ device or emulator.

## Docs setup

```bash
brew install python@3.12        # one-time
python3.12 -m venv venv         # one-time per clone
source venv/bin/activate        # every new terminal
pip install -r requirements.txt
zensical serve
```

The local preview is at `http://127.0.0.1:8000`.

## Opening a pull request

1. Fork the repository and create a branch from `master`.
2. Make your changes and add or update tests where applicable.
3. Run `./gradlew :FormValidator:test` to confirm nothing is broken.
4. Open a pull request against `master`. Describe what changed and why.

## Reporting a bug

Open a [GitHub Issue](https://github.com/funyin/FormValidator/issues/new) and include:

- FormValidator version
- Target platform (Android / JVM / iOS)
- A minimal reproducible example
- Expected vs actual behaviour
