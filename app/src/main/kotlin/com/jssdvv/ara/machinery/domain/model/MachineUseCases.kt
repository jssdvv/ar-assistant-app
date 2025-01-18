package com.jssdvv.ara.machinery.domain.model

import com.jssdvv.ara.machinery.domain.usecase.DeleteMachine
import com.jssdvv.ara.machinery.domain.usecase.GetMachines
import com.jssdvv.ara.machinery.domain.usecase.InsertMachine
import com.jssdvv.ara.machinery.domain.usecase.UpdateMachine

data class MachineUseCases(
    val getMachines: GetMachines,
    val insertMachine: InsertMachine,
    val updateMachine: UpdateMachine,
    val deleteMachine: DeleteMachine
)