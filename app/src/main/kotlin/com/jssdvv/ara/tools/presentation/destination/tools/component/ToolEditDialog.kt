package com.jssdvv.ara.tools.presentation.destination.tools.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.ChangeImageButton
import com.jssdvv.ara.core.presentation.common.component.CheckIcon
import com.jssdvv.ara.core.presentation.common.component.CloseIcon
import com.jssdvv.ara.core.presentation.foundation.component.ColumnDialog
import com.jssdvv.ara.core.presentation.theme.spacing
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
    var bodyMediaUri by remember { mutableStateOf(tool.bodyMediaUri) }
    var symbolMediaUri by remember { mutableStateOf(tool.symbolMediaUri) }
    val noImageBgColor = MaterialTheme.colorScheme.surfaceVariant

    val bodyLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> if (uri != null) bodyMediaUri = uri }

    val symbolLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> if (uri != null) symbolMediaUri = uri }

    ColumnDialog(
        title = { Text(stringResource(R.string.dialog_tool_edit_title)) },
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            // Body Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9F)
                    .background(noImageBgColor, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                bodyMediaUri?.let {
                    AsyncImage(
                        modifier = Modifier.fillMaxWidth(),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it)
                            .build(),
                        error = ColorPainter(noImageBgColor),
                        fallback = ColorPainter(noImageBgColor),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = stringResource(R.string.image_tool_body_content_desc)
                    )
                }

                ChangeImageButton(
                    onClick = { bodyLauncher.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = MaterialTheme.spacing.small),
                    isOutlined = bodyMediaUri == null
                )
            }

            // Symbol Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9F)
                    .background(noImageBgColor, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                symbolMediaUri?.let {
                    AsyncImage(
                        modifier = Modifier.fillMaxWidth(),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it)
                            .build(),
                        error = ColorPainter(noImageBgColor),
                        fallback = ColorPainter(noImageBgColor),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = stringResource(R.string.image_tool_symbol_content_desc)
                    )
                }

                ChangeImageButton(
                    onClick = { symbolLauncher.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = MaterialTheme.spacing.small),
                    isOutlined = symbolMediaUri == null
                )
            }

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.text_field_tool_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Code
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text(stringResource(R.string.text_field_tool_code_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Type
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

            // Action buttons
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                modifier = Modifier.fillMaxWidth()
            ) {
                com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    icon = { CloseIcon() },
                    content = { Text(stringResource(R.string.button_cancel_action)) }
                )

                com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon(
                    onClick = {
                        onSave(
                            tool.copy(
                                name = name,
                                code = code.ifBlank { null },
                                type = selectedType,
                                bodyMediaUri = bodyMediaUri,
                                symbolMediaUri = symbolMediaUri,
                            )
                        )
                    },
                    enabled = name.isNotBlank(),
                    icon = { CheckIcon() },
                    content = { Text(stringResource(R.string.button_save_action)) }
                )
            }
        }
    }
}
