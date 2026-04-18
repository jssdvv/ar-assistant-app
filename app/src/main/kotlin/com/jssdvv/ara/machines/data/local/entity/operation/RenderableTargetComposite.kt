package com.jssdvv.ara.machines.data.local.entity.operation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.jssdvv.ara.machines.data.local.entity.ModelEntity

@Entity(
    tableName = RenderableTargetComposite.TABLE_NAME,
    primaryKeys = [
        RenderableTargetComposite.COLUMN_OPERATION_ID,
        RenderableTargetComposite.COLUMN_MODEL_ID,
        RenderableTargetComposite.COLUMN_XXH3
    ],
    foreignKeys = [
        ForeignKey(
            entity = OperationEntity::class,
            parentColumns = [OperationEntity.COLUMN_ID],
            childColumns = [RenderableTargetComposite.COLUMN_OPERATION_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ModelEntity::class,
            parentColumns = [ModelEntity.COLUMN_ID],
            childColumns = [RenderableTargetComposite.COLUMN_MODEL_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = [RenderableTargetComposite.COLUMN_OPERATION_ID]),
        Index(value = [RenderableTargetComposite.COLUMN_MODEL_ID])
    ]
)
data class RenderableTargetComposite(
    @ColumnInfo(name = COLUMN_OPERATION_ID)
    val operationId: Int,

    @ColumnInfo(name = COLUMN_MODEL_ID)
    val modelId: Int,

    @ColumnInfo(name = COLUMN_XXH3)
    val xxh3: Long,

    @ColumnInfo(name = COLUMN_NAME)
    val name: String
) {
    companion object {
        const val TABLE_NAME = "renderable_target_composite"
        const val COLUMN_OPERATION_ID = "operation_id"
        const val COLUMN_MODEL_ID = "model_id"
        const val COLUMN_XXH3 = "xxh3"
        const val COLUMN_NAME = "name"
    }
}