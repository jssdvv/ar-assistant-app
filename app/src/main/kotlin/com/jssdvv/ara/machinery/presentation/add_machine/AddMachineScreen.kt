package com.jssdvv.ara.machinery.presentation.add_machine

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jssdvv.ara.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMachineScreen(
    onNavigateBack: () -> Unit,
) {
    val viewModel = hiltViewModel<AddMachineViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_machine_topbar_text),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier,
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription =  stringResource(R.string.add_machine_cancel_button_description)
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.onClickSavingChanges()
                            onNavigateBack()
                        },
                        modifier = Modifier,
                        enabled = uiState.isButtonEnabled
                    ) {
                        Text(stringResource(R.string.add_machine_save_button_text))
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            item {
                OutlinedTextField(
                    value = uiState.machine.name,
                    onValueChange = { viewModel.onUiEvent(AddMachineUiEvent.OnNameChange(it)) },
                    maxLines = 1,
                    label = { Text(stringResource(R.string.add_machine_name_text_field)) },

                    )
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                OutlinedTextField(
                    value = uiState.machine.category,
                    onValueChange = { viewModel.onUiEvent(AddMachineUiEvent.OnCategoryChange(it)) },
                    maxLines = 1,
                    label = { Text(stringResource(R.string.add_machine_category_text_field)) }
                )
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                OutlinedTextField(
                    value = uiState.machine.description,
                    onValueChange = { viewModel.onUiEvent(AddMachineUiEvent.OnDescriptionChange(it)) },
                    maxLines = 1,
                    label = { Text(stringResource(R.string.add_machine_description_text_field)) }
                )
            }
        }
    }
}