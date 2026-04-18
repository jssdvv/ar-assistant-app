package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.AddIcon
import com.jssdvv.ara.core.presentation.common.component.ChangeImageButton
import com.jssdvv.ara.core.presentation.common.component.CloseIcon
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.foundation.component.MinimalDialog
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Step

@Composable
fun StepDialog(
    currentStep: Step,
    onUpdateStep: (Step) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveStep: (Step) -> Unit,
) {
    val isNewStep = currentStep.id == 0
    val noImageBgColor = MaterialTheme.colorScheme.surface
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { contentUri ->
            if (contentUri != null) onUpdateStep(currentStep.copy(imageUri = contentUri))
        }
    )

    MinimalDialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(if (isNewStep) R.string.step_dialog_create_title else R.string.step_dialog_edit_title),
                style = MaterialTheme.typography.titleLarge
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9F)
                    .background(noImageBgColor, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                currentStep.imageUri?.let {
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
                        contentDescription = null // todo create string
                    )
                }

                ChangeImageButton(
                    onClick = {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = MaterialTheme.spacing.small),
                    isOutlined = currentStep.imageUri == null
                )
            }

            OutlinedTextField(
                value = currentStep.name,
                onValueChange = { onUpdateStep(currentStep.copy(name = it)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(stringResource(R.string.text_field_step_dialog_name_label)) }
            )

            OutlinedTextField(
                value = currentStep.description ?: "",
                onValueChange = { onUpdateStep(currentStep.copy(description = it)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                label = { Text(stringResource(R.string.text_field_step_dialog_description_label)) }
            )

            FlowRow(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ButtonWithIcon(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    icon = { CloseIcon() },
                    content = { Text(stringResource(R.string.button_step_dialog_cancel_action)) }
                )

                ButtonWithIcon(
                    onClick = {
                        onSaveStep(currentStep)
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.buttonColors(),
                    icon = { AddIcon() },
                    content = {
                        Text(
                            stringResource(
                                if (isNewStep) R.string.button_step_dialog_create_action
                                else R.string.button_step_dialog_save_action
                            )
                        )
                    }
                )
            }
        }
    }
}