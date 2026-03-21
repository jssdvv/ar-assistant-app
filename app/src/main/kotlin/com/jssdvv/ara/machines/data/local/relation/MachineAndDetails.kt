package com.jssdvv.ara.machines.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.jssdvv.ara.machines.data.local.entity.machine.MachineEntity
import com.jssdvv.ara.machines.data.local.entity.machine.MachineSpecsEntity
import com.jssdvv.ara.machines.data.local.entity.machine.MotorIdentityEntity
import com.jssdvv.ara.machines.data.local.entity.machine.MotorSpecsEntity

/**
 * Represents a one-to-one relationships between a [com.jssdvv.ara.machines.data.local.entity.machine.MachineEntity] and the entities:
 * [com.jssdvv.ara.machines.data.local.entity.machine.MachineSpecsEntity], [com.jssdvv.ara.machines.data.local.entity.machine.MotorIdentityEntity] and [com.jssdvv.ara.machines.data.local.entity.machine.MotorSpecsEntity].
 *
 * @property [machine] The machine entity.
 * @property [machineSpecs] The associated machine specs entity.
 * @property [motorIdentity] The motor entity.
 * @property [motorSpecs] The associated motor specs entity.
 */
data class MachineAndDetails(
    @Embedded
    val machine: MachineEntity,

    @Relation(
        parentColumn = MachineEntity.COLUMN_ID,
        entityColumn = MachineSpecsEntity.COLUMN_MACHINE_ID
    )
    val machineSpecs: MachineSpecsEntity,

    @Relation(
        parentColumn = MachineEntity.COLUMN_ID,
        entityColumn = MotorIdentityEntity.COLUMN_MACHINE_ID
    )
    val motorIdentity: MotorIdentityEntity,

    @Relation(
        parentColumn = MachineEntity.COLUMN_ID,
        entityColumn = MotorSpecsEntity.COLUMN_MACHINE_ID
    )
    val motorSpecs: MotorSpecsEntity,
)