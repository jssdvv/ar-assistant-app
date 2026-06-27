package com.jssdvv.ara.machines.data.local.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jssdvv.ara.machines.domain.type.ToolType

/**
 * Represents the entity for storing the tools to be used in step operations.
 *
 * @property [id] Unique identifier for the tool (auto-generated).
 * @property [type] The category type of the tool.
 * @property [name] Descriptive name of the tool in a specific category.
 * @property [code] Alphanumeric code used to uniquely identify the tool.
 * @property [mediaUri] [Uri] pointing to the tool's image or 2 vector path resource.
 *
 * @see [com.jssdvv.ara.machines.data.local.entity.operation.OperationEntity]
 * @see [Uri]
 */
@Entity(
    tableName = ToolEntity.TABLE_NAME,
    indices = [
        Index(value = [ToolEntity.COLUMN_ID], unique = true)
    ]
)
data class ToolEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_TYPE)
    val type: ToolType,

    @ColumnInfo(name = COLUMN_NAME)
    val name: String,

    @ColumnInfo(name = COLUMN_CODE)
    val code: String?,

    @ColumnInfo(name = COLUMN_BODY_MEDIA_URI)
    val bodyMediaUri: Uri?,

    @ColumnInfo(name = COLUMN_SYMBOL_MEDIA_URI)
    val symbolMediaUri: Uri?
) {
    companion object {
        const val TABLE_NAME = "tools"
        const val COLUMN_ID = "id"
        const val COLUMN_TYPE = "type"
        const val COLUMN_NAME = "name"
        const val COLUMN_CODE = "number"
        const val COLUMN_BODY_MEDIA_URI = "body_media_uri"
        const val COLUMN_SYMBOL_MEDIA_URI = "symbol_media_uri"
    }
}