package com.jssdvv.ara.machines.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.jssdvv.ara.machines.data.local.entity.machine.MachineEntity
import com.jssdvv.ara.machines.data.local.entity.machine.MachineSpecsEntity
import com.jssdvv.ara.machines.data.local.entity.machine.MotorIdentityEntity
import com.jssdvv.ara.machines.data.local.entity.machine.MotorSpecsEntity
import com.jssdvv.ara.machines.data.local.relation.MachineAndDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface MachineDao {
    @Transaction
    @Query(
        """
        SELECT * FROM ${MachineEntity.TABLE_NAME}
        WHERE ${MachineEntity.COLUMN_ID} = :machineId LIMIT 1
        """
    )
    suspend fun selectEntityAndDetails(machineId: Int): MachineAndDetails

    @RawQuery(observedEntities = [MachineEntity::class])
    fun searchEntitiesOrdered(query: SupportSQLiteQuery): Flow<List<MachineEntity>>

    @RawQuery(observedEntities = [MachineEntity::class])
    fun selectEntitiesOrdered(query: SupportSQLiteQuery): Flow<List<MachineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(entity: MachineEntity): Long

    @Upsert
    suspend fun upsertEntity(vararg entity: MachineEntity)

    @Delete
    suspend fun deleteEntity(vararg entity: MachineEntity)

    @Upsert
    suspend fun upsertEntitySpecs(vararg entity: MachineSpecsEntity)

    @Delete
    suspend fun deleteEntitySpecs(vararg entity: MachineSpecsEntity)

    @Upsert
    suspend fun upsertMotorIdentity(vararg model: MotorIdentityEntity)

    @Delete
    suspend fun deleteMotorIdentity(vararg model: MotorIdentityEntity)

    @Upsert
    suspend fun upsertMotorSpecs(vararg model: MotorSpecsEntity)

    @Delete
    suspend fun deleteMotorSpecs(vararg model: MotorSpecsEntity)

    @Transaction
    suspend fun upsertMachineAndDetails(data: MachineAndDetails) {

        val generatedId = insertEntity(data.machine).toInt()

        upsertEntitySpecs(data.machineSpecs.copy(machineId = generatedId))
        upsertMotorIdentity(data.motorIdentity.copy(machineId = generatedId))
        upsertMotorSpecs(data.motorSpecs.copy(machineId = generatedId))
    }
}