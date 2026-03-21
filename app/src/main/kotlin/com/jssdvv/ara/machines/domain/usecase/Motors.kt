package com.jssdvv.ara.machines.domain.usecase

import com.jssdvv.ara.machines.domain.model.machine.MotorIdentity
import com.jssdvv.ara.machines.domain.model.machine.MotorSpecs
import com.jssdvv.ara.machines.domain.repository.MotorRepository

data class MotorsDataManager(
    val select: SelectMotors,
)

class SelectMotors(private val repository: MotorRepository) {
    suspend operator fun invoke(vararg model: MotorIdentity) = repository.updateModel(*model)
}

class UpdateMotorSpecs(private val repository: MotorRepository) {
    suspend operator fun invoke(vararg model: MotorSpecs) = repository.updateModelSpecs(*model)
}

class SelectMotorAndSpecs(private val repository: MotorRepository) {
//    suspend operator fun invoke(motorId: Int) = repository.getMotorAndMotorSpecs(motorId)
}