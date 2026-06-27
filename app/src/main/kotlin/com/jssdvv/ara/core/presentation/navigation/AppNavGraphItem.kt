package com.jssdvv.ara.core.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jssdvv.ara.R
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import com.jssdvv.ara.scanner.presentation.navigation.ScannerGraph
import com.jssdvv.ara.schedule.presentation.navigation.EventsGraph
import com.jssdvv.ara.tools.presentation.navigation.ToolsGraph
import kotlin.reflect.KClass

/**
 * An enum class representing the navigation graph items of the navigation host.
 *
 * @see [AppNavHost]
 *
 * @property [selectedIconId] The resource id of the icon to be used when the item is selected.
 * @property [unselectedIconId] The resource id of the icon to be used when the item is not selected.
 * @property [labelTextId] The string resource id of the label text to be used for this item.
 * @property [iconContentDescId] The string resource id of the icon's content description.
 * @property [route] The [KClass] `Serializable` data class or data object route of this item.
 */
enum class AppNavGraphItem(
    @param:DrawableRes val selectedIconId: Int,
    @param:DrawableRes val unselectedIconId: Int,
    @param:StringRes val labelTextId: Int,
    @param:StringRes val iconContentDescId: Int,
    val route: KClass<*>,
) {
    SCANNER(
        selectedIconId = R.drawable.ic_scanner_filled,
        unselectedIconId = R.drawable.ic_scanner_outlined,
        labelTextId = R.string.graph_scanner_label,
        iconContentDescId = R.string.icon_scanner_content_desc,
        route = ScannerGraph::class
    ),
    MACHINES(
        selectedIconId = R.drawable.ic_machines_filled,
        unselectedIconId = R.drawable.ic_machines_outlined,
        labelTextId = R.string.graph_machines_label,
        iconContentDescId = R.string.icon_machines_content_desc,
        route = MachinesGraph::class
    ),
    TOOLS(
        selectedIconId = R.drawable.ic_tools_filled,
        unselectedIconId = R.drawable.ic_tools_outlined,
        labelTextId = R.string.graph_tools_label,
        iconContentDescId = R.string.icon_tools_content_desc,
        route = ToolsGraph::class
    ),
    EVENTS(
        selectedIconId = R.drawable.ic_events_filled,
        unselectedIconId = R.drawable.ic_events_outlined,
        labelTextId = R.string.graph_events_label,
        iconContentDescId = R.string.icon_events_content_desc,
        route = EventsGraph::class
    )
}