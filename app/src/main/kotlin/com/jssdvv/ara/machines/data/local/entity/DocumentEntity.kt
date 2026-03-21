package com.jssdvv.ara.machines.data.local.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jssdvv.ara.machines.data.local.entity.machine.MachineEntity

@Entity(
    tableName = DocumentEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = MachineEntity::class,
            parentColumns = [MachineEntity.COLUMN_ID],
            childColumns = [DocumentEntity.COLUMN_MACHINE_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(
            value = [DocumentEntity.COLUMN_MACHINE_ID]
        )
    ]
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_MACHINE_ID)
    val machineId: Int,

    @ColumnInfo(name = COLUMN_NAME)
    val name: String,

    @ColumnInfo(name = COLUMN_FILE_URI)
    val fileUri: Uri,
) {
    companion object {
        const val TABLE_NAME = "document"
        const val COLUMN_ID = "id"
        const val COLUMN_MACHINE_ID = "machine_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_FILE_URI = "file_uri"
    }
}