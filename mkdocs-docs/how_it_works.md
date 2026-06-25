# How It Works

## Overview

```
FormValidator(flow, fields)
        │
        ▼
  validator.validate()
        │
        ├─ Flow.Down  → iterate fields top-to-bottom, stop on first error
        ├─ Flow.Up    → iterate fields bottom-to-top, stop on first error
        └─ Flow.Splash → validate all fields, collect all errors
        │
        ▼
  ValidationField.valid()
        │
        ├─ Type.Required     → blank/null check
        ├─ Type.Email        → regex check
        ├─ Type.MustBeMoreThan / MustBeLessThan / MustBeInRange → numeric compare
        ├─ Type.MustBeEqualTo → equality check
        ├─ Type.Custom       → caller-supplied predicate
        └─ Type.Optional     → always passes
        │
        ▼
  onError(message?) called on each field
        │
        ▼
  validator.valid (Compose state) updated
```

## Composition local

`Form(validator) { ... }` calls `CompositionLocalProvider(LocalFormValidator provides validator)`. Any composable nested inside can read the current validator without prop-drilling:

```kotlin
val validator = LocalFormValidator.current
```

## Snackbar variant

`Form(validator, snackBarProperties) { ... }` shows a `ValidationSnackBar` automatically when `validate()` returns `false`. `SnackBarProperties` controls the message text, duration, and appearance.

```kotlin
Form(
    validator = validator,
    snackBarProperties = SnackBarProperties(visibleDuration = 4000)
) { ... }
```

## `validate()` return value

`validate()` returns `true` only when every field that was checked passed. Use it to gate submission:

```kotlin
Button(onClick = {
    if (validator.validate()) {
        submitForm()
    }
}) { Text("Submit") }
```

## `validator.valid` state

`validator.valid` is a Compose `State<Boolean>` that reflects the outcome of the last `validate()` call. It recomposes any composable that reads it:

```kotlin
Button(enabled = validator.valid, onClick = { submit() }) {
    Text("Submit")
}
```

## `validator.errorMessage`

When `Flow.Down` or `Flow.Up` stops at the first failing field, `validator.errorMessage` holds that field's error string. Useful for displaying a top-level error summary:

```kotlin
if (!validator.valid) {
    Text(validator.errorMessage ?: "Please fix the errors above", color = MaterialTheme.colors.error)
}
```
