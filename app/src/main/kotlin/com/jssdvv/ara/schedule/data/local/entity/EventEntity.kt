package com.jssdvv.ara.schedule.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jssdvv.ara.machines.data.local.entity.ActivityEntity
import com.jssdvv.ara.schedule.domain.type.RecurrenceUnit
import java.time.LocalDate

@Entity(
    tableName = EventEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = [ActivityEntity.COLUMN_ID],
            childColumns = [EventEntity.COLUMN_ACTIVITY_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = [EventEntity.COLUMN_ACTIVITY_ID])
    ]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_ACTIVITY_ID)
    val activityId: Int,

    @ColumnInfo(name = COLUMN_TITLE)
    val title: String,

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    val description: String?,

    @ColumnInfo(name = COLUMN_DATE)
    val date: LocalDate,

    @ColumnInfo(name = COLUMN_RECURRENT)
    val recurrent: Boolean,

    @ColumnInfo(name = COLUMN_QUANTITY)
    val quantity: Int,

    @ColumnInfo(name = COLUMN_RECURRENCE_UNIT)
    val recurrenceUnit: RecurrenceUnit
) {
    companion object {
        const val TABLE_NAME = "event"
        const val COLUMN_ID = "id"
        const val COLUMN_ACTIVITY_ID = "activity_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DATE = "date"
        const val COLUMN_RECURRENT = "recurrent"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_RECURRENCE_UNIT = "recurrence_unit"
    }
}