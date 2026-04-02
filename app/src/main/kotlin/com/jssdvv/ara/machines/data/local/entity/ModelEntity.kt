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
 * @property [offsetVx] X offset vector component respective to the origin point.
 * @property [offsetVy] Y offset vector component respective to the origin point.
 * @property [offsetVz] Z offset vector component respective to the origin point.
 * @property [offsetQx] X offset quaternion component respective to the origin point.
 * @property [offsetQy] Y offset quaternion component respective to the origin point.
 * @property [offsetQz] Z offset quaternion component respective to the origin point.
 * @property [offsetQw] W offset quaternion component respective to the origin point.
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

    @ColumnInfo(name = COLUMN_OFFSET_VX)
    val offsetVx: Float = 0F,

    @ColumnInfo(name = COLUMN_OFFSET_VY)
    val offsetVy: Float = 0F,

    @ColumnInfo(name = COLUMN_OFFSET_VZ)
    val offsetVz: Float = 0F,

    @ColumnInfo(name = COLUMN_OFFSET_QX)
    val offsetQx: Float = 0F,

    @ColumnInfo(name = COLUMN_OFFSET_QY)
    val offsetQy: Float = 0F,

    @ColumnInfo(name = COLUMN_OFFSET_QZ)
    val offsetQz: Float = 0F,

    @ColumnInfo(name = COLUMN_OFFSET_QW)
    val offsetQw: Float = 1F,
) {
    companion object {
        const val TABLE_NAME = "model"
        const val COLUMN_ID = "id"
        const val COLUMN_MACHINE_ID = "machine_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_GLB_URI = "glb_uri" // glTF Binary File

        const val COLUMN_CALIBRATED = "calibrated"
        const val COLUMN_OFFSET_VX = "offset_vx"
        const val COLUMN_OFFSET_VY = "offset_vy"
        const val COLUMN_OFFSET_VZ = "offset_vz"
        const val COLUMN_OFFSET_QX = "offset_qx"
        const val COLUMN_OFFSET_QY = "offset_qy"
        const val COLUMN_OFFSET_QZ = "offset_qz"
        const val COLUMN_OFFSET_QW = "offset_qw"
    }
}