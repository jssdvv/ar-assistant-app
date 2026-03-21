package com.jssdvv.ara.machines.data.local.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jssdvv.ara.machines.data.local.entity.machine.MachineEntity

/**
 * Entity table for storing augmented models for all activities of a certain machine.
 *
 * @property [id] Primary key.
 * @property [machineId] Foreign key referencing a [MachineEntity].
 * @property [name] Name of the model.
 * @property [glbUri] GLB file [Uri] of the model.
 * @property [xOffsetVector] X offset vector component respective to the origin point.
 * @property [yOffsetVector] Y offset vector component respective to the origin point.
 * @property [zOffsetVector] Z offset vector component respective to the origin point.
 * @property [xOffsetQuaternion] X offset quaternion component respective to the origin point.
 * @property [yOffsetQuaternion] Y offset quaternion component respective to the origin point.
 * @property [zOffsetQuaternion] Z offset quaternion component respective to the origin point.
 * @property [wOffsetQuaternion] W offset quaternion component respective to the origin point.
 */
@Entity(
    tableName = ModelEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = MachineEntity::class,
            parentColumns = [MachineEntity.COLUMN_ID],
            childColumns = [ModelEntity.COLUMN_MACHINE_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = [ModelEntity.COLUMN_MACHINE_ID])
    ]
)
data class ModelEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_MACHINE_ID)
    val machineId: Int,

    @ColumnInfo(name = COLUMN_NAME)
    val name: String,

    @ColumnInfo(name = COLUMN_GLB_URI)
    val glbUri: Uri,

    @ColumnInfo(name = COLUMN_CALIBRATED)
    val calibrated: Boolean,

    @ColumnInfo(name = COLUMN_X_OFFSET_VECTOR)
    val xOffsetVector: Float = 0F,

    @ColumnInfo(name = COLUMN_Y_OFFSET_VECTOR)
    val yOffsetVector: Float = 0F,

    @ColumnInfo(name = COLUMN_Z_OFFSET_VECTOR)
    val zOffsetVector: Float = 0F,

    @ColumnInfo(name = COLUMN_X_OFFSET_QUATERNION)
    val xOffsetQuaternion: Float = 0F,

    @ColumnInfo(name = COLUMN_Y_OFFSET_QUATERNION)
    val yOffsetQuaternion: Float = 0F,

    @ColumnInfo(name = COLUMN_Z_OFFSET_QUATERNION)
    val zOffsetQuaternion: Float = 0F,

    @ColumnInfo(name = COLUMN_W_OFFSET_QUATERNION)
    val wOffsetQuaternion: Float = 1F,
) {
    companion object {
        const val TABLE_NAME = "model"
        const val COLUMN_ID = "id"
        const val COLUMN_MACHINE_ID = "machine_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_GLB_URI = "glb_uri" // glTF Binary File

        const val COLUMN_CALIBRATED = "calibrated"
        const val COLUMN_X_OFFSET_VECTOR = "x_offset_vector"
        const val COLUMN_Y_OFFSET_VECTOR = "y_offset_vector"
        const val COLUMN_Z_OFFSET_VECTOR = "z_offset_vector"

        const val COLUMN_X_OFFSET_QUATERNION = "x_offset_quaternion"
        const val COLUMN_Y_OFFSET_QUATERNION = "y_offset_quaternion"
        const val COLUMN_Z_OFFSET_QUATERNION = "z_offset_quaternion"
        const val COLUMN_W_OFFSET_QUATERNION = "w_offset_quaternion"
    }
}