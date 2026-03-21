package com.jssdvv.ara.machines.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.jssdvv.ara.machines.data.local.entity.MarkerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkerDao {
    @Query(
        """
        SELECT COUNT(*) FROM ${MarkerEntity.TABLE_NAME}
        WHERE ${MarkerEntity.COLUMN_MACHINE_ID} = :machineId
        """
    )
    fun countEntitiesByMachineId(machineId: Int): Flow<Int>

    @Query(
        """
        SELECT COUNT(*) FROM ${MarkerEntity.TABLE_NAME}
        WHERE ${MarkerEntity.COLUMN_MACHINE_ID} = :machineId AND ${MarkerEntity.COLUMN_CALIBRATED} = 1
        """
    )
    fun countCalibratedMarkersByMachineId(machineId: Int): Flow<Int>

    @Query(
        """
        SELECT * FROM ${MarkerEntity.TABLE_NAME}
        WHERE ${MarkerEntity.COLUMN_ID} = :id 
        """
    )
    fun selectEntityById(id: Int): MarkerEntity?

    @Query(
        """
        SELECT * FROM ${MarkerEntity.TABLE_NAME}
        WHERE ${MarkerEntity.COLUMN_MACHINE_ID} = :machineId
        ORDER BY `${MarkerEntity.COLUMN_INDEX}` ASC
        """
    )
    fun selectMarkersByMachineId(machineId: Int): Flow<List<MarkerEntity>>

    @Upsert
    suspend fun upsertMarker(vararg entity: MarkerEntity)

    @Delete
    suspend fun deleteMarker(vararg entity: MarkerEntity)
}