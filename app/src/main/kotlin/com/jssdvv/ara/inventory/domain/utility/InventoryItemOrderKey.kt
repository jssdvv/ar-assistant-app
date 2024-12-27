package com.jssdvv.ara.inventory.domain.utility

import com.jssdvv.ara.core.domain.utility.OrderType

sealed class InventoryItemOrderKey(
    val orderType: OrderType
) {
    class Id(orderType: OrderType): InventoryItemOrderKey(orderType)
    class Name(orderType: OrderType): InventoryItemOrderKey(orderType)
    class Timestamp(orderType: OrderType): InventoryItemOrderKey(orderType)
}