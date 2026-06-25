package com.jssdvv.ara.machines.presentation.destination.steps.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.ChangeImageButton
import com.jssdvv.ara.core.presentation.common.component.CheckIcon
import com.jssdvv.ara.core.presentation.common.component.CloseIcon
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.foundation.component.MinimalDialog
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Activity
import com.jssdvv.ara.machines.domain.type.ActivityType

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditActivityDialog(
    activity: Activity,
    onConfirm: (name: String, type: ActivityType, description: String?, frequency: Int?, frequencyUnit: String?, imageUri: Uri?) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(activity.name) }
    var type by remember { mutableStateOf(activity.type) }
    var description by remember { mutableStateOf(activity.description.orEmpty()) }
    var frequency by remember { mutableStateOf(activity.frequency?.toString().orEmpty()) }
    var frequencyUnit by remember { mutableStateOf(activity.frequencyUnit.orEmpty()) }
    var imageUri by remember { mutableStateOf<Uri?>(activity.imageUri) }
    var showTypeDropDown by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val noImageBgColor = MaterialTheme.colorScheme.surface

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { contentUri ->
            if (contentUri != null) imageUri = contentUri
        }
    )

    MinimalDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Text(
                text = stringResource(R.string.dialog_activity_edit_title),
                style = MaterialTheme.typography.titleLarge
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9F)
                    .background(noImageBgColor, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                imageUri?.let {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9F),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it)
                            .build(),
                        error = ColorPainter(noImageBgColor),
                        fallback = ColorPainter(noImageBgColor),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null
                    )
                }

                ChangeImageButton(
                    onClick = {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = MaterialTheme.spacing.small),
                    isOutlined = imageUri == null
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.text_field_activity_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = showTypeDropDown,
                onExpandedChange = { showTypeDropDown = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
                    value = stringResource(type.labelResId),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.text_field_type_label)) },
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = showTypeDropDown,
                    onDismissRequest = { showTypeDropDown = false }
                ) {
                    ActivityType.entries.forEach { activityType ->
                        DropdownMenuItem(
                            text = { Text(stringResource(activityType.labelResId)) },
                            onClick = {
                                type = activityType
                                showTypeDropDown = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.text_field_activity_description_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = frequency,
                onValueChange = {
                    if (it.matches(Regex("^\\d*$"))) frequency = it
                },
                label = { Text(stringResource(R.string.text_field_activity_frequency_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = frequencyUnit,
                onValueChange = { frequencyUnit = it },
                label = { Text(stringResource(R.string.text_field_activity_frequency_unit_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

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
                            name,
                            type,
                            description.ifBlank { null },
                            frequency.toIntOrNull(),
                            frequencyUnit.ifBlank { null },
                            imageUri
                        )
                    },
                    colors = ButtonDefaults.buttonColors(),
                    enabled = name.isNotBlank(),
                    icon = { CheckIcon() },
                    content = { Text(stringResource(R.string.button_save_action)) }
                )
            }

            TextButton(
                onClick = { showDeleteDialog = true },
            ) {
                Text(
                    text = stringResource(R.string.button_delete_action),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = stringResource(R.string.button_delete_action))
            },
            text = {
                Text(text = stringResource(R.string.dialog_delete_activity_warning))
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text(text = stringResource(R.string.button_delete_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.button_cancel_action))
                }
            }
        )
    }
}
