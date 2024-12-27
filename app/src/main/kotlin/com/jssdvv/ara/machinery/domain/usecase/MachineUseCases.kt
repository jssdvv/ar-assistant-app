package com.jssdvv.ara.machinery.domain.usecase

data class MachineUseCases(
    val getMachines: GetMachines,
    val insertMachine: InsertMachine,
    val updateMachine: UpdateMachine,
    val deleteMachine: DeleteMachine
)