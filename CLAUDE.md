# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FormValidator is a Kotlin Multiplatform library for declarative form validation in Jetpack Compose. It targets Android, JVM (desktop), and iOS. The library is distributed via JitPack (`com.github.funyin:FormValidator`).

## Modules

- **FormValidator/** — The library module (KMP). All shared logic lives in `commonMain`.
- **app/** — Android demo app that exercises the library.

## Build & Test Commands

```bash
# Build everything
./gradlew build

# Run JVM unit tests
./gradlew test

# Run Android instrumented tests
./gradlew connectedAndroidTest

# Clean
./gradlew clean
```

## Architecture

### Core Classes (`FormValidator/src/commonMain/kotlin/com/initbase/formvalidator/`)

**`FormValidator.kt`** — Main orchestrator

- `FormValidator` — Holds `fields: List<ValidationField<*>>`, `flow: Flow`, `errorMessage`, and `valid` (Compose state). Call `validate(): Boolean` to trigger validation.
- `FormValidator.Flow` — Enum controlling validation order: `Down` (top-to-bottom, stops at first error), `Up` (bottom-to-top), `Splash` (all fields at once).
- `FormValidator.Type<T>` — Sealed class for validation rules: `Required`, `MustBeMoreThan`, `MustBeLessThan`, `MustBeInRange`, `MustBeEqualTo`, `Email`, `Custom`, `Optional`.
- `FormValidator.ValidationField<T>` — Represents a single field with `value`, `name`, `type`, `errorMessage`, and an `onError` callback. Call `valid()` to validate the field.

**`Components.kt`** — Compose UI and state helpers

- `Form(validator, content)` — Composable that provides `LocalFormValidator` via `CompositionLocalProvider`.
- `Form(validator, snackBarProperties, content)` — Overload that also shows a `ValidationSnackBar` on validation failure.
- `LocalFormValidator` — `CompositionLocal<FormValidator>` for accessing the validator in nested composables.
- `SnackBarProperties` — Data class controlling the error snackbar appearance and timing (`visibleDuration` defaults to 3000ms).
- `ErrorSafeValue<T>` — Data class that bundles a field's `value`, its original `initial` value, and a nullable `error` string. The computed `modified` property is `true` when `value != initial`.
- `errorSafe(value)` — Convenience function that returns a `MutableState<ErrorSafeValue<T>>`, intended to be used with `remember`.

### Validation Flow

1. Build a `FormValidator` with a list of `ValidationField` instances.
2. Wrap your form UI in `Form(validator) { ... }`.
3. Access `LocalFormValidator.current` in child composables.
4. Call `validator.validate()` on submit; the `Flow` enum controls which fields are checked and in what order.

### ErrorSafe Pattern

`errorSafe` pairs each field's value, error state, and change-tracking into one `remember`-able unit, eliminating separate `value` and `error` state variables.

```kotlin
// Declare state
var firstName by remember { errorSafe(member?.firstName ?: "") }
var lastName  by remember { errorSafe(member?.lastName  ?: "") }
var email     by remember { errorSafe(member?.email     ?: "") }

// Build the validator (rebuild when values change)
val validator = FormValidator(
    flow = FormValidator.Flow.Down,
    fields = buildList {
        add(ValidationField(value = firstName.value, name = "First Name", type = FormValidator.Type.Required) {
            firstName = firstName.copy(error = it)
        })
        add(ValidationField(value = lastName.value, name = "Last Name", type = FormValidator.Type.Required) {
            lastName = lastName.copy(error = it)
        })
        add(ValidationField(value = email.value, name = "Email", type = FormValidator.Type.Email) {
            email = email.copy(error = it)
        })
    }
)

// Live validation as the user types
LaunchedEffect(firstName.value, lastName.value, email.value) {
    validator.validate()
}

// Bind to a text field
AppTextField(
    label = "First Name",
    value = firstName.value,
    onChange = { firstName = firstName.copy(value = it) },
    errorMessage = firstName.error
)
```

The `modified` property on `ErrorSafeValue` can be used to gate submit buttons or dirty-state indicators:
```kotlin
val isDirty = firstName.modified || lastName.modified || email.modified
```

## Publishing

The library is published to JitPack. Version is set in `FormValidator/build.gradle.kts`:
```kotlin
version = "1.0.4"
```
The Maven publication block in `FormValidator/build.gradle.kts` is currently commented out. JitPack builds automatically from git tags.

## Key Config Files

- `gradle/libs.versions.toml` — Centralized dependency versions (Kotlin 2.0.21, AGP 8.5.0, Compose 1.7.1).
- `gradle.properties` — JVM heap (`-Xmx2048m`), AndroidX, and Kotlin code style settings.
- `jitpack.yml` — Specifies OpenJDK 17 for JitPack builds.
