package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.CloseIcon
import com.jssdvv.ara.core.presentation.foundation.component.SideSheet
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.Step

@Composable
fun StepsSideSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    steps: List<Step>,
    selectedStep: Step?,
    onNewStep: () -> Unit,
    onSaveStep: (Step) -> Unit,
    onSelectStep: (Int?) -> Unit,
    onUpdateStep: (Step) -> Unit,
    operations: List<Operation>,
    selectedOperation: Operation?,
    onNewOperation: (stepId: Int) -> Unit,
    onEditOperation: (Operation) -> Unit,
    onSelectOperation: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) = SideSheet(
    isVisible = isVisible,
    onDismiss = onDismiss,
    modifier = modifier,
) {
    var showStepDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.steps_side_sheet_title),
                modifier = Modifier.weight(1F),
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(onDismiss) { CloseIcon() }
        }

        LazyColumn(
            modifier = Modifier.weight(1F, false),
            contentPadding = PaddingValues(vertical = MaterialTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            items(
                items = steps,
                key = { it.id }
            ) { step ->
                val stepOperations =
                    remember(operations) { operations.filter { it.stepId == step.id } }
                StepCard(
                    onClick = { onSelectStep(step.id) },
                    step = step,
                    isSelected = selectedStep?.id == step.id,
                    onEditStep = {
                        onSelectStep(it.id)
                        showStepDialog = true
                    },
                    operations = stepOperations,
                    selectedOperation = selectedOperation,
                    onSelectOperation = onSelectOperation,
                    onAddOperation = {
                        onSelectStep(step.id)
                        onNewOperation(step.id)
                    },
                    onEditOperation = onEditOperation
                )
            }
        }

        AddStepButton {
            onNewStep()
            showStepDialog = true
        }

        if (showStepDialog && selectedStep != null) {
            StepDialog(
                currentStep = selectedStep,
                onUpdateStep = onUpdateStep,
                onDismissRequest = {
                    showStepDialog = false
                    onSelectStep(null)
                    onSelectOperation(null)
                },
                onSaveStep = { onSaveStep(it)
                    onSelectStep(null)
                }
            )
        }
    }
}