package com.jssdvv.ara.machines.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.data.local.dao.StepDao
import com.jssdvv.ara.machines.data.local.entity.StepEntity
import com.jssdvv.ara.machines.data.local.mapper.toDomain
import com.jssdvv.ara.machines.data.local.mapper.toEntity
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StepRepositoryImpl(
    private val dao: StepDao,
) : StepRepository {
    override fun selectStepsOrdered(
        activityId: Int,
        orderType: OrderType
    ): Flow<List<Step>> {
        val query = """
            SELECT * FROM ${StepEntity.TABLE_NAME}
            WHERE ${StepEntity.COLUMN_ACTIVITY_ID} = ?
            ORDER BY `${StepEntity.COLUMN_ORDER}` ${orderType.asString()}
        """.trimIndent()
        val simpleSQLiteQuery = SimpleSQLiteQuery(query, arrayOf(activityId.toString()))
        return dao.selectStepsOrdered(simpleSQLiteQuery)
            .map { it.map(StepEntity::toDomain) }
    }

    override fun selectStepById(id: Int): Flow<Step?> =
        dao.selectStepById(id).map { it?.toDomain() }

    override suspend fun upsertStep(vararg model: Step) =
        dao.upsertStep(*model.map(Step::toEntity).toTypedArray())

    override suspend fun deleteStep(vararg model: Step) =
        dao.deleteStep(*model.map(Step::toEntity).toTypedArray())

}

