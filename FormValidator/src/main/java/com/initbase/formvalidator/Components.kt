package com.initbase.formvalidator

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import kotlin.time.ExperimentalTime

@Composable
fun Form(
    modifier: Modifier = Modifier,
    validator: FormValidator = FormValidator(),
    content: @Composable ColumnScope.() -> Unit = {}
) {
    CompositionLocalProvider(LocalFormValidator provides validator) {
        Column(content = content, modifier = modifier)
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
    validator.onValidate = {
        if (!it) {
            scope.launch {
                showError = true
                delay(snackBarProperties.visibleDuration)
                showError = false
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
    var message: String? = null,
    val title: String = "Validation Error",
    val modifier: Modifier = Modifier.fillMaxWidth(),
    val margin: PaddingValues = PaddingValues(16.dp),
    val titleStyle: TextStyle? = null,
    val messageStyle: TextStyle? = null,
    val backgroundColor: Color? = null,
    val shape: Shape = RoundedCornerShape(8.dp),
    val visibleDuration: Duration = Duration.milliseconds(3000),
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