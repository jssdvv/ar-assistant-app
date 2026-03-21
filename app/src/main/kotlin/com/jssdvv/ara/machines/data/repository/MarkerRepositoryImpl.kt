package com.jssdvv.ara.machines.data.repository

import com.jssdvv.ara.machines.data.local.dao.MarkerDao
import com.jssdvv.ara.machines.data.local.entity.MarkerEntity
import com.jssdvv.ara.machines.data.local.mapper.toDomain
import com.jssdvv.ara.machines.data.local.mapper.toEntity
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.domain.repository.MarkerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MarkerRepositoryImpl(
    private val dao: MarkerDao,
) : MarkerRepository {

    override fun countMarkersByMachineId(machineId: Int): Flow<Int> =
        dao.countEntitiesByMachineId(machineId)

    override fun countCalibratedMarkersByMachineId(machineId: Int): Flow<Int> =
        dao.countCalibratedMarkersByMachineId(machineId)

    override fun selectMarkerById(id: Int): Marker? =
        dao.selectEntityById(id)?.toDomain()

    override fun selectMarkersByMachineId(machineId: Int): Flow<List<Marker>> =
        dao.selectMarkersByMachineId(machineId).map { it.map(MarkerEntity::toDomain) }

    override suspend fun upsertMarker(vararg model: Marker) =
        dao.upsertMarker(*model.map(Marker::toEntity).toTypedArray())

    override suspend fun deleteMarker(vararg model: Marker) =
        dao.deleteMarker(*model.map(Marker::toEntity).toTypedArray())
}