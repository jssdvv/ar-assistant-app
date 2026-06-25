package com.jssdvv.ara.machines.data.local.entity.machine

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Entity representing the specifications of a motor.
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
 * @property [insulationTemp] Insulation class temperature (ΔT).
 * @property [weight] Weight in kg (kg).
 * @property [createdAt] CreationDate when created.
 * @property [modifiedAt] CreationDate when updated.
 */
@Entity(
    tableName = MotorSpecsEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = MachineEntity::class,
            parentColumns = [MachineEntity.COLUMN_ID],
            childColumns = [MotorSpecsEntity.COLUMN_MACHINE_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(
            value = [MotorSpecsEntity.COLUMN_MACHINE_ID],
        )
    ]
)
data class MotorSpecsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_MACHINE_ID)
    val machineId: Int,

    @ColumnInfo(name = COLUMN_EFF_CLASS)
    val effClass: String?,

    @ColumnInfo(name = COLUMN_PHASES_NUMBER)
    val phasesNumber: Int?,

    @ColumnInfo(name = COLUMN_NOMINAL_POWER)
    val nominalPower: Double?,

    @ColumnInfo(name = COLUMN_FREQUENCY)
    val frequency: Double?,

    @ColumnInfo(name = COLUMN_RPM)
    val rpm: Int?,

    @ColumnInfo(name = COLUMN_RPM_RANGE)
    val rpmRange: String?,

    @ColumnInfo(name = COLUMN_NOMINAL_VOLTAGE)
    val nominalVoltage: String?,

    @ColumnInfo(name = COLUMN_NOMINAL_CURRENT)
    val nominalCurrent: String?,

    @ColumnInfo(name = COLUMN_SERVICE_FACTOR)
    val serviceFactor: Double?,

    @ColumnInfo(name = COLUMN_POWER_FACTOR)
    val powerFactor: Double?,

    @ColumnInfo(name = COLUMN_DUTY)
    val duty: String?,

    @ColumnInfo(name = COLUMN_ROOM_TEMP)
    val roomTemp: Double?,

    @ColumnInfo(name = COLUMN_ENERGY_EFF)
    val energyEff: Double?,

    @ColumnInfo(name = COLUMN_MAX_ALTITUDE)
    val maxAltitude: Int?,

    @ColumnInfo(name = COLUMN_INGRESS_PROTECTION)
    val ingressProtection: String?,

    @ColumnInfo(name = COLUMN_MOUNTING_TYPE)
    val mountingType: String?,

    @ColumnInfo(name = COLUMN_FRAME_TYPE)
    val frameType: String?,

    @ColumnInfo(name = COLUMN_COOLING_METHOD)
    val coolingMethod: String?,

    @ColumnInfo(name = COLUMN_DRIVE_END)
    val driveEnd: String?,

    @ColumnInfo(name = COLUMN_NON_DRIVE_END)
    val nonDriveEnd: String?,

    @ColumnInfo(name = COLUMN_INSULATION_CLASS)
    val insulationClass: String?,

    @ColumnInfo(name = COLUMN_INSULATION_TEMP)
    val insulationTemp: Double?,

    @ColumnInfo(name = COLUMN_WEIGHT)
    val weight: Double?,

    @ColumnInfo(name = COLUMN_CREATED_AT)
    val createdAt: Instant,

    @ColumnInfo(name = COLUMN_MODIFIED_AT)
    val modifiedAt: Instant,
) {
    companion object {
        const val TABLE_NAME = "motor_specs"
        const val COLUMN_ID = "id"
        const val COLUMN_MACHINE_ID = "machine_id"
        const val COLUMN_EFF_CLASS = "eff_class"
        const val COLUMN_PHASES_NUMBER = "phases_number"
        const val COLUMN_NOMINAL_POWER = "nominal_power"
        const val COLUMN_FREQUENCY = "frequency"
        const val COLUMN_RPM = "rpm"
        const val COLUMN_RPM_RANGE = "rpm_range"
        const val COLUMN_NOMINAL_VOLTAGE = "nominal_voltage"
        const val COLUMN_NOMINAL_CURRENT = "nominal_current"
        const val COLUMN_SERVICE_FACTOR = "service_factor"
        const val COLUMN_POWER_FACTOR = "power_factor"
        const val COLUMN_DUTY = "duty"
        const val COLUMN_ROOM_TEMP = "room_temp"
        const val COLUMN_ENERGY_EFF = "energy_eff"
        const val COLUMN_MAX_ALTITUDE = "max_altitude"
        const val COLUMN_INGRESS_PROTECTION = "ingress_protection"
        const val COLUMN_MOUNTING_TYPE = "mounting_type"
        const val COLUMN_FRAME_TYPE = "frame_type"
        const val COLUMN_COOLING_METHOD = "cooling_method"
        const val COLUMN_DRIVE_END = "drive_end"
        const val COLUMN_NON_DRIVE_END = "non_drive_end"
        const val COLUMN_INSULATION_CLASS = "insulation_class"
        const val COLUMN_INSULATION_TEMP = "insulation_temp"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_MODIFIED_AT = "modified_at"
    }
}
