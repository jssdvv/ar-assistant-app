package com.jssdvv.ara.machines.domain.model.machine

import android.net.Uri
import com.jssdvv.ara.machines.domain.model.Activity
import com.jssdvv.ara.machines.domain.type.MachineType
import java.time.Instant
import java.time.LocalDate

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
    val acquisitionDate: LocalDate? = null,
    val imageUri: Uri? = null,
    val createdAt: Instant = Instant.now(),
    val modifiedAt: Instant = Instant.now()
)

data class MachineDetails(
    val machine: Machine,
    val machineSpecs: MachineSpecs = MachineSpecs(machineId = machine.id),
    val motorIdentity: MotorIdentity = MotorIdentity(machineId = machine.id),
    val motorSpecs: MotorSpecs = MotorSpecs(machineId = machine.id)
)

data class MachineActivities(
    val machine: Machine,
    val activities: List<Activity>
)