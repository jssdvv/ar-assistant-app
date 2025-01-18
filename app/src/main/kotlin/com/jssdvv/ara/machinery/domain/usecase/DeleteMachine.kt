package com.jssdvv.ara.machinery.domain.usecase

import com.jssdvv.ara.core.data.local.entity.MachineEntity
import com.jssdvv.ara.core.domain.model.Machine
import com.jssdvv.ara.core.domain.repository.MachineRepository

class DeleteMachine(
    private val repository: MachineRepository
) {
    suspend operator fun invoke(model: Machine) {
        repository.deleteMachine(model)
    }
}