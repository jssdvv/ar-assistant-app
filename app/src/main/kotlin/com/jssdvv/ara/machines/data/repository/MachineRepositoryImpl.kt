package com.jssdvv.ara.machines.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.data.local.dao.MachineDao
import com.jssdvv.ara.machines.data.local.entity.machine.MachineEntity
import com.jssdvv.ara.machines.data.local.mapper.machine.toDomain
import com.jssdvv.ara.machines.data.local.mapper.machine.toEntity
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineDetails
import com.jssdvv.ara.machines.domain.model.machine.MachineSpecs
import com.jssdvv.ara.machines.domain.repository.MachineRepository
import com.jssdvv.ara.machines.domain.type.MachineOrderKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MachineRepositoryImpl(
    private val dao: MachineDao,
) : MachineRepository {

    private fun getOrderKey(
        orderKey: MachineOrderKey = MachineOrderKey.NAME,
    ): String = when (orderKey) {
        MachineOrderKey.CODE -> MachineEntity.COLUMN_CODE
        MachineOrderKey.NAME -> MachineEntity.COLUMN_NAME
        MachineOrderKey.TYPE -> MachineEntity.COLUMN_TYPE
        MachineOrderKey.CREATION_DATE -> MachineEntity.COLUMN_CREATED_AT
        MachineOrderKey.MODIFICATION_DATE -> MachineEntity.COLUMN_MODIFIED_AT
    }

    private fun getOrderType(
        orderType: OrderType = OrderType.ASCENDING,
    ): String = when (orderType) {
        OrderType.ASCENDING -> "ASC"
        OrderType.DESCENDING -> "DESC"
    }

    private fun buildSearchQuery(
        search: String,
        orderKey: MachineOrderKey = MachineOrderKey.NAME,
        orderType: OrderType = OrderType.ASCENDING,
    ): SupportSQLiteQuery {
        val query =
            """
            SELECT * FROM ${MachineEntity.TABLE_NAME}
            WHERE 
                ${MachineEntity.COLUMN_NAME} LIKE '%' || ? || '%' OR
                ${MachineEntity.COLUMN_CODE} LIKE '%' || ? || '%' OR
                ${MachineEntity.COLUMN_TYPE} LIKE '%' || ? || '%'
            ORDER BY ${getOrderKey(orderKey)} ${getOrderType(orderType)}
            """.trimIndent()

        return SimpleSQLiteQuery(query, arrayOf(search, search, search))
    }

    private fun buildSelectQuery(
        orderKey: MachineOrderKey = MachineOrderKey.NAME,
        orderType: OrderType = OrderType.ASCENDING,
    ): SupportSQLiteQuery {
        val query =
            """
            SELECT * FROM ${MachineEntity.TABLE_NAME}
            ORDER BY ${getOrderKey(orderKey)} ${getOrderType(orderType)}
            """.trimIndent()

        return SimpleSQLiteQuery(query)
    }

    override suspend fun selectMachineAndDetailsByMachineId(machineId: Int): MachineDetails =
        dao.selectEntityAndDetails(machineId).toDomain()

    override fun searchModelsOrdered(
        search: String,
        orderKey: MachineOrderKey,
        orderType: OrderType,
    ): Flow<List<Machine>> =
        dao.selectEntitiesOrdered(buildSearchQuery(search, orderKey, orderType))
            .map { it.map(MachineEntity::toDomain) }

    override fun selectModelsOrdered(
        orderKey: MachineOrderKey,
        orderType: OrderType,
    ): Flow<List<Machine>> =
        dao.selectEntitiesOrdered(buildSelectQuery(orderKey, orderType))
            .map { it.map(MachineEntity::toDomain) }

    override suspend fun upsertMachine(vararg model: Machine) =
        dao.upsertEntity(*model.map(Machine::toEntity).toTypedArray())

    override suspend fun deleteMachine(vararg model: Machine) =
        dao.deleteEntity(*model.map(Machine::toEntity).toTypedArray())

    override suspend fun upsertMachineSpecs(vararg model: MachineSpecs) =
        dao.upsertEntitySpecs(*model.map(MachineSpecs::toEntity).toTypedArray())

    override suspend fun deleteMachineSpecs(vararg model: MachineSpecs) =
        dao.deleteEntitySpecs(*model.map(MachineSpecs::toEntity).toTypedArray())
}