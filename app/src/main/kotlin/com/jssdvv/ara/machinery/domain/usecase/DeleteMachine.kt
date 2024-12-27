package com.jssdvv.ara.machinery.domain.usecase

import com.jssdvv.ara.machinery.domain.model.MachineEntity
import com.jssdvv.ara.machinery.domain.repository.MachineRepository

class DeleteMachine(
    private val repository: MachineRepository
) {
    suspend operator fun invoke(entity: MachineEntity) {
        repository.deleteMachine(entity)
    }
}