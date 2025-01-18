package com.jssdvv.ara.core.data.mapper

import com.jssdvv.ara.core.data.local.entity.AnimationEntity
import com.jssdvv.ara.core.domain.model.Animation

fun AnimationEntity.toDomain(): Animation {
    return Animation(
        animationId = animationId,
        modelId = modelId,
        name = name,
        index = index,
        description = description
    )
}

fun Animation.toEntity(): AnimationEntity {
    return AnimationEntity(
        animationId = animationId,
        modelId = modelId,
        name = name,
        index = index,
        description = description
    )
}