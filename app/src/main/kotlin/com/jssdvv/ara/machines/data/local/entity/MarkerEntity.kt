package com.jssdvv.ara.machines.data.local.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jssdvv.ara.machines.data.local.entity.machine.MachineEntity

@Entity(
    tableName = MarkerEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = MachineEntity::class,
            parentColumns = [MachineEntity.COLUMN_ID],
            childColumns = [MarkerEntity.COLUMN_MACHINE_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = [MarkerEntity.COLUMN_MACHINE_ID])
    ]
)
data class MarkerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_MACHINE_ID)
    val machineId: Int,

    @ColumnInfo(name = COLUMN_INDEX)
    val index: Int,

    @ColumnInfo(name = COLUMN_IMAGE_URI)
    val imageUri: Uri,

    @ColumnInfo(name = COLUMN_SIZE_CENTIMETERS)
    val sizeCentimeters: Float,

    @ColumnInfo(name = COLUMN_CALIBRATED)
    val calibrated: Boolean,

    @ColumnInfo(name = COLUMN_X_ORIGIN_VECTOR)
    val xOriginVector: Float,

    @ColumnInfo(name = COLUMN_Y_ORIGIN_VECTOR)
    val yOriginVector: Float,

    @ColumnInfo(name = COLUMN_Z_ORIGIN_VECTOR)
    val zOriginVector: Float,

    @ColumnInfo(name = COLUMN_X_ORIGIN_QUATERNION)
    val xOriginQuaternion: Float,

    @ColumnInfo(name = COLUMN_Y_ORIGIN_QUATERNION)
    val yOriginQuaternion: Float,

    @ColumnInfo(name = COLUMN_Z_ORIGIN_QUATERNION)
    val zOriginQuaternion: Float,

    @ColumnInfo(name = COLUMN_W_ORIGIN_QUATERNION)
    val wOriginQuaternion: Float,
) {
    companion object {
        const val TABLE_NAME = "marker"
        const val COLUMN_ID = "id"
        const val COLUMN_MACHINE_ID = "machine_id"
        const val COLUMN_INDEX = "index"
        const val COLUMN_SIZE_CENTIMETERS = "size_centimeters"
        const val COLUMN_IMAGE_URI = "image_uri"

        const val COLUMN_CALIBRATED = "calibrated"
        const val COLUMN_X_ORIGIN_VECTOR = "x_origin_vector"
        const val COLUMN_Y_ORIGIN_VECTOR = "y_origin_vector"
        const val COLUMN_Z_ORIGIN_VECTOR = "z_origin_vector"

        const val COLUMN_X_ORIGIN_QUATERNION = "x_origin_quaternion"
        const val COLUMN_Y_ORIGIN_QUATERNION = "y_origin_quaternion"
        const val COLUMN_Z_ORIGIN_QUATERNION = "z_origin_quaternion"
        const val COLUMN_W_ORIGIN_QUATERNION = "w_origin_quaternion"
    }
}
