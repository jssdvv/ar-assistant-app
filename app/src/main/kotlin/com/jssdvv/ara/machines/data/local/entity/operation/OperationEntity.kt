package com.jssdvv.ara.machines.data.local.entity.operation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jssdvv.ara.machines.data.local.entity.StepEntity
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.type.OperationType

/**
 * Entity table for storing the animation information items of a certain GLB model.
 */
@Entity(
    tableName = OperationEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = StepEntity::class,
            parentColumns = [StepEntity.COLUMN_ID],
            childColumns = [OperationEntity.COLUMN_STEP_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = [OperationEntity.COLUMN_STEP_ID])
    ]
)
data class OperationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_STEP_ID)
    val stepId: Int,

    @ColumnInfo(name = COLUMN_ORDER)
    val order: Int,

    @ColumnInfo(name = COLUMN_TITLE)
    val title: String,

    @ColumnInfo(name = COLUMN_TYPE)
    val type: OperationType,

    @ColumnInfo(name = COLUMN_DELAY)
    val delay: Float,

    @ColumnInfo(name = COLUMN_DURATION)
    val duration: Float,

    @ColumnInfo(name = COLUMN_AXIS)
    val axis: Axis,

    @ColumnInfo(name = COLUMN_TURNS)
    val turns: Float,

    @ColumnInfo(name = COLUMN_IS_GLOBAL)
    val isGlobal: Boolean,

    @ColumnInfo(name = COLUMN_OFFSET_VX)
    val offsetVx: Float,

    @ColumnInfo(name = COLUMN_OFFSET_VY)
    val offsetVy: Float,

    @ColumnInfo(name = COLUMN_OFFSET_VZ)
    val offsetVz: Float,

    @ColumnInfo(name = COLUMN_OFFSET_QX)
    val offsetQx: Float,

    @ColumnInfo(name = COLUMN_OFFSET_QY)
    val offsetQy: Float,

    @ColumnInfo(name = COLUMN_OFFSET_QZ)
    val offsetQz: Float,

    @ColumnInfo(name = COLUMN_OFFSET_QW)
    val offsetQw: Float
) {
    companion object {
        const val TABLE_NAME = "operation"
        const val COLUMN_ID = "id"
        const val COLUMN_STEP_ID = "step_id"
        const val COLUMN_ORDER = "order"
        const val COLUMN_TITLE = "title"

        // Animation
        const val COLUMN_TYPE = "type"
        const val COLUMN_DELAY = "delay"
        const val COLUMN_DURATION = "duration"

        // Renderables
        const val COLUMN_AXIS = "axis"
        const val COLUMN_TURNS = "turns"
        const val COLUMN_IS_GLOBAL = "is_global"
        const val COLUMN_OFFSET_VX = "offset_vx"
        const val COLUMN_OFFSET_VY = "offset_vy"
        const val COLUMN_OFFSET_VZ = "offset_vz"
        const val COLUMN_OFFSET_QX = "offset_qx"
        const val COLUMN_OFFSET_QY = "offset_qy"
        const val COLUMN_OFFSET_QZ = "offset_qz"
        const val COLUMN_OFFSET_QW = "offset_qw"
    }
}