package com.jssdvv.ara.machinery.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jssdvv.ara.machinery.domain.model.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activityTable WHERE machineId = :machineId")
    fun getAllActivitiesForMachineIdAsc(machineId: Int): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertActivity(entity: ActivityEntity)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateActivity(entity: ActivityEntity)

    @Delete
    suspend fun deleteActivity(entity: ActivityEntity)
}
