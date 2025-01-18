package com.jssdvv.ara.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.json.Json

@Entity(
    tableName = "stepTable",
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["activityId"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(
            value = ["activityId"]
        )
    ]
)
data class StepEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "stepId")
    val stepId: Int = 0,

    @ColumnInfo(name = "activityId")
    val activityId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "orderNumber")
    val orderNumber: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "models")
    val models: String,

    @ColumnInfo(name = "tools")
    val tools: String,

    @ColumnInfo(name = "parts")
    val parts: String,
)

class InvalidStepException(exception: String) : Exception(exception)