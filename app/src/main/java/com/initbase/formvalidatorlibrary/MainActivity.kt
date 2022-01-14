package com.initbase.formvalidatorlibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.initbase.formvalidator.Form
import com.initbase.formvalidator.FormValidator
import com.initbase.formvalidator.FormValidator.ValidationField
import com.initbase.formvalidator.SnackBarProperties
import com.initbase.formvalidatorlibrary.components.AppButton
import com.initbase.formvalidatorlibrary.components.AppTextField
import com.initbase.formvalidatorlibrary.ui.theme.FormValidatorLibraryTheme

class MainActivity : ComponentActivity() {

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }

    var formTypes = listOf("Regular Form", "Snackbar Form")

    @ExperimentalMaterialApi
    @Preview
    @Composable
    private fun Content() {
        FormValidatorLibraryTheme {
            // A surface container using the 'background' color from the theme
            Surface(color = MaterialTheme.colors.background) {
                Column(modifier = Modifier.fillMaxSize()) {
                    var activeForm by remember { mutableStateOf(0) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        formTypes.forEachIndexed { index, s ->
                            AppButton(
                                text = s,
                                modifier = Modifier.weight(1f),
                                background = if (activeForm == index) MaterialTheme.colors.primary else Color.Gray
                            ) {
                                activeForm = index
                            }
                            if (index == 0)
                                Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    if (activeForm == 0)
                        RegularForm()
                    else
                        SnackBarForm()
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun SnackBarForm() {
        var formIsValid by remember { mutableStateOf(false) }
        var name by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        val nameField = "Name"
        val ageField = "Age"
        val emailField = "Email"
        var activeFlow by remember { mutableStateOf(FormValidator.Flow.Down) }
        val validator = FormValidator(
            flow = activeFlow,
            fields = listOf(
                ValidationField(
                    value = name,
                    name = nameField,
                ),
                ValidationField(
                    value = age.toIntOrNull(),
                    name = ageField, type = FormValidator.Type.Custom {
                        (it != null && it % 2 == 0) to "Age must be divisible by two"
                    }
                ),
                ValidationField(
                    value = email, type = FormValidator.Type.Email
                )
            )
        )
        Form(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(16.dp),
            validator = validator,
            snackBarProperties = SnackBarProperties(backgroundColor = Color.Gray.copy(alpha = 0.8f)),
        ) {
            Text(
                text = "Flow",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                style = MaterialTheme.typography.subtitle1
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                (0..2).forEachIndexed { index, i ->
                    val active: Boolean
                    val text = when (index) {
                        0 -> {
                            active = activeFlow == FormValidator.Flow.Down
                            "Down"
                        }
                        1 -> {
                            active = activeFlow == FormValidator.Flow.Up
                            "Up"
                        }
                        else -> {
                            active = activeFlow == FormValidator.Flow.Splash
                            "Splash"
                        }
                    }
                    Surface(
                        onClick = {
                            activeFlow = when (index) {
                                0 -> FormValidator.Flow.Down
                                1 -> FormValidator.Flow.Up
                                else -> FormValidator.Flow.Splash
                            }
                        },
                        color = if (active) MaterialTheme.colors.primary else Color.LightGray,
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(
                            text = text,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                    }
                    if (index != 2) {
                        Divider(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .width(16.dp)
                                .rotate(90f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            AppTextField(
                label = nameField,
                value = name,
                placeholder = "Enter name",
                onValueChanged = { name = it },
            )
            FormVerticalSpace()
            AppTextField(
                label = ageField,
                value = age,
                placeholder = "Enter age",
                onValueChanged = { age = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            FormVerticalSpace()
            AppTextField(
                label = emailField,
                value = email,
                placeholder = "Enter email",
                onValueChanged = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(40.dp))
            AppButton(text = "Submit") {
                formIsValid = validator.validate()
            }
            Spacer(modifier = Modifier.height(40.dp))
            Card(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = if (formIsValid) Color.Green else MaterialTheme.colors.error,
                elevation = 0.dp
            ) {
                Text(
                    text = if (formIsValid) "Form is valid" else "Form is not valid",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun RegularForm() {
        var formIsValid by remember { mutableStateOf(false) }
        var name by remember { mutableStateOf("") }
        var nameError by remember { mutableStateOf<String?>(null) }
        var age by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var ageError by remember { mutableStateOf<String?>(null) }
        var emailError by remember { mutableStateOf<String?>(null) }
        var formError by remember { mutableStateOf<String?>(null) }
        val nameField = "Name"
        val ageField = "Age"
        val emailField = "Email"
        var activeFlow by remember { mutableStateOf(FormValidator.Flow.Down) }
        val validator = FormValidator(
            flow = activeFlow,
            fields = listOf(
                ValidationField(
                    value = name,
                    name = nameField,
                    onError = {
                        nameError = it
                    }),
                ValidationField(
                    value = age.toIntOrNull(),
                    name = ageField,
                    onError = {
                        ageError = it
                    }, type = FormValidator.Type.Custom {
                        (it != null && it % 2 == 0) to "Age must be divisible by two"
                    }
                ),
                ValidationField(
                    value = email,
                    onError = {
                        emailError = it
                    }, type = FormValidator.Type.Email
                )
            )
        )
        Form(
            validator = validator,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Flow",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                style = MaterialTheme.typography.subtitle1
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                (0..2).forEachIndexed { index, i ->
                    val active: Boolean
                    val text = when (index) {
                        0 -> {
                            active = activeFlow == FormValidator.Flow.Down
                            "Down"
                        }
                        1 -> {
                            active = activeFlow == FormValidator.Flow.Up
                            "Up"
                        }
                        else -> {
                            active = activeFlow == FormValidator.Flow.Splash
                            "Splash"
                        }
                    }
                    Surface(
                        onClick = {
                            activeFlow = when (index) {
                                0 -> FormValidator.Flow.Down
                                1 -> FormValidator.Flow.Up
                                else -> FormValidator.Flow.Splash
                            }
                        },
                        color = if (active) MaterialTheme.colors.primary else Color.LightGray,
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(
                            text = text,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                    }
                    if (index != 2) {
                        Divider(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .width(16.dp)
                                .rotate(90f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            AppTextField(
                label = nameField,
                value = name,
                placeholder = "Enter name",
                onValueChanged = { name = it },
                errorMessage = nameError
            )
            FormVerticalSpace()
            AppTextField(
                label = ageField,
                value = age,
                placeholder = "Enter age",
                onValueChanged = { age = it },
                errorMessage = ageError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            FormVerticalSpace()
            AppTextField(
                label = emailField,
                value = email,
                placeholder = "Enter email",
                onValueChanged = { email = it },
                errorMessage = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(40.dp))
            AppButton(text = "Submit") {
                formIsValid = validator.validate()
                formError = validator.errorMessage
            }
            Spacer(modifier = Modifier.height(40.dp))
            Card(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = if (formIsValid) Color.Green else MaterialTheme.colors.error,
                elevation = 0.dp
            ) {
                Text(
                    text = if (formIsValid) "Form is valid" else "Form is not valid",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            if (formError != null)
                Text(
                    text = formError!!,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.CenterHorizontally)
                )
        }
    }

    @Composable
    fun FormVerticalSpace() {
        Spacer(modifier = Modifier.height(16.dp))
    }
}