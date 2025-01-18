package com.jssdvv.ara.core.data.repository

import com.jssdvv.ara.core.data.local.dao.MachineDao
import com.jssdvv.ara.core.data.mapper.toDomain
import com.jssdvv.ara.core.data.mapper.toEntity
import com.jssdvv.ara.core.domain.model.Machine
import com.jssdvv.ara.core.domain.repository.MachineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MachineRepositoryImpl(
    private val dao: MachineDao,
) : MachineRepository {
    override fun getAllMachinesByNameAsc(): Flow<List<Machine>> {
        return dao.getAllMachinesByNameAsc().map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }

    override fun getAllMachinesByNameDesc(): Flow<List<Machine>> {
        return dao.getAllMachinesByNameDesc().map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }

    override fun getAllMachinesByCategoryAsc(): Flow<List<Machine>> {
        return dao.getAllMachinesByCategoryAsc().map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }

    override fun getAllMachinesByCategoryDesc(): Flow<List<Machine>> {
        return dao.getAllMachinesByCategoryDesc().map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }

    override fun getAllMachinesByTimestampAsc(): Flow<List<Machine>> {
        return dao.getAllMachinesByTimestampAsc().map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }

    override fun getAllMachinesByTimestampDesc(): Flow<List<Machine>> {
        return dao.getAllMachinesByTimestampDesc().map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun insertMachine(model: Machine) {
        return dao.insertMachine(model.toEntity())
    }

    override suspend fun updateMachine(model: Machine) {
        return dao.updateMachine(model.toEntity())
    }

    override suspend fun deleteMachine(model: Machine) {
        return dao.deleteMachine(model.toEntity())
    }
}