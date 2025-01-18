package com.jssdvv.ara.machinery.domain.usecase

import com.jssdvv.ara.core.data.local.entity.InvalidActivityException
import com.jssdvv.ara.core.data.local.entity.InvalidMachineException
import com.jssdvv.ara.core.domain.model.Activity
import com.jssdvv.ara.core.domain.repository.ActivityRepository

class InsertActivity(
    private val repository: ActivityRepository
) {
    @Throws(InvalidActivityException::class)
    suspend operator fun invoke(model: Activity) {
        if (model.name.isBlank()) {
            throw InvalidMachineException("La máquina debe tener un nombre")
        }
        if (model.description.isBlank()) {
            throw InvalidMachineException("La máquina debe tener un nombre")
        }
        repository.insertActivity(model)
    }
}