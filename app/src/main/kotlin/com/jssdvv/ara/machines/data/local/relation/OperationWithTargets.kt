package com.jssdvv.ara.machines.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.jssdvv.ara.machines.data.local.entity.operation.OperationEntity
import com.jssdvv.ara.machines.data.local.entity.operation.RenderableTargetComposite

// 1 to N
data class OperationWithTargets(
    @Embedded
    val operation: OperationEntity,

    @Relation(
        parentColumn = OperationEntity.COLUMN_ID,
        entityColumn = RenderableTargetComposite.COLUMN_OPERATION_ID
    )
    val targets: List<RenderableTargetComposite>
)
