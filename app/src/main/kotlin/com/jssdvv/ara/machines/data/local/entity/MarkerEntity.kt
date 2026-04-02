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

    @ColumnInfo(name = COLUMN_ORIGIN_OFFSET_VX)
    val originOffsetVx: Float,

    @ColumnInfo(name = COLUMN_ORIGIN_OFFSET_VY)
    val originOffsetVy: Float,

    @ColumnInfo(name = COLUMN_ORIGIN_OFFSET_VZ)
    val originOffsetVz: Float,

    @ColumnInfo(name = COLUMN_ORIGIN_OFFSET_QX)
    val originOffsetQx: Float,

    @ColumnInfo(name = COLUMN_ORIGIN_OFFSET_QY)
    val originOffsetQy: Float,

    @ColumnInfo(name = COLUMN_ORIGIN_OFFSET_QZ)
    val originOffsetQz: Float,

    @ColumnInfo(name = COLUMN_ORIGIN_OFFSET_QW)
    val originOffsetQw: Float,
) {
    companion object {
        const val TABLE_NAME = "marker"
        const val COLUMN_ID = "id"
        const val COLUMN_MACHINE_ID = "machine_id"
        const val COLUMN_INDEX = "index"
        const val COLUMN_SIZE_CENTIMETERS = "size_centimeters"
        const val COLUMN_IMAGE_URI = "image_uri"

        const val COLUMN_CALIBRATED = "calibrated"
        const val COLUMN_ORIGIN_OFFSET_VX = "origin_offset_vx"
        const val COLUMN_ORIGIN_OFFSET_VY = "origin_offset_vy"
        const val COLUMN_ORIGIN_OFFSET_VZ = "origin_offset_vz"

        const val COLUMN_ORIGIN_OFFSET_QX = "origin_offset_qx"
        const val COLUMN_ORIGIN_OFFSET_QY = "origin_offset_qy"
        const val COLUMN_ORIGIN_OFFSET_QZ = "origin_offset_qz"
        const val COLUMN_ORIGIN_OFFSET_QW = "origin_offset_qw"
    }
}
