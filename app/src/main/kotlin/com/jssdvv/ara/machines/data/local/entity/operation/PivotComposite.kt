package com.jssdvv.ara.machines.data.local.entity.operation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.jssdvv.ara.machines.data.local.entity.ModelEntity

@Entity(
    tableName = PivotComposite.TABLE_NAME,
    primaryKeys = [
        PivotComposite.COLUMN_OPERATION_ID,
        PivotComposite.COLUMN_MODEL_ID,
        PivotComposite.COLUMN_HASH
    ],
    foreignKeys = [
        ForeignKey(
            entity = OperationEntity::class,
            parentColumns = [OperationEntity.COLUMN_ID],
            childColumns = [PivotComposite.COLUMN_OPERATION_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ModelEntity::class,
            parentColumns = [ModelEntity.COLUMN_ID],
            childColumns = [PivotComposite.COLUMN_MODEL_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = [PivotComposite.COLUMN_OPERATION_ID]),
        Index(value = [PivotComposite.COLUMN_MODEL_ID])
    ]
)
data class PivotComposite(
    @ColumnInfo(name = COLUMN_OPERATION_ID)
    val operationId: Int,

    @ColumnInfo(name = COLUMN_MODEL_ID)
    val modelId: Int,

    @ColumnInfo(name = COLUMN_HASH)
    val hash: Long,
) {
    companion object {
        const val TABLE_NAME = "pivot_composite"
        const val COLUMN_OPERATION_ID = "operation_id"
        const val COLUMN_MODEL_ID = "model_id"
        const val COLUMN_HASH = "hash"
    }
}