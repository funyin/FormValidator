# ErrorSafe Pattern

`errorSafe` bundles a field's current value, its original value, and its error message into one `remember`-able unit — eliminating three separate state variables per field.

## The `ErrorSafeValue<T>` data class

| Property | Type | Description |
|----------|------|-------------|
| `value` | `T` | Current field value |
| `initial` | `T` | Value at creation time |
| `error` | `String?` | Current error message, or `null` |
| `modified` | `Boolean` | `true` when `value != initial` |

## Declaring state

```kotlin
var firstName by remember { errorSafe("") }
var email     by remember { errorSafe(existingUser?.email ?: "") }
```

The argument to `errorSafe` becomes both the initial value and the baseline for `modified`.

## Reading and writing

```kotlin
// Read
Text(firstName.value)

// Write — copy to preserve other fields
firstName = firstName.copy(value = newValue)

// Clear error
firstName = firstName.copy(error = null)
```

## Wiring to a text field

```kotlin
AppTextField(
    label = "First Name",
    value = firstName.value,
    onChange = { firstName = firstName.copy(value = it) },
    errorMessage = firstName.error
)
```

## Checking dirty state

`modified` is `true` as soon as the user changes the value from its starting point:

```kotlin
val isDirty = firstName.modified || email.modified

Button(enabled = isDirty, onClick = { ... }) {
    Text("Save")
}
```

This is useful for preventing unnecessary API calls when nothing has changed, or for showing a discard-changes prompt.

## Wiring errors from the validator

The `onError` callback on each `ValidationField` receives the error string (or `null` on success). Copy it onto the matching state:

```kotlin
ValidationField(
    value = email.value,
    name = "Email",
    type = FormValidator.Type.Email
) { errorMessage ->
    email = email.copy(error = errorMessage)
}
```
