package com.jssdvv.ara.core.domain.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jssdvv.ara.core.data.local.entity.ActivityEntity
import com.jssdvv.ara.core.data.local.entity.ModelEntity
import com.jssdvv.ara.core.domain.model.Model
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    fun getAllModelsForMachineIdAsc(machineId: Int): Flow<List<Model>>

    suspend fun insertModel(model: Model)

    suspend fun updateModel(model: Model)

    suspend fun deleteModel(model: Model)
}