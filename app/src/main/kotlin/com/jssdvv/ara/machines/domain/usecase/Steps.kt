package com.jssdvv.ara.machines.domain.usecase

import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow

data class StepsDataManager(
    val select: SelectSteps,
    val upsert: UpsertSteps,
    val delete: DeleteSteps,
)

class SelectSteps(private val repository: StepRepository) {
    operator fun invoke(activityId: Int, orderType: OrderType): Flow<List<Step>> =
        repository.selectStepsOrdered(activityId, orderType)

    operator fun invoke(stepId: Int): Flow<Step?> =
        repository.selectStepById(stepId)
}

class UpsertSteps(private val repository: StepRepository) {
    suspend operator fun invoke(vararg steps: Step) =
        repository.upsertStep(*steps)
}

class DeleteSteps(private val repository: StepRepository) {
    suspend operator fun invoke(vararg steps: Step) =
        repository.deleteStep(*steps)
}