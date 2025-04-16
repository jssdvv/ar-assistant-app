package com.jssdvv.ara.core.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jssdvv.ara.R
import com.jssdvv.ara.inventory.presentation.navigation.InventoryGraphRoute
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraphRoute
import com.jssdvv.ara.scanner.presentation.navigation.ScannerGraphRoute
import kotlin.reflect.KClass

/**
 * A enum class representing the navigation graph items of the app.
 *
 * @property [selectedIconId] The resource id of the icon to be used when the item is selected.
 * @property [unselectedIconId] The resource id of the icon to be used when the item is not selected.
 * @property [labelTextId] The string resource id of the label text to be used for this item.
 * @property [iconContentDescId] The string resource id of the icon's content description.
 * @property [route] The [KClass] `Serializable` data class or data object route of this item.
 */
enum class AppNavGraphItem(
    @DrawableRes val selectedIconId: Int,
    @DrawableRes val unselectedIconId: Int,
    @StringRes val labelTextId: Int,
    @StringRes val iconContentDescId: Int,
    val route: KClass<*>,
) {
    SCANNER(
        selectedIconId = R.drawable.ic_scanner_filled,
        unselectedIconId = R.drawable.ic_scanner_outlined,
        labelTextId = R.string.graph_scanner_label,
        iconContentDescId = R.string.graph_icon_scanner_content_desc,
        route = ScannerGraphRoute::class
    ),
    MACHINES(
        selectedIconId = R.drawable.ic_machines_filled,
        unselectedIconId = R.drawable.ic_machines_outlined,
        labelTextId = R.string.graph_machines_label,
        iconContentDescId = R.string.graph_icon_machines_content_desc,
        route = MachinesGraphRoute::class
    ),
    INVENTORY(
        selectedIconId = R.drawable.ic_inventory_filled,
        unselectedIconId = R.drawable.ic_inventory_outlined,
        labelTextId = R.string.graph_inventory_label,
        iconContentDescId = R.string.graph_icon_inventory_content_desc,
        route = InventoryGraphRoute::class
    )
}