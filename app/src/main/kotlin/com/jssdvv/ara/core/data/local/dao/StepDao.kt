package com.jssdvv.ara.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jssdvv.ara.core.data.local.entity.ActivityEntity
import com.jssdvv.ara.core.data.local.entity.StepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {
    @Query("SELECT * FROM stepTable WHERE activityId = :activityId ORDER BY orderNumber ASC")
    fun getAllStepsForActivityIdAsc(activityId: Int): Flow<List<StepEntity>>

    @Query("SELECT * FROM stepTable WHERE activityId = :activityId ORDER BY orderNumber DESC")
    fun getAllStepsForActivityIdDesc(activityId: Int): Flow<List<StepEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStep(entity: StepEntity)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateStep(entity: StepEntity)

    @Delete
    suspend fun deleteStep(entity: StepEntity)
}
