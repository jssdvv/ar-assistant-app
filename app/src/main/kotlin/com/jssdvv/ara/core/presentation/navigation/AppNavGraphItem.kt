package com.jssdvv.ara.core.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.navigation.graph.InventoryNavGraph
import com.jssdvv.ara.core.presentation.navigation.graph.MachineryNavGraph
import com.jssdvv.ara.core.presentation.navigation.graph.ScannerNavGraph
import kotlin.reflect.KClass

enum class AppNavGraphItem(
    @DrawableRes val selectedIconId: Int,
    @DrawableRes val unselectedIconId: Int,
    @StringRes val labelTextId: Int,
    @StringRes val iconDescId: Int,
    val route: KClass<*>
) {
    SCANNER(
        selectedIconId = R.drawable.scanner_filled,
        unselectedIconId = R.drawable.scanner_outlined,
        labelTextId = R.string.scanner_label_text,
        iconDescId = R.string.scanner_icon_desc,
        route = ScannerNavGraph::class
    ),
    MACHINERY(
        selectedIconId = R.drawable.machines_filled,
        unselectedIconId = R.drawable.machines_outlined,
        labelTextId = R.string.machinery_label_text,
        iconDescId = R.string.machinery_icon_desc,
        route = MachineryNavGraph::class
    ),
    INVENTORY(
        selectedIconId = R.drawable.inventory_filled,
        unselectedIconId = R.drawable.inventory_outlined,
        labelTextId = R.string.inventory_label_text,
        iconDescId = R.string.inventory_icon_desc,
        route = InventoryNavGraph::class
    )
}