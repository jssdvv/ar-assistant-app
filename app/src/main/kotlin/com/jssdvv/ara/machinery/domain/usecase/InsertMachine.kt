package com.jssdvv.ara.machinery.domain.usecase

import com.jssdvv.ara.machinery.domain.model.InvalidMachineException
import com.jssdvv.ara.machinery.domain.model.MachineEntity
import com.jssdvv.ara.machinery.domain.repository.MachineRepository

class InsertMachine(
    private val repository: MachineRepository
) {
    @Throws(InvalidMachineException::class)
    suspend operator fun invoke(entity: MachineEntity) {
        if (entity.name.isBlank()) {
            throw InvalidMachineException("La máquina debe tener un nombre")
        }
        if (entity.category.isBlank()) {
            throw InvalidMachineException("La máquina debe tener una categoría")
        }
        repository.insertMachine(entity)
    }
}