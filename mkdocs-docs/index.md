---
comments: true
---

# FormValidator

Declarative form validation for Jetpack Compose — Android, JVM, and iOS.

FormValidator lets you define validation rules alongside your fields, then call `validate()` once on submit. Errors flow back to each field via callbacks, and the `ErrorSafe` pattern keeps value, error, and dirty-state in a single `remember`-able unit.

=== "Kotlin Gradle"

    ```kotlin
    implementation("com.github.funyin:FormValidator:LIBRARY_VERSION")
    ```

=== "Groovy"

    ```groovy
    implementation 'com.github.funyin:FormValidator:LIBRARY_VERSION'
    ```

!!! tip "Latest version"
    Find the current release on the [releases page](https://github.com/funyin/FormValidator/releases).

---

**At a glance**

=== "Declare"

    ```kotlin
    val validator = FormValidator(
        flow = FormValidator.Flow.Down,
        fields = listOf(
            ValidationField(value = email.value, name = "Email", type = FormValidator.Type.Email) {
                email = email.copy(error = it)
            }
        )
    )
    ```

=== "Wrap"

    ```kotlin
    Form(validator) {
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

=== "Validate"

    ```kotlin
    // Live validation as the user types
    LaunchedEffect(email.value) { validator.validate() }

    // Gate submit
    Button(
        enabled = validator.valid,
        onClick = { validator.validate(); submit() }
    ) { Text("Submit") }
    ```
