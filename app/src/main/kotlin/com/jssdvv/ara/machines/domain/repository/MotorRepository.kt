package com.jssdvv.ara.machines.domain.repository

import com.jssdvv.ara.machines.domain.model.machine.MotorIdentity
import com.jssdvv.ara.machines.domain.model.machine.MotorSpecs

interface MotorRepository {
    suspend fun updateModel(vararg model: MotorIdentity)
    suspend fun updateModelSpecs(vararg model: MotorSpecs)
}