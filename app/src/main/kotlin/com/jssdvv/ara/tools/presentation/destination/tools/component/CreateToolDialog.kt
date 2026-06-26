package com.jssdvv.ara.tools.presentation.destination.tools.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.jssdvv.ara.core.presentation.common.component.AddIcon
import com.jssdvv.ara.core.presentation.common.component.ChangeImageButton
import com.jssdvv.ara.core.presentation.common.component.CloseIcon
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.foundation.component.MinimalDialog
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Tool
import com.jssdvv.ara.machines.domain.type.ToolType

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateToolDialog(
    onConfirm: (Tool) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ToolType.OTHER) }
    var bodyMediaUri by remember { mutableStateOf<Uri?>(null) }
    var symbolMediaUri by remember { mutableStateOf<Uri?>(null) }
    var showTypeDropDown by remember { mutableStateOf(false) }
    val noImageBgColor = MaterialTheme.colorScheme.surface

    val bodyLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> if (uri != null) bodyMediaUri = uri }

    val symbolLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> if (uri != null) symbolMediaUri = uri }

    MinimalDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Text(
                text = stringResource(R.string.dialog_tool_create_title),
                style = MaterialTheme.typography.titleLarge
            )

            // Body Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(noImageBgColor, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                bodyMediaUri?.let {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
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
                    .aspectRatio(1f)
                    .background(noImageBgColor, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                symbolMediaUri?.let {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
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
                expanded = showTypeDropDown,
                onExpandedChange = { showTypeDropDown = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = stringResource(type.labelResId),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.text_field_tool_type_label)) },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropDown) }
                )
                ExposedDropdownMenu(
                    expanded = showTypeDropDown,
                    onDismissRequest = { showTypeDropDown = false }
                ) {
                    ToolType.entries.forEach { toolType ->
                        DropdownMenuItem(
                            text = { Text(stringResource(toolType.labelResId)) },
                            onClick = {
                                type = toolType
                                showTypeDropDown = false
                            }
                        )
                    }
                }
            }

            // Buttons
            FlowRow(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                ButtonWithIcon(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    icon = { CloseIcon() },
                    content = { Text(stringResource(R.string.button_cancel_action)) }
                )

                ButtonWithIcon(
                    onClick = {
                        onConfirm(
                            Tool(
                                type = type,
                                name = name,
                                code = code.ifBlank { null },
                                bodyMediaUri = bodyMediaUri,
                                symbolMediaUri = symbolMediaUri,
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(),
                    enabled = name.isNotBlank(),
                    icon = { AddIcon() },
                    content = { Text(stringResource(R.string.button_save_action)) }
                )
            }
        }
    }
}
