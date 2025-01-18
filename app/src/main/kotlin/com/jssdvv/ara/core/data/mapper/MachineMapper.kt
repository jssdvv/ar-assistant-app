package com.jssdvv.ara.core.data.mapper

import com.jssdvv.ara.core.data.local.entity.MachineEntity
import com.jssdvv.ara.core.domain.model.Machine

fun MachineEntity.toDomain(): Machine {
    return Machine(
        machineId = machineId,
        name = name,
        category = category,
        imageUri = imageUri,
        description = description,
        timestamp = timestamp
    )
}

fun Machine.toEntity(): MachineEntity {
    return MachineEntity(
        machineId = machineId,
        name = name,
        category = category,
        imageUri = imageUri,
        description = description,
        timestamp = timestamp
    )
}