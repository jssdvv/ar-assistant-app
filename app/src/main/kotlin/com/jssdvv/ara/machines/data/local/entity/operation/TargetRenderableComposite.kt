package com.jssdvv.ara.machines.data.local.entity.operation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.jssdvv.ara.machines.data.local.entity.ModelEntity

@Entity(
    tableName = TargetRenderableComposite.TABLE_NAME,
    primaryKeys = [
        TargetRenderableComposite.COLUMN_OPERATION_ID,
        TargetRenderableComposite.COLUMN_MODEL_ID,
        TargetRenderableComposite.COLUMN_RENDERABLE_INDEX
    ],
    foreignKeys = [
        ForeignKey(
            entity = OperationEntity::class,
            parentColumns = [OperationEntity.COLUMN_ID],
            childColumns = [TargetRenderableComposite.COLUMN_OPERATION_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ModelEntity::class,
            parentColumns = [ModelEntity.COLUMN_ID],
            childColumns = [TargetRenderableComposite.COLUMN_MODEL_ID],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = [TargetRenderableComposite.COLUMN_OPERATION_ID]),
        Index(value = [TargetRenderableComposite.COLUMN_MODEL_ID])
    ]
)
data class TargetRenderableComposite(
    @ColumnInfo(name = COLUMN_OPERATION_ID)
    val operationId: Int,

    @ColumnInfo(name = COLUMN_MODEL_ID)
    val modelId: Int,

    @ColumnInfo(name = COLUMN_RENDERABLE_INDEX)
    val renderableIndex: Int,

    @ColumnInfo(name = COLUMN_RENDERABLE_NAME)
    val renderableName: String
) {
    companion object {
        const val TABLE_NAME = "target_renderable_composite"
        const val COLUMN_OPERATION_ID = "operation_id"
        const val COLUMN_MODEL_ID = "model_id"
        const val COLUMN_RENDERABLE_INDEX = "renderable_index"
        const val COLUMN_RENDERABLE_NAME = "renderable_name"
    }
}