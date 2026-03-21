package com.jssdvv.ara.machines.data.local.dao

import androidx.room.Dao
import androidx.room.Update
import com.jssdvv.ara.machines.data.local.entity.machine.MotorIdentityEntity
import com.jssdvv.ara.machines.data.local.entity.machine.MotorSpecsEntity

@Dao
interface MotorDao {

    @Update
    suspend fun updateEntity(vararg entity: MotorIdentityEntity)

    @Update
    suspend fun updateEntitySpecs(vararg entity: MotorSpecsEntity)
}