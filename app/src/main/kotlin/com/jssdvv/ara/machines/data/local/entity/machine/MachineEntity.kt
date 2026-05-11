package com.jssdvv.ara.machines.data.local.entity.machine

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jssdvv.ara.machines.domain.type.MachineType
import java.time.Instant
import java.time.LocalDate
import java.util.Date

/**
 * Represents the entity for storing machine identification details in the database.
 *
 * @property [id] Unique identifier for the machine (auto-generated).
 * @property [code] Alphanumeric code used to uniquely identify the machine.
 * @property [name] Descriptive name of the machine.
 * @property [type] The category type of the machine.
 * @property [location] Physical location where the machine is installed.
 * @property [brand] Manufacturer or brand of the machine.
 * @property [model] Specific model name or number provided by the manufacturer.
 * @property [serial] Unique serial number assigned to the machine by the manufacturer.
 * @property [fabricationYear] Year when the machine was manufactured.
 * @property [price] Purchase cost or estimated value of the machine.
 * @property [acquisitionDate] Date when the machine was acquired by the organization.
 * @property [imageUri] [Uri] pointing to the machine's image resource.
 * @property [createdAt] Timestamp indicating when the machine record was first created.
 * @property [modifiedAt] Timestamp indicating when the machine record was last updated.
 *
 * @see [MachineType]
 * @see [Date]
 */
@Entity(
    tableName = MachineEntity.TABLE_NAME,
    indices = [
        Index(value = [MachineEntity.COLUMN_ID], unique = true)
    ]
)
data class MachineEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_CODE)
    val code: String,

    @ColumnInfo(name = COLUMN_NAME)
    val name: String,

    @ColumnInfo(name = COLUMN_TYPE)
    val type: MachineType,

    @ColumnInfo(name = COLUMN_LOCATION)
    val location: String?,

    @ColumnInfo(name = COLUMN_BRAND)
    val brand: String?,

    @ColumnInfo(name = COLUMN_MODEL)
    val model: String?,

    @ColumnInfo(name = COLUMN_SERIAL)
    val serial: String?,

    @ColumnInfo(name = COLUMN_FABRICATION_YEAR)
    val fabricationYear: Int?,

    @ColumnInfo(name = COLUMN_PRICE)
    val price: Double?,

    @ColumnInfo(name = COLUMN_ACQUISITION_DATE)
    val acquisitionDate: LocalDate?,

    @ColumnInfo(name = COLUMN_IMAGE_URI)
    val imageUri: Uri?,

    @ColumnInfo(name = COLUMN_CREATED_AT)
    val createdAt: Instant,

    @ColumnInfo(name = COLUMN_MODIFIED_AT)
    val modifiedAt: Instant,
) {
    companion object {
        const val TABLE_NAME = "machine"
        const val COLUMN_ID = "id"
        const val COLUMN_CODE = "code"
        const val COLUMN_NAME = "name"
        const val COLUMN_TYPE = "type"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_BRAND = "brand"
        const val COLUMN_MODEL = "model"
        const val COLUMN_SERIAL = "serial"
        const val COLUMN_FABRICATION_YEAR = "fabrication_year"
        const val COLUMN_PRICE = "price"
        const val COLUMN_ACQUISITION_DATE = "acquisition_date"
        const val COLUMN_IMAGE_URI = "image_uri"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_MODIFIED_AT = "modified_at"
    }
}