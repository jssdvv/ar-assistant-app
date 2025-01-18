package com.jssdvv.ara.core.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MachineWithActivities(
    @Embedded val machine: MachineEntity,
    @Relation(
        parentColumn = "machineId",
        entityColumn = "machineId"
    )
    val activityList: List<ActivityEntity>
)

data class MachineAndFabricationData(
    @Embedded val machine: MachineEntity,
    @Relation(
        parentColumn = "machineId",
        entityColumn = "machineId"
    )
    val fabricationData: FabricationDataEntity
)

data class MachineAndMotor(
    @Embedded val machine: MachineEntity,
    @Relation(
        parentColumn = "machineId",
        entityColumn = "machineId"
    )
    val motor: MotorEntity
)

data class MachineAndModel(
    @Embedded val machine: MachineEntity,
    @Relation(
        parentColumn = "machineId",
        entityColumn = "machineId"
    )
    val model: ModelEntity
)