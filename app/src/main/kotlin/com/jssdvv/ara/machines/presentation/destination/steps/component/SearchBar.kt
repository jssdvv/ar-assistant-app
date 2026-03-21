package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.common.CloseIcon
import com.jssdvv.ara.core.presentation.common.SearchIcon
import com.jssdvv.ara.core.presentation.theme.spacing

@Composable
fun SearchBar(
    state: TextFieldState = rememberTextFieldState(),
    modifier: Modifier = Modifier,
    onKeyboardAction: KeyboardActionHandler? = null
) = TextField(
    state = state,
    modifier = modifier
        .padding(MaterialTheme.spacing.small)
        .height(36.dp),
    labelPosition = TextFieldLabelPosition.Attached(),
    contentPadding = PaddingValues(MaterialTheme.spacing.extraSmall),
    textStyle = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
    colors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
    ),
    leadingIcon = { SearchIcon(Modifier.size(16.dp)) },
    shape = MaterialTheme.shapes.small,
    lineLimits = TextFieldLineLimits.SingleLine,
    placeholder = {
        Text(
            text = "Search", // todo create string
            softWrap = false,
            maxLines = 1
        )
    },
    trailingIcon = if (state.text.isNotEmpty()) {
        {
            IconButton(
                onClick = { state.clearText() },
                content = { CloseIcon(Modifier.size(16.dp)) }
            )
        }
    } else null,
    onKeyboardAction = onKeyboardAction
)
