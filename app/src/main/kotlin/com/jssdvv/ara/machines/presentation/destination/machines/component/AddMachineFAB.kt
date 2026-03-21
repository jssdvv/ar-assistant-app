package com.jssdvv.ara.machines.presentation.destination.machines.component

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.jssdvv.ara.core.presentation.common.AddIcon

@Composable
fun AddMachineFAB(
    onNavigateToAddMachine: () -> Unit
) {
    ExtendedFloatingActionButton(
        text = { Text(text = "Nueva máquina") },
        icon = {
            AddIcon()
        },
        onClick = onNavigateToAddMachine
    )
}