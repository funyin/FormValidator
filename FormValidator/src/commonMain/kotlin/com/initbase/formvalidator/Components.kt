package com.initbase.formvalidator

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

/**
 * Wraps a form field value together with its validation error and change-tracking state.
 *
 * @property value The current field value.
 * @property initial The value at the time of construction, used to compute [modified].
 * @property error The current validation error message, or null when the field is valid.
 * @property modified True when [value] differs from [initial].
 */
data class ErrorSafeValue<T>(
    val value: T,
    val initial: T = value,
    val error: String? = null,
) {
    val modified: Boolean
        get() = initial != value
}

/**
 * Creates a [MutableState] holding an [ErrorSafeValue] for [value].
 *
 * Intended to be used with [remember] inside a composable:
 * ```kotlin
 * var firstName by remember { errorSafe(member?.firstName ?: "") }
 * ```
 * Wire the field into a [FormValidator.ValidationField] and update via [ErrorSafeValue.copy]:
 * ```kotlin
 * ValidationField(value = firstName.value, name = "First Name", type = FormValidator.Type.Required) {
 *     firstName = firstName.copy(error = it)
 * }
 * ```
 */
fun <T> errorSafe(value: T): MutableState<ErrorSafeValue<T>> = mutableStateOf(ErrorSafeValue(value = value))

@Composable
fun Form(
    modifier: Modifier = Modifier,
    validator: FormValidator = FormValidator(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    CompositionLocalProvider(LocalFormValidator provides validator) {
        Column(
            content = content,
            modifier = modifier,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        )
    }
}

/**
 * Overload of [Form] that shows a snack bar with validation error.
 * Suggestion -> When this overload is used, do not show the error for each field in the form with
 * [FormValidator.ValidationField.onError] since the purpose of this form is to show one error at a time.
 *
 *
 *@param modifier Modifier for the Form i.e **Column**
 *@param snackBarProperties The properties([SnackBarProperties]) for the snackbar that is shown when the form fails validation
 *@param lifecycleOwner Used to observe validation and dispose observer
 *@param validator The [FormValidator] that controls the form.
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalTime::class)
@Composable
fun Form(
    modifier: Modifier = Modifier,
    validator: FormValidator = FormValidator(),
    snackBarProperties: SnackBarProperties = SnackBarProperties(),
    content: @Composable ColumnScope.() -> Unit = {}
) {
    var showError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val currentSnackBarProperties by rememberUpdatedState(snackBarProperties)
    SideEffect {
        validator.onValidate = { valid ->
            if (!valid) {
                scope.launch {
                    showError = true
                    delay(currentSnackBarProperties.visibleDuration)
                    showError = false
                }
            }
        }
    }
    CompositionLocalProvider(LocalFormValidator provides validator) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Column(content = content, modifier = modifier)
            AnimatedVisibility(
                visible = showError,
                enter = snackBarProperties.enterTransition,
                exit = snackBarProperties.exitTransition
            ) {
                ValidationSnackBar(snackBarProperties.copy(message = validator.errorMessage))
            }
        }
    }
}

/**
 * SnackBar properties for [Form]
 */
data class SnackBarProperties @OptIn(
    ExperimentalTime::class,
    ExperimentalAnimationApi::class
) constructor(
    val message: String? = null,
    val title: String = "Validation Error",
    val modifier: Modifier = Modifier.fillMaxWidth(),
    val margin: PaddingValues = PaddingValues(16.dp),
    val titleStyle: TextStyle? = null,
    val messageStyle: TextStyle? = null,
    val backgroundColor: Color? = null,
    val shape: Shape = RoundedCornerShape(8.dp),
    val visibleDuration: Duration = 3000.milliseconds,
    val enterTransition: EnterTransition = fadeIn() + expandIn(),
    val exitTransition: ExitTransition = shrinkOut() + fadeOut()
)

@Composable
private fun ValidationSnackBar(properties: SnackBarProperties) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(properties.margin)) {
        Surface(
            color = properties.backgroundColor ?: MaterialTheme.colors.surface,
            shape = properties.shape,
            modifier = properties.modifier,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = properties.title,
                    modifier = Modifier.padding(bottom = 5.dp),
                    style = properties.titleStyle ?: TextStyle(
                        color = MaterialTheme.colors.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.W600,
                    )
                )
                val message = properties.message
                if (message != null)
                    Text(
                        text = message,
                        style = properties.messageStyle
                            ?: TextStyle(color = MaterialTheme.colors.onSurface)
                    )
            }
        }
    }
}