# Examples

---

## Validating a login form

```kotlin
@Composable
fun LoginForm(onLogin: (email: String, password: String) -> Unit) {
    var email    by remember { errorSafe("") }
    var password by remember { errorSafe("") }

    val validator = remember(email.value, password.value) {
        FormValidator(
            flow = FormValidator.Flow.Down,
            fields = listOf(
                ValidationField(email.value, "Email", FormValidator.Type.Email) {
                    email = email.copy(error = it)
                },
                ValidationField(password.value, "Password", FormValidator.Type.Required) {
                    password = password.copy(error = it)
                }
            )
        )
    }

    Form(validator) {
        OutlinedTextField(
            value = email.value,
            onValueChange = { email = email.copy(value = it) },
            label = { Text("Email") },
            isError = email.error != null,
            supportingText = { email.error?.let { Text(it) } }
        )
        OutlinedTextField(
            value = password.value,
            onValueChange = { password = password.copy(value = it) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = password.error != null,
            supportingText = { password.error?.let { Text(it) } }
        )
        Button(onClick = {
            if (validator.validate()) onLogin(email.value, password.value)
        }) { Text("Log in") }
    }
}
```

---

## Matching a confirm-password field

```kotlin
var password        by remember { errorSafe("") }
var confirmPassword by remember { errorSafe("") }

val validator = remember(password.value, confirmPassword.value) {
    FormValidator(
        flow = FormValidator.Flow.Down,
        fields = listOf(
            ValidationField(password.value, "Password", FormValidator.Type.Required) {
                password = password.copy(error = it)
            },
            ValidationField(
                value = confirmPassword.value,
                name = "Confirm Password",
                type = FormValidator.Type.MustBeEqualTo(target = password.value)
            ) { confirmPassword = confirmPassword.copy(error = it) }
        )
    )
}
```

---

## Validating a numeric range

```kotlin
var seats by remember { errorSafe(1) }

val validator = remember(seats.value) {
    FormValidator(
        flow = FormValidator.Flow.Splash,
        fields = listOf(
            ValidationField(
                value = seats.value,
                name = "Seats",
                type = FormValidator.Type.MustBeInRange(min = 1, max = 10)
            ) { seats = seats.copy(error = it) }
        )
    )
}
```

---

## Using a custom rule

```kotlin
var username by remember { errorSafe("") }

val validator = remember(username.value) {
    FormValidator(
        flow = FormValidator.Flow.Down,
        fields = listOf(
            ValidationField(
                value = username.value,
                name = "Username",
                type = FormValidator.Type.Custom { value ->
                    when {
                        value.length < 3  -> "At least 3 characters required"
                        value.contains(" ") -> "Spaces are not allowed"
                        else -> null
                    }
                }
            ) { username = username.copy(error = it) }
        )
    )
}
```

---

## Showing a snackbar on validation failure

```kotlin
Form(
    validator = validator,
    snackBarProperties = SnackBarProperties(visibleDuration = 3000)
) {
    // fields ...
    Button(onClick = { validator.validate() }) { Text("Submit") }
}
```

---

## Validating all fields simultaneously with Splash flow

`Flow.Splash` reports errors on every failing field at once instead of stopping at the first one:

```kotlin
val validator = FormValidator(
    flow = FormValidator.Flow.Splash,
    fields = listOf(firstNameField, lastNameField, emailField)
)
```

---

## Gating submit on dirty state

```kotlin
var name  by remember { errorSafe(profile.name) }
var email by remember { errorSafe(profile.email) }

val hasChanges = name.modified || email.modified

Button(
    enabled = hasChanges,
    onClick = { if (validator.validate()) save() }
) { Text("Save changes") }
```
