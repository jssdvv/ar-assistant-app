package com.jssdvv.ara.machinery.domain.usecase

import com.jssdvv.ara.machinery.domain.model.ActivityEntity
import com.jssdvv.ara.machinery.domain.repository.ActivityRepository

class DeleteActivity(
    private val repository: ActivityRepository
) {
    suspend operator fun invoke(entity: ActivityEntity) {
        repository.deleteActivity(entity)
    }
}