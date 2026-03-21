package com.jssdvv.ara.machines.domain.repository

import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.domain.model.Step
import kotlinx.coroutines.flow.Flow

interface StepRepository {
    fun selectStepsOrdered(activityId: Int, orderType: OrderType): Flow<List<Step>>
    fun selectStepById(id: Int): Flow<Step?>
    suspend fun upsertStep(vararg model: Step)
    suspend fun deleteStep(vararg model: Step)
}