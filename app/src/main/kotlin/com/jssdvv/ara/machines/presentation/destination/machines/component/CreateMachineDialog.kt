package com.jssdvv.ara.machines.presentation.destination.machines.component

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
import androidx.compose.material3.ButtonDefaults
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
import com.jssdvv.ara.machines.domain.model.machine.Machine

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateMachineDialog(
    onConfirm: (Machine) -> Unit,
    onDismiss: () -> Unit,
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
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
                text = stringResource(R.string.dialog_machine_create_title),
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
                value = code,
                onValueChange = { code = it },
                label = { Text(stringResource(R.string.text_field_code_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.text_field_machine_name_label)) },
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
                            Machine(
                                code = code,
                                name = name,
                                imageUri = imageUri
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
