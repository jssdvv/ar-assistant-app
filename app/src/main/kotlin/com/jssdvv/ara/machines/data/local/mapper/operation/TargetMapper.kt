package com.jssdvv.ara.machines.data.local.mapper.operation

import com.jssdvv.ara.machines.data.local.entity.operation.PivotComposite
import com.jssdvv.ara.machines.domain.model.Pivot

fun PivotComposite.toDomain() = Pivot(
    modelId = modelId,
    xxh3 = hash
)

fun Pivot.toComposite(operationId: Int) = PivotComposite(
    operationId = operationId,
    modelId = modelId,
    hash = xxh3
)