package com.jssdvv.ara.machines.data.repository

import com.jssdvv.ara.machines.data.local.dao.MotorDao
import com.jssdvv.ara.machines.data.local.mapper.machine.toEntity
import com.jssdvv.ara.machines.domain.model.machine.MotorIdentity
import com.jssdvv.ara.machines.domain.model.machine.MotorSpecs
import com.jssdvv.ara.machines.domain.repository.MotorRepository

class MotorRepositoryImpl(
    val dao: MotorDao,
) : MotorRepository {

    override suspend fun updateModel(vararg model: MotorIdentity) =
        dao.updateEntity(*model.map(MotorIdentity::toEntity).toTypedArray())

    override suspend fun updateModelSpecs(vararg model: MotorSpecs) =
        dao.updateEntitySpecs(*model.map(MotorSpecs::toEntity).toTypedArray())
}