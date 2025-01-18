package com.jssdvv.ara.core.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ModelWithAnimations(
    @Embedded val model: ModelEntity,
    @Relation(
        parentColumn = "modelId",
        entityColumn = "modelId"
    )
    val animationList: List<AnimationEntity>
)