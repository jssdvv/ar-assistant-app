package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.theme.spacing


@Composable
fun InformationChips(
    currentStepOrder: Int,
    currentOperationOrder: Int,
    stepsCount: Int,
    operationsCount: Int,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium, Alignment.End),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        maxItemsInEachRow = 2
    ) {
        ButtonWithIcon(
            onClick = { },
            icon = { StepIcon() },
            content = {
                Text(
                    text = pluralStringResource(
                        R.plurals.button_editor_step_count_label,
                        stepsCount,
                        currentStepOrder,
                        stepsCount
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        )

        ButtonWithIcon(
            onClick = { },
            colors = ButtonDefaults.filledTonalButtonColors(),
            icon = { AnimationIcon() },
            content = {
                Text(
                    text = pluralStringResource(
                        R.plurals.button_editor_operation_count_label,
                        operationsCount,
                        currentOperationOrder,
                        operationsCount
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        )
    }
}