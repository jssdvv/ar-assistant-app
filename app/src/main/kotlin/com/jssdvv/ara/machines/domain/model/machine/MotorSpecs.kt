package com.jssdvv.ara.machines.domain.model.machine

import java.time.Instant

/**
 * Data class representing the specifications of a motor.
 *
 * @property [id] Unique identifier for motor specifications.
 * @property [machineId] Foreign key referencing a Machine.
 * @property [effClass] Efficiency class (IE).
 * @property [phasesNumber] Number of phases (PH ~).
 * @property [nominalPower] Nominal power (kW).
 * @property [frequency] Frequency in Hz (Hz).
 * @property [rpm] Speed in revolutions per minute (min⁻¹).
 * @property [rpmRange] Speed range (CT).
 * @property [nominalVoltage] Line voltage / Phase voltage in V (V).
 * @property [nominalCurrent] Line current / Phase current in A (A).
 * @property [serviceFactor] Service factor (S.F.).
 * @property [powerFactor] Power factor (P.F.).
 * @property [duty] Duty cycle (DUTY).
 * @property [roomTemp] Ambient temperature in Celsius (AMB.).
 * @property [energyEff] Energy efficiency (EFF.).
 * @property [maxAltitude] Maximum operating altitude in meters above sea level (m.a.s.l).
 * @property [ingressProtection] Ingress Protection (IP).
 * @property [mountingType] Mounting type (IM).
 * @property [frameType] Frame type (FR).
 * @property [coolingMethod] Cooling method (IC).
 * @property [driveEnd] Drive-end bearings (DE).
 * @property [nonDriveEnd] Non-drive-end bearings (NDE).
 * @property [insulationClass] Insulation class (INS.CL.).
 * @property [insulationClassTemp] Insulation class temperature (ΔT).
 * @property [weight] Weight in kg (kg).
 * @property [createdAt] CreationDate when created.
 * @property [modifiedAt] CreationDate when updated.
 */
data class MotorSpecs(
    val id: Int = 0,
    val machineId: Int,
    val effClass: String? = null,
    val phasesNumber: Int? = null,
    val nominalPower: Double? = null,
    val frequency: Double? = null,
    val rpm: Int? = null,
    val rpmRange: String? = null,
    val nominalVoltage: String? = null,
    val nominalCurrent: String? = null,
    val serviceFactor: Double? = null,
    val powerFactor: Double? = null,
    val duty: String? = null,
    val roomTemp: Double? = null,
    val energyEff: Double? = null,
    val maxAltitude: Int? = null,
    val ingressProtection: String? = null,
    val mountingType: String? = null,
    val frameType: String? = null,
    val coolingMethod: String? = null,
    val driveEnd: String? = null,
    val nonDriveEnd: String? = null,
    val insulationClass: String? = null,
    val insulationClassTemp: Double? = null,
    val weight: Double? = null,
    val createdAt: Instant = Instant.now(),
    val modifiedAt: Instant = Instant.now()
)