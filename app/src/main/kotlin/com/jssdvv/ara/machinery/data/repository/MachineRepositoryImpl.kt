package com.jssdvv.ara.machinery.data.repository

import com.jssdvv.ara.machinery.data.local.MachineDao
import com.jssdvv.ara.machinery.domain.model.MachineEntity
import com.jssdvv.ara.machinery.domain.repository.MachineRepository
import kotlinx.coroutines.flow.Flow

class MachineRepositoryImpl(
    private val dao: MachineDao
) : MachineRepository {
    override fun getAllMachinesByNameAsc(): Flow<List<MachineEntity>> {
        return dao.getAllMachinesByNameAsc()
    }

    override fun getAllMachinesByNameDesc(): Flow<List<MachineEntity>> {
        return dao.getAllMachinesByNameDesc()
    }

    override fun getAllMachinesByCategoryAsc(): Flow<List<MachineEntity>> {
        return dao.getAllMachinesByCategoryAsc()
    }

    override fun getAllMachinesByCategoryDesc(): Flow<List<MachineEntity>> {
        return dao.getAllMachinesByCategoryDesc()
    }

    override fun getAllMachinesByTimestampAsc(): Flow<List<MachineEntity>> {
        return dao.getAllMachinesByTimestampAsc()
    }

    override fun getAllMachinesByTimestampDesc(): Flow<List<MachineEntity>> {
        return dao.getAllMachinesByTimestampDesc()
    }

    override suspend fun insertMachine(entity: MachineEntity) {
        return dao.insertMachine(entity)
    }

    override suspend fun updateMachine(entity: MachineEntity) {
        return dao.updateMachine(entity)
    }

    override suspend fun deleteMachine(entity: MachineEntity) {
        return dao.deleteMachine(entity)
    }
}