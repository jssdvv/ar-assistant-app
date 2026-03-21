package com.jssdvv.ara.machines.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.jssdvv.ara.machines.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query(
        """
        SELECT COUNT(*) FROM ${ActivityEntity.TABLE_NAME}
        WHERE ${ActivityEntity.COLUMN_MACHINE_ID} = :machineId
        """
    )
    fun countActivitiesByMachineId(machineId: Int): Flow<Int>

    @Query(
        """
        SELECT * FROM ${ActivityEntity.TABLE_NAME}
        WHERE ${ActivityEntity.COLUMN_MACHINE_ID} = :machineId
        ORDER BY ${ActivityEntity.COLUMN_ID} ASC
        """
    )
    fun selectActivitiesByMachineId(machineId: Int): Flow<List<ActivityEntity>>

    @Upsert
    suspend fun upsertActivity(vararg entity: ActivityEntity)

    @Delete
    suspend fun deleteActivity(vararg entity: ActivityEntity)
}