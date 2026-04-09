package com.jssdvv.ara.machines.domain.type

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jssdvv.ara.R

/**
 * Represents the mechanical constraint and kinematic behavior of a submodel animation.
 * These types define how the object moves between its initial and final states.
 */
enum class OperationType(
    @param:DrawableRes val iconResId: Int,
    @param:StringRes val iconContentDescResId: Int,
    @param:StringRes val labelResId: Int
) {
    /**
     * Standard point-to-point movement. The object moves freely in 3D space
     * with 3 degrees of freedom for translation and rotation (LERP).
     */
    POINT_TO_POINT(
        iconResId = R.drawable.ic_operation_type_point_to_point,
        iconContentDescResId = R.string.icon_operation_type_free_content_desc,
        labelResId = R.string.operation_type_free_label
    ),

    /**
     * Coupled translation and rotation. The object rotates as it advances
     * along an axis based on a defined thread pitch.
     * Uses: Bolts, screws, and threaded caps.
     */
    SCREW(
        iconResId = R.drawable.ic_operation_type_screw,
        iconContentDescResId = R.string.icon_operation_type_screw_content_desc,
        labelResId = R.string.operation_type_screw_label
    ),

    /**
     * Independent translation and rotation along a single common axis.
     * Provides 2 degrees of freedom (1 translation + 1 rotation).
     * Uses: A round pin in a hole or a telescopic pole that can also spin.
     */
    CYLINDRICAL(
        iconResId = R.drawable.ic_operation_type_cylindrical,
        iconContentDescResId = R.string.icon_operation_type_cylindrical_content_desc,
        labelResId = R.string.operation_type_cylindrical_label
    ),

    /**
     * Rotation around a fixed point in any direction.
     * Provides 3 degrees of rotational freedom.
     * Uses: Ball joints, joysticks, or sockets.
     */
    JOINT(
        iconResId = R.drawable.ic_operation_type_joint,
        iconContentDescResId = R.string.icon_operation_type_joint_content_desc,
        labelResId = R.string.operation_type_joint_label
    )
}