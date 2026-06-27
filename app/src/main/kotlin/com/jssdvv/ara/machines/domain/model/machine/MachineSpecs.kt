package com.jssdvv.ara.machines.domain.model.machine

import java.time.Instant

/**
 * Data class for machine specifications.
 *
 * @property [specsId] Unique ID for specifications.
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
 */
data class MachineSpecs(
    val specsId: Int = 0,
    val machineId: Int,
    val serviceCapacity: String? = null,
    val speed: String? = null,
    val lubricant: String? = null,
    val powerSupply: String? = null,
    val weight: String? = null,
    val height: String? = null,
    val length: String? = null,
    val width: String? = null,
    val jobDesc: String? = null,
    val hoursPerDay: Int? = null,
    val roomTemp: String? = null,
    val additionalDesc: String? = null,
    val createdAt: Instant = Instant.now(),
    val modifiedAt: Instant = Instant.now()
)