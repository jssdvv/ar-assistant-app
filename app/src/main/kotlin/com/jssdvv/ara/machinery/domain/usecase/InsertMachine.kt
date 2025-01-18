package com.jssdvv.ara.machinery.domain.usecase

import com.jssdvv.ara.core.data.local.entity.InvalidMachineException
import com.jssdvv.ara.core.data.local.entity.MachineEntity
import com.jssdvv.ara.core.domain.model.Machine
import com.jssdvv.ara.core.domain.repository.MachineRepository
import javax.inject.Inject
import javax.inject.Singleton

class InsertMachine @Inject constructor(
    private val repository: MachineRepository
) {
    @Throws(InvalidMachineException::class)
    suspend operator fun invoke(model: Machine) {
        if (model.name.isBlank()) {
            throw InvalidMachineException("La máquina debe tener un nombre")
        }
        if (model.category.isBlank()) {
            throw InvalidMachineException("La máquina debe tener una categoría")
        }
        repository.insertMachine(model)
    }
}