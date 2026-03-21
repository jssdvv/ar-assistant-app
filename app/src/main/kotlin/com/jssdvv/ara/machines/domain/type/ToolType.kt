package com.jssdvv.ara.machines.domain.type

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jssdvv.ara.R

enum class ToolType(
    @param:StringRes val labelResId: Int,
    @param:DrawableRes val iconResId: Int,
    @param:StringRes val iconContentDescResId: Int,
) {
    WRENCH(
        labelResId = R.string.tool_type_wrenches_label,
        iconResId = R.drawable.ic_wrench,
        iconContentDescResId = R.string.icon_tool_type_wrench_content_desc
    ),

    SCREWDRIVER(
        labelResId = R.string.tool_type_screw_drivers_label,
        iconResId = R.drawable.ic_screwdriver,
        iconContentDescResId = R.string.icon_tool_type_screw_driver_content_desc
    ),

    PLIERS(
        labelResId = R.string.tool_type_pliers_label,
        iconResId = R.drawable.ic_pliers,
        iconContentDescResId = R.string.icon_tool_type_pliers_content_desc
    ),

    HAMMER(
        labelResId = R.string.tool_type_hammers_label,
        iconResId = R.drawable.ic_hammer,
        iconContentDescResId = R.string.icon_tool_type_hammer_content_desc
    ),

    SAW(
        labelResId = R.string.tool_type_saw_label,
        iconResId = R.drawable.ic_saw,
        iconContentDescResId = R.string.icon_tool_type_saw_content_desc
    ),

    MEASURING(
        labelResId = R.string.tool_type_measuring_label,
        iconResId = R.drawable.ic_measuring_tape,
        iconContentDescResId = R.string.icon_tool_type_measuring_content_desc
    ),

    OTHER(
        labelResId = R.string.tool_type_other_label,
        iconResId = R.drawable.ic_toolbox,
        iconContentDescResId = R.string.icon_tool_type_other_content_desc
    )
}