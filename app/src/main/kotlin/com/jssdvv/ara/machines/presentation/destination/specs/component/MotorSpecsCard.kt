package com.jssdvv.ara.machines.presentation.destination.specs.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jssdvv.ara.machines.domain.model.machine.MotorSpecs
import com.jssdvv.ara.machines.presentation.destination.specs.MachineDetailsCard

@Composable
fun MotorSpecsCard(
    modifier: Modifier = Modifier,
    motorSpecs: MotorSpecs?,
    editingCard: MachineDetailsCard,
    onClickEditCard: (MachineDetailsCard) -> Unit,
    onClickSaveCard: (MotorSpecs) -> Unit,
) {

}