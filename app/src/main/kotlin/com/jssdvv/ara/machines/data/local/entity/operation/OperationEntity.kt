package com.jssdvv.ara.machines.data.local.entity.operation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jssdvv.ara.machines.data.local.entity.StepEntity
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

    @ColumnInfo(name = COLUMN_ORDER_NUMBER)
    val orderNumber: Int,

    @ColumnInfo(name = COLUMN_TITLE)
    val title: String,

    @ColumnInfo(name = COLUMN_TYPE)
    val type: OperationType,

    @ColumnInfo(name = COLUMN_DURATION)
    val duration: Float,

    @ColumnInfo(name = COLUMN_DELAY)
    val delay: Float,

    @ColumnInfo(name = COLUMN_OFFSET_X_VECTOR)
    val offsetXVector: Float,

    @ColumnInfo(name = COLUMN_OFFSET_Y_VECTOR)
    val offsetYVector: Float,

    @ColumnInfo(name = COLUMN_OFFSET_Z_VECTOR)
    val offsetZVector: Float,

    @ColumnInfo(name = COLUMN_OFFSET_X_QUATERNION)
    val offsetXQuaternion: Float,

    @ColumnInfo(name = COLUMN_OFFSET_Y_QUATERNION)
    val offsetYQuaternion: Float,

    @ColumnInfo(name = COLUMN_OFFSET_Z_QUATERNION)
    val offsetZQuaternion: Float,

    @ColumnInfo(name = COLUMN_OFFSET_W_QUATERNION)
    val offsetWQuaternion: Float,

    @ColumnInfo(name = COLUMN_SCREW_PITCH)
    val screwPitch: Float,

    @ColumnInfo(name = COLUMN_AXIS_X)
    val axisX: Float,

    @ColumnInfo(name = COLUMN_AXIS_Y)
    val axisY: Float,

    @ColumnInfo(name = COLUMN_AXIS_Z)
    val axisZ: Float,

    @ColumnInfo(name = COLUMN_PIVOT_X)
    val pivotX: Float,

    @ColumnInfo(name = COLUMN_PIVOT_Y)
    val pivotY: Float,

    @ColumnInfo(name = COLUMN_PIVOT_Z)
    val pivotZ: Float
) {
    companion object {
        const val TABLE_NAME = "operation"
        const val COLUMN_ID = "id"
        const val COLUMN_STEP_ID = "step_id"
        const val COLUMN_ORDER_NUMBER = "order_number"
        const val COLUMN_TITLE = "title"

        const val COLUMN_TYPE = "type"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_DELAY = "delay"

        // On local coordinates
        const val COLUMN_OFFSET_X_VECTOR = "offset_x_vector"
        const val COLUMN_OFFSET_Y_VECTOR = "offset_y_vector"
        const val COLUMN_OFFSET_Z_VECTOR = "offset_z_vector"

        const val COLUMN_OFFSET_X_QUATERNION = "offset_x_quaternion"
        const val COLUMN_OFFSET_Y_QUATERNION = "offset_y_quaternion"
        const val COLUMN_OFFSET_Z_QUATERNION = "offset_z_quaternion"
        const val COLUMN_OFFSET_W_QUATERNION = "offset_w_quaternion"

        const val COLUMN_SCREW_PITCH = "screw_pitch"

        const val COLUMN_AXIS_X = "axis_x"
        const val COLUMN_AXIS_Y = "axis_y"
        const val COLUMN_AXIS_Z = "axis_z"

        const val COLUMN_PIVOT_X = "pivot_x"
        const val COLUMN_PIVOT_Y = "pivot_y"
        const val COLUMN_PIVOT_Z = "pivot_z"
    }
}