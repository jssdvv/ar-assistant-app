package com.jssdvv.ara.machines.domain.model.machine

import android.net.Uri
import com.jssdvv.ara.machines.domain.type.MachineType
import java.util.Date

/**
 * Data class for mapping machine details from MachineEntity.
 *
 * @property [id] Unique ID for the machine.
 * @property [code] Machine code.
 * @property [name] Machine name.
 * @property [type] Machine type.
 * @property [location] Machine location.
 * @property [brand] Machine brand.
 * @property [model] Machine model.
 * @property [serial] Machine serial number.
 * @property [fabricationYear] Year of manufacture.
 * @property [price] Machine price.
 * @property [acquisitionDate] Year of acquisition.
 * @property [imageUri] [Uri] of the machine image.
 * @property [createdAt] CreationDate when created.
 * @property [modifiedAt] CreationDate when updated.
 *
 * @see [MachineType]
 */
data class Machine(
    val id: Int = 0,
    val code: String,
    val name: String,
    val type: MachineType = MachineType.UNKNOWN,
    val location: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val serial: String? = null,
    val fabricationYear: Int? = null,
    val price: Double? = null,
    val acquisitionDate: Date? = null,
    val imageUri: Uri? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)

data class MachineDetails(
    val machine: Machine,
    val machineSpecs: MachineSpecs,
    val motorIdentity: MotorIdentity,
    val motorSpecs: MotorSpecs
)