package com.jssdvv.ara.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jssdvv.ara.core.data.local.entity.ActivityEntity
import com.jssdvv.ara.core.data.local.entity.ModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    @Query("SELECT * FROM modelTable WHERE machineId = :machineId")
    fun getAllModelsForMachineIdAsc(machineId: Int): Flow<List<ModelEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertModel(entity: ModelEntity)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateModel(entity: ModelEntity)

    @Delete
    suspend fun deleteModel(entity: ModelEntity)
}
