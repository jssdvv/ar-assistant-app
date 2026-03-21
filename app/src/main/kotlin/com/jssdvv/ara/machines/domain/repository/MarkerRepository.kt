package com.jssdvv.ara.machines.domain.repository

import com.jssdvv.ara.machines.domain.model.Marker
import kotlinx.coroutines.flow.Flow

interface MarkerRepository {
    fun countMarkersByMachineId(machineId: Int): Flow<Int>
    fun countCalibratedMarkersByMachineId(machineId: Int): Flow<Int>
    fun selectMarkerById(id: Int): Marker?
    fun selectMarkersByMachineId(machineId: Int): Flow<List<Marker>>
    suspend fun upsertMarker(vararg model: Marker)
    suspend fun deleteMarker(vararg model: Marker)
}