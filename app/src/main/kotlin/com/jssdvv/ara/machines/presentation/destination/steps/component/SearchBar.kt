package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.CloseIcon
import com.jssdvv.ara.core.presentation.common.component.SearchIcon
import com.jssdvv.ara.core.presentation.foundation.component.SimpleSearchBar
import com.jssdvv.ara.core.presentation.theme.spacing

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    state: TextFieldState = rememberTextFieldState(),
) {
    SimpleSearchBar(
        modifier = modifier
            .padding(MaterialTheme.spacing.small)
            .height(36.dp),
        value = state.text.toString(),
        onValueChange = { state.setTextAndPlaceCursorAtEnd(it) },
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
        placeholder = {
            Text(
                text = stringResource(R.string.text_search_action),
                softWrap = false,
                maxLines = 1
            )
        },
        leadingIcon = { SearchIcon(Modifier.size(16.dp)) },
        trailingIcon = if (state.text.isNotEmpty()) {
            {
                IconButton(
                    onClick = { state.clearText() },
                    content = { CloseIcon(Modifier.size(16.dp)) }
                )
            }
        } else null,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(MaterialTheme.spacing.extraSmall)
    )
}