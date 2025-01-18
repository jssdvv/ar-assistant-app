package com.jssdvv.ara.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "animationTable",
    foreignKeys = [
        ForeignKey(
            entity = ModelEntity::class,
            parentColumns = ["modelId"],
            childColumns = ["modelId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(
            value = ["modelId"]
        )
    ]
)
data class AnimationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "animationId")
    val animationId: Int = 0,

    @ColumnInfo(name = "modelId")
    val modelId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "index")
    val index: Int,

    @ColumnInfo(name = "description")
    val description: String,
)

class InvalidAnimationException(exception: String) : Exception(exception)