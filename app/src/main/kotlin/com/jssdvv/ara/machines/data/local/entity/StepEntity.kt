package com.jssdvv.ara.machines.data.local.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity table for storing the steps of an activity.
 *
 * @property [id] Primary key.
 * @property [activityId] Foreign key referencing an [ActivityEntity].
 * @property [order] Order number of the step.
 * @property [title] Title of the step.
 * @property [description] Description of the step.
 * @property [imageUri] Image file [Uri] of the step.
 */
@Entity(
    tableName = StepEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = [ActivityEntity.COLUMN_ID],
            childColumns = [StepEntity.COLUMN_ACTIVITY_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = [StepEntity.COLUMN_ACTIVITY_ID])
    ]
)
data class StepEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_ACTIVITY_ID)
    val activityId: Int,

    @ColumnInfo(name = COLUMN_ORDER)
    val order: Int,

    @ColumnInfo(name = COLUMN_TITLE)
    val title: String,

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    val description: String?,

    @ColumnInfo(name = COLUMN_IMAGE_URI)
    val imageUri: Uri?,
) {
    companion object {
        const val TABLE_NAME = "step"
        const val COLUMN_ID = "id"
        const val COLUMN_ACTIVITY_ID = "activity_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_ORDER = "order"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IMAGE_URI = "image_uri"
    }
}