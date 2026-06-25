# FormValidator

[![Maven Central](https://img.shields.io/maven-central/v/com.funyinkash/FormValidator.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.funyinkash/FormValidator)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FormValidator-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/8353)
[![Made in Nigeria](https://img.shields.io/badge/made%20in-nigeria-008751.svg?style=flat-square)](https://github.com/acekyd/made-in-nigeria)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin%20Multiplatform-Android%20%7C%20iOS%20%7C%20Desktop-7F52FF?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)

A declarative form validation library for **Kotlin Multiplatform** + Jetpack Compose.  
Targets **Android**, **iOS**, and **Desktop (JVM)**.

**[Documentation](https://funyin.github.io/FormValidator/)
** · [Installation](#installation) · [Quick start](#quick-start) · [API reference](https://funyin.github.io/FormValidator/kdoc/latest/)

---

## Preview

https://user-images.githubusercontent.com/38915569/149603789-1f47436b-b8f9-44a6-98a6-6ec389a75e7b.mp4

---

## Features

- **Validation flow** — `Down` (stop at first error), `Up` (stop at last error), `Splash` (all
  fields at once)
- **Validation types** — Required, Email, range / equality checks, fully Custom, Optional
- **ErrorSafe** — single state object per field bundles value + error + dirty-tracking
- **Validation snackbar** — built-in auto-dismissing error snackbar with configurable appearance
- **Live validation** — designed to work with `LaunchedEffect` for as-you-type feedback

---

## Installation

Available on **Maven Central** — no extra repository setup needed.

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.funyinkash:FormValidator:1.0.6")
}
```

<details>
<summary>Groovy DSL</summary>

```groovy
dependencies {
    implementation 'com.funyinkash:FormValidator:1.0.6'
}
```

</details>

| Target        | Minimum                             |
|---------------|-------------------------------------|
| Android       | API 21                              |
| JVM (desktop) | JVM 8                               |
| iOS           | iosArm64, iosX64, iosSimulatorArm64 |

---

## Quick start

### 1. Declare state with `errorSafe`

`errorSafe` bundles each field's value, validation error, and dirty-tracking into one `remember`
-able unit:

```kotlin
var firstName by remember { errorSafe("") }
var lastName by remember { errorSafe("") }
var email by remember { errorSafe("") }
```

### 2. Build a `FormValidator`

```kotlin
val validator = remember(firstName.value, lastName.value, email.value) {
    FormValidator(
        flow = FormValidator.Flow.Down,
        fields = buildList {
            add(ValidationField(firstName.value, "First Name", FormValidator.Type.Required) {
                firstName = firstName.copy(error = it)
            })
            add(ValidationField(lastName.value, "Last Name", FormValidator.Type.Required) {
                lastName = lastName.copy(error = it)
            })
            add(ValidationField(email.value, "Email", FormValidator.Type.Email) {
                email = email.copy(error = it)
            })
        }
    )
}
```

### 3. Wrap your UI in `Form` and call `validate()`

```kotlin
Form(validator) {
    OutlinedTextField(
        value = firstName.value,
        onValueChange = { firstName = firstName.copy(value = it) },
        label = { Text("First Name") },
        isError = firstName.error != null,
        supportingText = { firstName.error?.let { Text(it) } }
    )
    // ... remaining fields ...
    Button(onClick = { if (validator.validate()) submit() }) {
        Text("Submit")
    }
}
```

### 4. Optionally enable live validation and dirty-state gating

```kotlin
LaunchedEffect(firstName.value, lastName.value, email.value) {
    validator.validate()
}

val isDirty = firstName.modified || lastName.modified || email.modified
Button(enabled = isDirty, onClick = { if (validator.validate()) submit() }) {
    Text("Save")
}
```

---

## Validation types

| Type                                  | Behaviour                                                    |
|---------------------------------------|--------------------------------------------------------------|
| `Required`                            | Non-null; strings must also be non-blank                     |
| `Email`                               | Validates against a standard email pattern                   |
| `MustBeMoreThan(n)`                   | Numeric value must exceed `n`                                |
| `MustBeLessThan(n)`                   | Numeric value must be below `n`                              |
| `MustBeEqualTo(target)`               | Value must equal `target` — useful for confirm-password      |
| `MustBeInRange(min, max)`             | Numeric value must fall within `[min, max]`                  |
| `Custom { value -> "error" \| null }` | Fully custom predicate                                       |
| `Optional`                            | Always valid; participates in flow ordering without blocking |

---

## Validation flow

| Flow     | Behaviour                                             |
|----------|-------------------------------------------------------|
| `Down`   | Top-to-bottom; stops at the first failing field       |
| `Up`     | Bottom-to-top; stops at the first failing field       |
| `Splash` | All fields at once; every failing field gets an error |

---

## Snackbar integration

```kotlin
Form(
    validator = validator,
    snackBarProperties = SnackBarProperties(visibleDuration = 3000)
) {
    // form content
    Button(onClick = { validator.validate() }) { Text("Submit") }
}
```

---

## `ErrorSafeValue<T>` properties

| Property   | Type      | Description                                 |
|------------|-----------|---------------------------------------------|
| `value`    | `T`       | Current field value                         |
| `initial`  | `T`       | Value at construction time                  |
| `error`    | `String?` | Current validation error, `null` when valid |
| `modified` | `Boolean` | `true` when `value != initial`              |

---

## Documentation

Full documentation including guides, examples, and KDoc API reference:  
**https://funyin.github.io/FormValidator/**

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
