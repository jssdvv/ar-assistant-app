package com.jssdvv.ara.machines.data.local.entity.machine

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

/**
 * Entity representing the motor details.
 *
 * @property [id] Unique identifier for the motor (auto-generated).
 * @property [machineId] Foreign key referencing a Machine.
 * @property [brand] Brand of the motor.
 * @property [model] Model of the motor (MODEL).
 * @property [serialNumber] Serial number of the motor (S/N).
 * @property [productNumber] Product code (P/N).
 * @property [fabricationCountry] Country of manufacture.
 * @property [standards] Applied standard (EN).
 * @property [fabricationYear] Year of manufacture.
 * @property [price] Price in Colombian currency (COP).
 * @property [acquisitionDate] Year of acquisition.
 * @property [createdAt] CreationDate when created.
 * @property [modifiedAt] CreationDate when updated.
 */
@Entity(
    tableName = MotorIdentityEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = MachineEntity::class,
            parentColumns = [MachineEntity.COLUMN_ID],
            childColumns = [MotorIdentityEntity.COLUMN_MACHINE_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(
            value = [MotorIdentityEntity.COLUMN_MACHINE_ID]
        )
    ]
)
data class MotorIdentityEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_MACHINE_ID)
    val machineId: Int,

    @ColumnInfo(name = COLUMN_BRAND)
    val brand: String?,

    @ColumnInfo(name = COLUMN_MODEL)
    val model: String?,

    @ColumnInfo(name = COLUMN_SERIAL_NUMBER)
    val serialNumber: String?,

    @ColumnInfo(name = COLUMN_PRODUCT_NUMBER)
    val productNumber: String?,

    @ColumnInfo(name = COLUMN_FABRICATION_COUNTRY)
    val fabricationCountry: String?,

    @ColumnInfo(name = COLUMN_STANDARDS)
    val standards: String?,

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
        const val TABLE_NAME = "motor_identity"
        const val COLUMN_ID = "id"
        const val COLUMN_MACHINE_ID = "machine_id"
        const val COLUMN_BRAND = "brand"
        const val COLUMN_MODEL = "model"
        const val COLUMN_SERIAL_NUMBER = "serial_number"
        const val COLUMN_PRODUCT_NUMBER = "product_number"
        const val COLUMN_FABRICATION_COUNTRY = "fabrication_country"
        const val COLUMN_STANDARDS = "standards"
        const val COLUMN_FABRICATION_YEAR = "fabrication_year"
        const val COLUMN_PRICE = "price"
        const val COLUMN_ACQUISITION_DATE = "acquisition_date"
        const val COLUMN_IMAGE_URI = "image_uri"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_MODIFIED_AT = "modified_at"
    }
}