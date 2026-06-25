# Quick Start

## 1. Declare field state with `errorSafe`

```kotlin
var firstName by remember { errorSafe("") }
var lastName  by remember { errorSafe("") }
var email     by remember { errorSafe("") }
```

## 2. Build a `FormValidator`

```kotlin
val validator = FormValidator(
    flow = FormValidator.Flow.Down,
    fields = buildList {
        add(ValidationField(
            value = firstName.value,
            name = "First Name",
            type = FormValidator.Type.Required
        ) { firstName = firstName.copy(error = it) })

        add(ValidationField(
            value = lastName.value,
            name = "Last Name",
            type = FormValidator.Type.Required
        ) { lastName = lastName.copy(error = it) })

        add(ValidationField(
            value = email.value,
            name = "Email",
            type = FormValidator.Type.Email
        ) { email = email.copy(error = it) })
    }
)
```

!!! tip
    Rebuild the validator inside `remember(firstName.value, lastName.value, email.value)` so the field values it captures stay current.

## 3. Wrap your UI in `Form`

```kotlin
Form(validator) {
    AppTextField(
        label = "First Name",
        value = firstName.value,
        onChange = { firstName = firstName.copy(value = it) },
        errorMessage = firstName.error
    )
    AppTextField(
        label = "Last Name",
        value = lastName.value,
        onChange = { lastName = lastName.copy(value = it) },
        errorMessage = lastName.error
    )
    AppTextField(
        label = "Email",
        value = email.value,
        onChange = { email = email.copy(value = it) },
        errorMessage = email.error
    )
    Button(onClick = { if (validator.validate()) submit() }) {
        Text("Submit")
    }
}
```

## 4. Optionally enable live validation

```kotlin
LaunchedEffect(firstName.value, lastName.value, email.value) {
    validator.validate()
}
```

## 5. Gate the submit button on dirty state

```kotlin
val isDirty = firstName.modified || lastName.modified || email.modified

Button(
    enabled = isDirty,
    onClick = { if (validator.validate()) submit() }
) { Text("Submit") }
```
