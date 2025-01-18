package com.jssdvv.ara.machinery.domain.usecase

import com.jssdvv.ara.core.data.local.entity.ActivityEntity
import com.jssdvv.ara.core.domain.model.Activity
import com.jssdvv.ara.core.domain.repository.ActivityRepository

class DeleteActivity(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke(model: Activity) {
        repository.deleteActivity(model)
    }
}