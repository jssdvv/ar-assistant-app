package com.jssdvv.ara.machines.presentation.destination.calibration.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.CancelButtonWithIcon
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.presentation.component.RenderableIcon

@Composable
fun ModelsDialog(
    models: List<Model>,
    onSelectModel: (modelId: Int) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = { CancelButtonWithIcon(onDismissRequest) },
        modifier = modifier,
        title = {
            Text(stringResource(R.string.calibration_dialog_models_title))
        },
        text = {
            LazyColumn {
                itemsIndexed(models) { index, model ->
                    if (index > 0) HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .padding(MaterialTheme.spacing.medium)
                            .clickable { onSelectModel(model.id) },
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        RenderableIcon()
                        Text(model.name)
                    }
                }
            }
        }
    )
}