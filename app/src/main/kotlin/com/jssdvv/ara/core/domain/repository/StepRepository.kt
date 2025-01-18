package com.jssdvv.ara.core.domain.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jssdvv.ara.core.data.local.entity.ActivityEntity
import com.jssdvv.ara.core.data.local.entity.ModelEntity
import com.jssdvv.ara.core.data.local.entity.StepEntity
import com.jssdvv.ara.core.domain.model.Model
import com.jssdvv.ara.core.domain.model.Step
import kotlinx.coroutines.flow.Flow

interface StepRepository {
    fun getAllStepsForActivityIdAsc(activityId: Int): Flow<List<Step>>

    fun getAllStepsForActivityIdDesc(activityId: Int): Flow<List<Step>>

    suspend fun insertStep(model: Step)

    suspend fun updateStep(model: Step)

    suspend fun deleteStep(model: Step)
}