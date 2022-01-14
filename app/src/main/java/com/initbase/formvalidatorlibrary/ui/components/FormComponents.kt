package com.initbase.formvalidatorlibrary.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChanged: (String) -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: String = "Label",
    placeholder: String? = null,
    errorMessage: String? = null,
    keyboardOptions:KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = MaterialTheme.shapes.small)
            .animateContentSize()
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.W600,
        )
        TextField(
            enabled = enabled,
            readOnly = readOnly,
            value = value,
            onValueChange = onValueChanged,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            textStyle = textStyle,
            visualTransformation = VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions.Default,
            singleLine = true,
            maxLines = Int.MAX_VALUE,
            interactionSource = remember { MutableInteractionSource() },
            placeholder = {
                if (placeholder != null)
                    Text(text = placeholder)
            }
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

@Composable
fun AppCheckBox(
    modifier: Modifier = Modifier,
    value: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Checkbox(
        checked = value,
        onCheckedChange = onCheckedChange,
        modifier = modifier.size(30.dp),
        enabled = enabled
    )
}

@Preview
@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    text: String = "Text",
    background:Color=MaterialTheme.colors.primary,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(46.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = background
        )
    ) {
        Text(text)
    }
}