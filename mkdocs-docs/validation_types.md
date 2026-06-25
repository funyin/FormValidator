# Validation Types

All validation rules are members of `FormValidator.Type<T>`, a sealed class.

## Required

Fails when the value is blank or null.

```kotlin
ValidationField(value = username.value, name = "Username", type = FormValidator.Type.Required) {
    username = username.copy(error = it)
}
```

## Email

Checks that the value matches a valid email address format.

```kotlin
ValidationField(value = email.value, name = "Email", type = FormValidator.Type.Email) {
    email = email.copy(error = it)
}
```

## MustBeMoreThan

Fails when a numeric value is not greater than `min`.

```kotlin
ValidationField(
    value = age.value,
    name = "Age",
    type = FormValidator.Type.MustBeMoreThan(min = 18)
) { age = age.copy(error = it) }
```

## MustBeLessThan

Fails when a numeric value is not less than `max`.

```kotlin
ValidationField(
    value = discount.value,
    name = "Discount",
    type = FormValidator.Type.MustBeLessThan(max = 100)
) { discount = discount.copy(error = it) }
```

## MustBeInRange

Fails when a numeric value falls outside `[min, max]`.

```kotlin
ValidationField(
    value = quantity.value,
    name = "Quantity",
    type = FormValidator.Type.MustBeInRange(min = 1, max = 99)
) { quantity = quantity.copy(error = it) }
```

## MustBeEqualTo

Fails when the value does not equal `target`. Useful for confirm-password fields.

```kotlin
ValidationField(
    value = confirmPassword.value,
    name = "Confirm Password",
    type = FormValidator.Type.MustBeEqualTo(target = password.value)
) { confirmPassword = confirmPassword.copy(error = it) }
```

## Custom

Provide your own predicate. Return `null` to pass, or an error message string to fail.

```kotlin
ValidationField(
    value = username.value,
    name = "Username",
    type = FormValidator.Type.Custom { value ->
        if (value.length < 3) "Username must be at least 3 characters" else null
    }
) { username = username.copy(error = it) }
```

## Optional

Skips validation entirely — the field is always considered valid. Use for fields that should participate in `Flow.Down` ordering without blocking submission.

```kotlin
ValidationField(
    value = bio.value,
    name = "Bio",
    type = FormValidator.Type.Optional
) { bio = bio.copy(error = it) }
```

---

## Validation flow

`FormValidator.Flow` controls which fields are checked and in what order:

| Flow | Behaviour |
|------|-----------|
| `Down` | Top-to-bottom; stops at the first failing field |
| `Up` | Bottom-to-top; stops at the first failing field |
| `Splash` | All fields validated simultaneously |

```kotlin
FormValidator(flow = FormValidator.Flow.Splash, fields = ...)
```
