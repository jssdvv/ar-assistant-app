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

    @ColumnInfo(name = COLUMN_INITIAL_X_VECTOR)
    val initialXVector: Float,

    @ColumnInfo(name = COLUMN_INITIAL_Y_VECTOR)
    val initialYVector: Float,

    @ColumnInfo(name = COLUMN_INITIAL_Z_VECTOR)
    val initialZVector: Float,

    @ColumnInfo(name = COLUMN_INITIAL_X_QUATERNION)
    val initialXQuaternion: Float,

    @ColumnInfo(name = COLUMN_INITIAL_Y_QUATERNION)
    val initialYQuaternion: Float,

    @ColumnInfo(name = COLUMN_INITIAL_Z_QUATERNION)
    val initialZQuaternion: Float,

    @ColumnInfo(name = COLUMN_INITIAL_W_QUATERNION)
    val initialWQuaternion: Float,

    @ColumnInfo(name = COLUMN_FINAL_X_VECTOR)
    val finalXVector: Float,

    @ColumnInfo(name = COLUMN_FINAL_Y_VECTOR)
    val finalYVector: Float,

    @ColumnInfo(name = COLUMN_FINAL_Z_VECTOR)
    val finalZVector: Float,

    @ColumnInfo(name = COLUMN_FINAL_X_QUATERNION)
    val finalXQuaternion: Float,

    @ColumnInfo(name = COLUMN_FINAL_Y_QUATERNION)
    val finalYQuaternion: Float,

    @ColumnInfo(name = COLUMN_FINAL_Z_QUATERNION)
    val finalZQuaternion: Float,

    @ColumnInfo(name = COLUMN_FINAL_W_QUATERNION)
    val finalWQuaternion: Float,

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
        const val COLUMN_INITIAL_X_VECTOR = "initial_x_vector"
        const val COLUMN_INITIAL_Y_VECTOR = "initial_y_vector"
        const val COLUMN_INITIAL_Z_VECTOR = "initial_z_vector"

        const val COLUMN_INITIAL_X_QUATERNION = "initial_x_quaternion"
        const val COLUMN_INITIAL_Y_QUATERNION = "initial_y_quaternion"
        const val COLUMN_INITIAL_Z_QUATERNION = "initial_z_quaternion"
        const val COLUMN_INITIAL_W_QUATERNION = "initial_w_quaternion"

        const val COLUMN_FINAL_X_VECTOR = "final_x_vector"
        const val COLUMN_FINAL_Y_VECTOR = "final_y_vector"
        const val COLUMN_FINAL_Z_VECTOR = "final_z_vector"

        const val COLUMN_FINAL_X_QUATERNION = "final_x_quaternion"
        const val COLUMN_FINAL_Y_QUATERNION = "final_y_quaternion"
        const val COLUMN_FINAL_Z_QUATERNION = "final_z_quaternion"
        const val COLUMN_FINAL_W_QUATERNION = "final_w_quaternion"

        const val COLUMN_SCREW_PITCH = "screw_pitch"

        const val COLUMN_AXIS_X = "axis_x"
        const val COLUMN_AXIS_Y = "axis_y"
        const val COLUMN_AXIS_Z = "axis_z"

        const val COLUMN_PIVOT_X = "pivot_x"
        const val COLUMN_PIVOT_Y = "pivot_y"
        const val COLUMN_PIVOT_Z = "pivot_z"
    }
}