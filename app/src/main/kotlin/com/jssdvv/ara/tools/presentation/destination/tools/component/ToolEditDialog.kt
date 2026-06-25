package com.jssdvv.ara.tools.presentation.destination.tools.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R
import com.jssdvv.ara.machines.domain.model.Tool
import com.jssdvv.ara.machines.domain.type.ToolType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolEditDialog(
    tool: Tool,
    onSave: (Tool) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(tool.name) }
    var code by remember { mutableStateOf(tool.code ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(tool.type) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.dialog_tool_edit_title))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.text_field_tool_name_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text(stringResource(R.string.text_field_tool_code_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = stringResource(selectedType.labelResId),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.text_field_tool_type_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ToolType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(stringResource(type.labelResId)) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        tool.copy(
                            name = name,
                            code = code.ifBlank { null },
                            type = selectedType
                        )
                    )
                },
                enabled = name.isNotBlank()
            ) {
                Text(text = stringResource(R.string.button_save_action))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.button_cancel_action))
            }
        }
    )
}
