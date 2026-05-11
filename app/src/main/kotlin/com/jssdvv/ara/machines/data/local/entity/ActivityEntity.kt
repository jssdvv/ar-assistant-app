package com.jssdvv.ara.machines.data.local.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jssdvv.ara.machines.data.local.entity.machine.MachineEntity
import com.jssdvv.ara.machines.domain.type.ActivityType
import java.time.Instant

/**
 * Entity representing a maintenance activity for a machine.
 *
 * @property [id] Unique identifier for the activity.
 * @property [machineId] Foreign key referencing the associated machine.
 * @property [name] Name of the activity.
 * @property [type] Type of maintenance activity.
 * @property [description] Detailed description of the activity.
 * @property [frequency] Recurrence interval of the activity.
 * @property [frequencyUnit] Unit of time for the frequency (e.g., days, weeks, months).
 * @property [imageUri] UriType of an associated image for the activity.
 * @property [createdAt] CreationDate when created.
 * @property [modifiedAt] CreationDate when updated.
 *
 * @see [ActivityType]
 */
@Entity(
    tableName = ActivityEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = MachineEntity::class,
            parentColumns = [MachineEntity.COLUMN_ID],
            childColumns = [ActivityEntity.COLUMN_MACHINE_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(
            value = [ActivityEntity.COLUMN_MACHINE_ID]
        )
    ]
)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_MACHINE_ID)
    val machineId: Int,

    @ColumnInfo(name = COLUMN_NAME)
    val name: String,

    @ColumnInfo(name = COLUMN_TYPE)
    val type: ActivityType,

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    val description: String?,

    @ColumnInfo(name = COLUMN_FREQUENCY)
    val frequency: Int?,

    @ColumnInfo(name = COLUMN_FREQUENCY_UNIT)
    val frequencyUnit: String?,

    @ColumnInfo(name = COLUMN_IMAGE_URI)
    val imageUri: Uri?,

    @ColumnInfo(name = COLUMN_CREATED_AT)
    val createdAt: Instant,

    @ColumnInfo(name = COLUMN_MODIFIED_AT)
    val modifiedAt: Instant,
) {
    companion object {
        const val TABLE_NAME = "activity"
        const val COLUMN_ID = "id"
        const val COLUMN_MACHINE_ID = "machine_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_TYPE = "type"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_FREQUENCY = "frequency"
        const val COLUMN_FREQUENCY_UNIT = "frequency_unit"
        const val COLUMN_IMAGE_URI = "image_uri"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_MODIFIED_AT = "modified_at"
    }
}