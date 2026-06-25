package com.jssdvv.ara.tools.presentation.destination.tools.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R
import com.jssdvv.ara.machines.domain.model.Tool

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: List<Tool>,
    onNavigateToToolDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) {
        SearchBar(
            modifier = modifier,
            inputField = {
                SearchBarDefaults.InputField(
                    query = if (expanded) textFieldState.text.toString() else String(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text(stringResource(R.string.search_bar_tools_supporting_text)) },
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            // TODO: Implement search results dropdown
        }
    }
}