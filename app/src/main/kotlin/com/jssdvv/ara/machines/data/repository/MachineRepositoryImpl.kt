package com.jssdvv.ara.machines.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.jssdvv.ara.core.domain.type.OrderKey
import com.jssdvv.ara.core.domain.type.OrderState
import com.jssdvv.ara.machines.data.local.dao.MachineDao
import com.jssdvv.ara.machines.data.local.entity.machine.MachineEntity
import com.jssdvv.ara.machines.data.local.mapper.machine.toDomain
import com.jssdvv.ara.machines.data.local.mapper.machine.toEntity
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineDetails
import com.jssdvv.ara.machines.domain.model.machine.MachineSpecs
import com.jssdvv.ara.machines.domain.model.machine.MotorIdentity
import com.jssdvv.ara.machines.domain.model.machine.MotorSpecs
import com.jssdvv.ara.machines.domain.repository.MachineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MachineRepositoryImpl(
    private val dao: MachineDao,
) : MachineRepository {

    private fun getOrderKey(
        orderKey: OrderKey = OrderKey.NAME,
    ): String = when (orderKey) {
        OrderKey.CODE -> MachineEntity.COLUMN_CODE
        OrderKey.NAME -> MachineEntity.COLUMN_NAME
        OrderKey.TYPE -> MachineEntity.COLUMN_TYPE
        OrderKey.CREATION_DATE -> MachineEntity.COLUMN_CREATED_AT
        OrderKey.MODIFICATION_DATE -> MachineEntity.COLUMN_MODIFIED_AT
    }

    private fun buildSearchQuery(
        search: String,
        orderState: OrderState = OrderState()
    ): SupportSQLiteQuery {
        val query =
            """
            SELECT * FROM ${MachineEntity.TABLE_NAME}
            WHERE 
                ${MachineEntity.COLUMN_NAME} LIKE '%' || ? || '%' OR
                ${MachineEntity.COLUMN_CODE} LIKE '%' || ? || '%' OR
                ${MachineEntity.COLUMN_TYPE} LIKE '%' || ? || '%'
            ORDER BY ${getOrderKey(orderState.key)} ${orderState.type.queryString}
            """.trimIndent()

        return SimpleSQLiteQuery(query, arrayOf(search, search, search))
    }

    private fun buildSelectQuery(orderState: OrderState = OrderState()): SupportSQLiteQuery {
        val query =
            """
            SELECT * FROM ${MachineEntity.TABLE_NAME}
            ORDER BY ${getOrderKey(orderState.key)} ${orderState.type.queryString}
            """.trimIndent()

        return SimpleSQLiteQuery(query)
    }

    override suspend fun selectMachineAndDetailsByMachineId(machineId: Int): MachineDetails =
        dao.selectEntityAndDetails(machineId).toDomain()

    override fun searchModelsOrdered(search: String, orderState: OrderState): Flow<List<Machine>> =
        dao.selectEntitiesOrdered(buildSearchQuery(search, orderState))
            .map { it.map(MachineEntity::toDomain) }

    override fun selectModelsOrdered(orderState: OrderState): Flow<List<Machine>> =
        dao.selectEntitiesOrdered(buildSelectQuery(orderState))
            .map { it.map(MachineEntity::toDomain) }

    override suspend fun upsertMachine(vararg model: Machine)  {
        dao.upsertEntity(*model.map(Machine::toEntity).toTypedArray())
    }

    override suspend fun upsertMachineAndDetails(relation: MachineDetails)  {
        dao.upsertMachineAndDetails(relation.toEntity())
    }

    override suspend fun deleteMachine(vararg model: Machine) =
        dao.deleteEntity(*model.map(Machine::toEntity).toTypedArray())

    override suspend fun upsertMachineSpecs(vararg model: MachineSpecs) =
        dao.upsertEntitySpecs(*model.map(MachineSpecs::toEntity).toTypedArray())

    override suspend fun deleteMachineSpecs(vararg model: MachineSpecs) =
        dao.deleteEntitySpecs(*model.map(MachineSpecs::toEntity).toTypedArray())

    override suspend fun upsertMotorSpecs(vararg model: MotorSpecs) {
        dao.upsertMotorSpecs(*model.map(MotorSpecs::toEntity).toTypedArray())
    }

    override suspend fun deleteMotorSpecs(vararg model: MotorSpecs) {
        dao.deleteMotorSpecs(*model.map(MotorSpecs::toEntity).toTypedArray())
    }

    override suspend fun upsertMotorIdentity(vararg model: MotorIdentity) {
        dao.upsertMotorIdentity(*model.map(MotorIdentity::toEntity).toTypedArray())
    }

    override suspend fun deleteMotorIdentity(vararg model: MotorIdentity) {
        dao.deleteMotorIdentity(*model.map(MotorIdentity::toEntity).toTypedArray())
    }
}