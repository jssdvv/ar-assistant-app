package com.jssdvv.ara.machines.domain.usecase

import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.domain.repository.MarkerRepository
import kotlinx.coroutines.flow.Flow

data class MarkersDataManager(
    val select: SelectMarkers,
    val upsert: UpsertMarkers,
    val delete: DeleteMarkers,
)

class CountMarkers(private val repository: MarkerRepository) {
    operator fun invoke(machineId: Int): Flow<Int> = repository.countMarkersByMachineId(machineId)

    fun countCalibratedMarkersByMachineId(machineId: Int): Flow<Int> =
        repository.countCalibratedMarkersByMachineId(machineId)
}

class SelectMarker(private val repository: MarkerRepository) {
    operator fun invoke(markerId: Int): Marker? = repository.selectMarkerById(markerId)
}

class SelectMarkers(private val repository: MarkerRepository) {
    operator fun invoke(machineId: Int): Flow<List<Marker>> =
        repository.selectMarkersByMachineId(machineId)

    fun selectSingleMarkerById(markerId: Int): Marker? =
        repository.selectMarkerById(markerId)
}

class UpsertMarkers(private val repository: MarkerRepository) {
    suspend operator fun invoke(vararg model: Marker) = repository.upsertMarker(*model)
}

class DeleteMarkers(private val repository: MarkerRepository) {
    suspend operator fun invoke(vararg model: Marker) = repository.deleteMarker(*model)
}