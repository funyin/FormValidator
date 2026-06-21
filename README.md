# FormValidator

[![Maven Central](https://img.shields.io/maven-central/v/com.funyinkash/FormValidator.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.funyinkash/FormValidator)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FormValidator-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/8353)
[![Made in Nigeria](https://img.shields.io/badge/made%20in-nigeria-008751.svg?style=flat-square)](https://github.com/acekyd/made-in-nigeria)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin%20Multiplatform-Android%20%7C%20iOS%20%7C%20Desktop-7F52FF?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)

A declarative form validation library for **Kotlin Multiplatform** + Jetpack Compose.  
Targets **Android**, **iOS**, and **Desktop (JVM)**.

## Preview

https://user-images.githubusercontent.com/38915569/149603789-1f47436b-b8f9-44a6-98a6-6ec389a75e7b.mp4

## Features

- **Validation flow** — `Down` (stop at first error), `Up` (stop at last error), `Splash` (all fields at once)
- **Validation types** — Required, Email, range / equality / length checks, fully Custom
- **ErrorSafe** — single state object per field bundles value + error + dirty-tracking
- **Validation Snackbar** — built-in auto-dismissing error snackbar with configurable appearance
- **Live validation** — designed to work with `LaunchedEffect` for as-you-type feedback

## Installation

Available on **Maven Central** — no extra repository setup needed.

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.funyinkash:FormValidator:1.0.5")
}
```

<details>
<summary>Groovy DSL</summary>

```groovy
dependencies {
    implementation 'com.funyinkash:FormValidator:1.0.5'
}
```

</details>

## Usage

### Basic form

```kotlin
var name      by remember { mutableStateOf("") }
var nameError by remember { mutableStateOf<String?>(null) }
var email      by remember { mutableStateOf("") }
var emailError by remember { mutableStateOf<String?>(null) }

val validator = FormValidator(
    flow = FormValidator.Flow.Down,
    fields = listOf(
        ValidationField(value = name, name = "Name", type = FormValidator.Type.Required) {
            nameError = it
        },
        ValidationField(value = email, name = "Email", type = FormValidator.Type.Email) {
            emailError = it
        }
    )
)

Form(validator = validator) {
    TextField(value = name, onValueChange = { name = it }, ...)
    if (nameError != null) Text(nameError!!, color = MaterialTheme.colors.error)

    TextField(value = email, onValueChange = { email = it }, ...)
    if (emailError != null) Text(emailError!!, color = MaterialTheme.colors.error)

    Button(onClick = { validator.validate() }) { Text("Submit") }
}
```

---

### ErrorSafe pattern (recommended)

`errorSafe` bundles each field's value, validation error, and change-tracking into one
`remember`-able unit — eliminating the separate `value` / `error` state variables:

```kotlin
var firstName by remember { errorSafe("") }
var lastName  by remember { errorSafe("") }
var email     by remember { errorSafe("") }

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

// Gate the submit button on dirty state
val isDirty = firstName.modified || lastName.modified || email.modified

TextField(
    value = firstName.value,
    onValueChange = { firstName = firstName.copy(value = it) },
    ...
)
if (firstName.error != null) Text(firstName.error!!, color = MaterialTheme.colors.error)
```

`ErrorSafeValue<T>` properties:

| Property | Type | Description |
|---|---|---|
| `value` | `T` | Current field value |
| `initial` | `T` | Value at construction time |
| `error` | `String?` | Current validation error, `null` when valid |
| `modified` | `Boolean` | `true` when `value != initial` |

---

### Validation types

| Type | Behaviour |
|---|---|
| `Required` | Non-null; strings must also be non-blank |
| `Email` | Validates against RFC-compliant email pattern |
| `MustBeMoreThan(n)` | String: `length > n`; Number: `value > n` |
| `MustBeLessThan(n)` | String: `length < n`; Number: `value < n` |
| `MustBeEqualTo(n)` | String + Number template: `length == n`; String + String: exact match; Number: `value == n` |
| `MustBeInRange(min, max)` | Number inclusive between `min` and `max` |
| `Custom { value -> Pair(result, message) }` | Provide your own validation logic |
| `Optional` | Always valid |

---

### Validation flow

| Flow | Behaviour |
|---|---|
| `Down` | Validates top to bottom; stops at the first invalid field |
| `Up` | Validates all fields; reports the last invalid field |
| `Splash` | Validates all fields simultaneously; calls `onError` on every invalid field |

---

### Snackbar form

Use the `Form` overload with `SnackBarProperties` to show an auto-dismissing error
snackbar instead of per-field errors. Best paired with `Flow.Down` or `Flow.Up`.

```kotlin
Form(
    validator = validator,
    snackBarProperties = SnackBarProperties(
        title = "Validation Error",
        backgroundColor = Color.DarkGray.copy(alpha = 0.9f),
        visibleDuration = 3000.milliseconds
    )
) {
    // form content
    Button(onClick = { validator.validate() }) { Text("Submit") }
}
```

`SnackBarProperties` options:

| Property | Default | Description |
|---|---|---|
| `title` | `"Validation Error"` | Snackbar title |
| `message` | validator error | Populated automatically from `validator.errorMessage` |
| `backgroundColor` | `MaterialTheme.colors.surface` | Snackbar background |
| `shape` | `RoundedCornerShape(8.dp)` | Snackbar shape |
| `visibleDuration` | `3000ms` | How long the snackbar stays visible |
| `enterTransition` | `fadeIn() + expandIn()` | Entry animation |
| `exitTransition` | `shrinkOut() + fadeOut()` | Exit animation |

---

## API Reference

Full KDoc API reference → **https://funyin.github.io/FormValidator/**

---

## License

```
Copyright 2021 Funyinoluwa Kashimawo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

---

[![BuyMeAShawrma.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1619907239535/KqJOyu-70.png)](https://www.buymeacoffee.com/funyinkash)
