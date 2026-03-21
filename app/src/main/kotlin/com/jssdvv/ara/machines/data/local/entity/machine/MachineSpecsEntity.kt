package com.jssdvv.ara.machines.data.local.entity.machine

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity for machine specifications.
 *
 * @property [id] Unique identifier for the machine (auto-generated).
 * @property [machineId] Foreign key for machine.
 * @property [serviceCapacity] Max workload or capacity.
 * @property [speed] Operating speed.
 * @property [lubricant] Required lubricant type.
 * @property [powerSupply] Required power source.
 * @property [weight] Machine weight.
 * @property [height] Machine height.
 * @property [length] Machine length.
 * @property [width] Machine width.
 * @property [jobDesc] Primary function/task.
 * @property [hoursPerDay] Avg. daily working hours.
 * @property [roomTemp] Recommended ambient temp.
 * @property [additionalDesc] Extra details or notes.
 * @property [createdAt] CreationDate when created.
 * @property [modifiedAt] CreationDate when updated.
 *
 * @see [MachineEntity]
 */
@Entity(
    tableName = MachineSpecsEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = MachineEntity::class,
            parentColumns = [MachineEntity.COLUMN_ID],
            childColumns = [MachineSpecsEntity.COLUMN_MACHINE_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(
            value = [MachineSpecsEntity.COLUMN_MACHINE_ID]
        )
    ]
)
data class MachineSpecsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_MACHINE_ID)
    val machineId: Int,

    @ColumnInfo(name = COLUMN_SERVICE_CAPACITY)
    val serviceCapacity: String?,

    @ColumnInfo(name = COLUMN_SPEED)
    val speed: String?,

    @ColumnInfo(name = COLUMN_LUBRICANT)
    val lubricant: String?,

    @ColumnInfo(name = COLUMN_POWER_SUPPLY)
    val powerSupply: String?,

    @ColumnInfo(name = COLUMN_WEIGHT)
    val weight: String?,

    @ColumnInfo(name = COLUMN_HEIGHT)
    val height: String?,

    @ColumnInfo(name = COLUMN_LENGTH)
    val length: String?,

    @ColumnInfo(name = COLUMN_WIDTH)
    val width: String?,

    @ColumnInfo(name = COLUMN_JOB_DESC)
    val jobDesc: String?,

    @ColumnInfo(name = COLUMN_HOURS_PER_DAY)
    val hoursPerDay: Int?,

    @ColumnInfo(name = COLUMN_ROOM_TEMP)
    val roomTemp: String?,

    @ColumnInfo(name = COLUMN_ADDITIONAL_DESC)
    val additionalDesc: String?,

    @ColumnInfo(name = COLUMN_CREATED_AT)
    val createdAt: Date,

    @ColumnInfo(name = COLUMN_MODIFIED_AT)
    val modifiedAt: Date,
) {
    companion object {
        const val TABLE_NAME = "machine_specs"
        const val COLUMN_ID = "id"
        const val COLUMN_MACHINE_ID = "machine_id"
        const val COLUMN_SERVICE_CAPACITY = "service_capacity"
        const val COLUMN_SPEED = "speed"
        const val COLUMN_LUBRICANT = "lubricant"
        const val COLUMN_POWER_SUPPLY = "power_supply"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_HEIGHT = "height"
        const val COLUMN_LENGTH = "length"
        const val COLUMN_WIDTH = "width"
        const val COLUMN_JOB_DESC = "job_desc"
        const val COLUMN_HOURS_PER_DAY = "hours_per_day"
        const val COLUMN_ROOM_TEMP = "room_temp"
        const val COLUMN_ADDITIONAL_DESC = "additional_desc"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_MODIFIED_AT = "modified_at"
    }
}