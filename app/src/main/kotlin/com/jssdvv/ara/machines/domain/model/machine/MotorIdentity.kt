package com.jssdvv.ara.machines.domain.model.machine

import android.net.Uri
import java.time.Instant
import java.time.LocalDate

/**
 * Data class representing the motor details.
 *
 * @property [id] Unique identifier for the motor.
 * @property [machineId] Foreign key referencing a Machine.
 * @property [brand] Brand of the motor.
 * @property [model] Model of the motor (MODEL).
 * @property [serialNumber] Serial number of the motor (S/N).
 * @property [productNumber] Product code (P/N).
 * @property [fabricationCountry] Country of manufacture.
 * @property [standards] Applied standards (EN).
 * @property [fabricationYear] Year of manufacture.
 * @property [price] Price in Colombian currency (COP).
 * @property [acquisitionDate] Year of acquisition.
 * @property [createdAt] CreationDate when created.
 * @property [modifiedAt] CreationDate when updated.
 */
data class MotorIdentity(
    val id: Int = 0,
    val machineId: Int,
    val brand: String? = null,
    val model: String? = null,
    val serialNumber: String? = null,
    val productNumber: String? = null,
    val fabricationCountry: String? = null,
    val standards: String? = null,
    val fabricationYear: Int? = null,
    val price: Double? = null,
    val acquisitionDate: LocalDate? = null,
    val imageUri: Uri? = null,
    val createdAt: Instant = Instant.now(),
    val modifiedAt: Instant = Instant.now()
)