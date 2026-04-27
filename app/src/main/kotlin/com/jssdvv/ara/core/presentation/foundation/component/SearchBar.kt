package com.jssdvv.ara.core.presentation.foundation.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import com.jssdvv.ara.core.presentation.common.component.CloseIcon
import com.jssdvv.ara.core.presentation.common.component.SearchIcon
import com.jssdvv.ara.core.presentation.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = { SearchIcon() },
    trailingIcon: @Composable (() -> Unit)? = if (value.isNotBlank()) {
        {
            IconButton(
                onClick = { onValueChange("") },
                content = { CloseIcon() }
            )
        }
    } else null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    shape: Shape = CircleShape,
    contentPadding: PaddingValues = TextFieldDefaults.contentPaddingWithLabel(
        top = MaterialTheme.spacing.extraSmall,
        bottom = MaterialTheme.spacing.extraSmall
    ),
    interactionSource: MutableInteractionSource? = remember { MutableInteractionSource() }
) {
    val focusManager = LocalFocusManager.current
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

    val textColor = textStyle.color.takeOrElse {
        val focused = resolvedInteractionSource.collectIsFocusedAsState().value
        TextFieldDefaults.colors().textColor(enabled = true, isError = false, focused = focused)
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    val colors = colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
    )

    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .padding(MaterialTheme.spacing.extraSmall)
                .height(SearchBarDefaults.InputFieldHeight)
                .defaultMinSize(
                    minWidth = TextFieldDefaults.MinWidth,
                    minHeight = TextFieldDefaults.MinHeight,
                ),
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(colors.cursorColor(isError = false)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            singleLine = true,
            interactionSource = resolvedInteractionSource,
            decorationBox = { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = value,
                    visualTransformation = VisualTransformation.None,
                    innerTextField = innerTextField,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    shape = shape,
                    singleLine = true,
                    enabled = true,
                    isError = false,
                    interactionSource = resolvedInteractionSource,
                    colors = colors,
                    contentPadding = contentPadding
                )
            }
        )
    }
}