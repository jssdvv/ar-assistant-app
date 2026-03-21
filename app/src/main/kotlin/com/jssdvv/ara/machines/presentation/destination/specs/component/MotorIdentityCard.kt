package com.jssdvv.ara.machines.presentation.destination.specs.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jssdvv.ara.machines.domain.model.machine.MotorIdentity
import com.jssdvv.ara.machines.presentation.destination.specs.MachineDetailsCard

@Composable
fun MotorIdentityCard(
    modifier: Modifier = Modifier,
    motorIdentity: MotorIdentity?,
    editingCard: MachineDetailsCard,
    onClickEditCard: (MachineDetailsCard) -> Unit,
    onClickSaveCard: (MotorIdentity) -> Unit,
) {

}