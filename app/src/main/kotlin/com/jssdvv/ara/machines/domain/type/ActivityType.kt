package com.jssdvv.ara.machines.domain.type

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jssdvv.ara.R

/**
 * Enum class representing different types of maintenance activities.
 *
 * @property [labelResId] String resource ID for the activity type label.
 */
enum class ActivityType(
    @param:DrawableRes val iconResId: Int,
    @param:StringRes val iconContentDescResId: Int,
    @param:StringRes val labelResId: Int,
) {
    /**
     * **General** activity that encompasses multiple types of maintenance or
     * represents a complete procedure involving various stages.
     *
     * **Examples:** Full machine overhaul, routine multi-point service,
     * global system maintenance.
     */
    GENERAL(
        iconResId = R.drawable.ic_other,
        iconContentDescResId = R.string.icon_activity_type_general_content_desc,
        labelResId = R.string.activity_type_general_label
    ),

    /**
     * **Calibration** to ensure accuracy and precision of machine components.
     *
     * **Examples:** Adjusting sensors, aligning tools, verifying measurements.
     */
    CALIBRATION(
        iconResId = R.drawable.ic_other,
        iconContentDescResId = R.string.icon_activity_type_calibration_content_desc,
        labelResId = R.string.activity_type_calibration_label
    ),

    /**
     * **Inspection** to assess the condition of machines, components, or systems.
     *
     * **Examples:** Visual checks, sensor readings, wear analysis.
     */
    INSPECTION(
        iconResId = R.drawable.ic_other,
        iconContentDescResId = R.string.icon_activity_type_inspection_content_desc,
        labelResId = R.string.activity_type_inspection_label
    ),

    /**
     * **Installation** of new components, parts, or entire machines.
     *
     * **Examples:** Setting up new motors, configuring automation systems.
     */
    INSTALLATION(
        iconResId = R.drawable.ic_other,
        iconContentDescResId = R.string.icon_activity_type_installation_content_desc,
        labelResId = R.string.activity_type_installation_label
    ),

    /**
     * **Restoration** of faulty or damaged components to functional condition.
     *
     * **Examples:** Repairing broken parts, realigning misaligned elements, cleaning parts.
     */
    RESTORATION(
        iconResId = R.drawable.ic_other,
        iconContentDescResId = R.string.icon_activity_type_restoration_content_desc,
        labelResId = R.string.activity_type_restoration_label
    ),

    /**
     * **Replacement** of components or systems from the machine.
     *
     * **Examples:** Dismantling motors, removing obsolete parts.
     */
    REPLACEMENT(
        iconResId = R.drawable.ic_other,
        iconContentDescResId = R.string.icon_activity_type_replacement_content_desc,
        labelResId = R.string.activity_type_replacement_label
    ),

    /**
     * **Miscellaneous** activities that do not fit predefined categories.
     *
     * **Examples:** Custom modifications, experimental procedures.
     */
    OTHER(
        iconResId = R.drawable.ic_other,
        iconContentDescResId = R.string.icon_activity_type_other_content_desc,
        labelResId = R.string.activity_type_other_label
    )
}