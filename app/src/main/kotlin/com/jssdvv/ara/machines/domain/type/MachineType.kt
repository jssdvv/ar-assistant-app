package com.jssdvv.ara.machines.domain.type

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jssdvv.ara.R

/**
 * Enum class representing different types of machines, based on the energy they receive.
 *
 * @property [iconResId] Drawable resource ID for the unselected state icon.
 * @property [iconContentDescResId] String resource ID for the icon content description.
 * @property [labelResId] String resource ID for the machine type label.
 */
enum class MachineType(
    @param:DrawableRes val iconResId: Int,
    @param:StringRes val iconContentDescResId: Int,
    @param:StringRes val labelResId: Int
) {
    /**
     * Machines that receive **electrical energy** and convert it into mechanical motion, heat, or
     * another form of energy.
     *
     * **Examples:** Electric motors, induction heaters, welding machine.
     */
    ELECTRICAL(
        iconResId = R.drawable.ic_electrical,
        iconContentDescResId = R.string.icon_electrical_content_desc,
        labelResId = R.string.machine_type_electrical_label
    ),

    /**
     * Machines that receive **hydraulic energy** from pressurized liquids and convert it into
     * mechanical work or force amplification.
     *
     * **Examples:** Hydraulic presses, excavators, injection molding machine.
     */
    HYDRAULIC(
        iconResId = R.drawable.ic_hydraulic,
        iconContentDescResId = R.string.icon_hydraulic_content_desc,
        labelResId = R.string.machine_type_hydraulic_label
    ),

    /**
     * Machines that receive **mechanical energy** from gears, levers, or stored kinetic energy and
     * converts it into motion or force.
     *
     * **Examples:** Gear systems, crankshafts, conveyor belts.
     */
    MECHANICAL(
        iconResId = R.drawable.ic_mechanical,
        iconContentDescResId = R.string.icon_mechanical_content_desc,
        labelResId = R.string.machine_type_mechanical_label
    ),

    /**
     * Machines that receive **pneumatic energy** from compressed air or gas and convert it into
     * mechanical motion or force.
     *
     * **Examples:** Pneumatic drills, air brakes, automation actuators.
     */
    PNEUMATIC(
        iconResId = R.drawable.ic_pneumatic,
        iconContentDescResId = R.string.icon_pneumatic_content_desc,
        labelResId = R.string.machine_type_pneumatic_label
    ),

    /**
     * Machines that receive **thermal energy** from heat sources and convert it into mechanical
     * work or another form of energy.
     *
     * **Examples:** Boilers, steam turbines, internal combustion engines.
     */
    THERMAL(
        iconResId = R.drawable.ic_thermal,
        iconContentDescResId = R.string.icon_thermal_content_desc,
        labelResId = R.string.machine_type_thermal_label
    ),

    /**
     * Machines that their energy sources are **unknown** or **not identified**.
     */
    UNKNOWN(
        iconResId = R.drawable.ic_unknown,
        iconContentDescResId = R.string.icon_unknown_content_desc,
        labelResId = R.string.machine_type_unknown_label
    )
}