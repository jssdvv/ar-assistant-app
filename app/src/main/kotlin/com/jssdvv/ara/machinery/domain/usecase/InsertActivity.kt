package com.jssdvv.ara.machinery.domain.usecase

import com.jssdvv.ara.machinery.domain.model.ActivityEntity
import com.jssdvv.ara.machinery.domain.model.InvalidActivityException
import com.jssdvv.ara.machinery.domain.model.InvalidMachineException
import com.jssdvv.ara.machinery.domain.repository.ActivityRepository

class InsertActivity(
    private val repository: ActivityRepository
) {
    @Throws(InvalidActivityException::class)
    suspend operator fun invoke(entity: ActivityEntity) {
        if (entity.name.isBlank()) {
            throw InvalidMachineException("La máquina debe tener un nombre")
        }
        if (entity.description.isBlank()) {
            throw InvalidMachineException("La máquina debe tener un nombre")
        }
        repository.insertActivity(entity)
    }
}