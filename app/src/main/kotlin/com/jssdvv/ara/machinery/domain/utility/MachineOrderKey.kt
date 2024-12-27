package com.jssdvv.ara.machinery.domain.utility

import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.utility.OrderType

sealed class MachineOrderKey(
    val orderType: OrderType,
    val orderKeyName: Int
) {
    class Name(
        orderType: OrderType,
        orderKeyName: Int = R.string.machine_name_order_key_name
    ) : MachineOrderKey(orderType, orderKeyName)

    class Category(
        orderType: OrderType,
        orderKeyName: Int = R.string.machine_category_order_key_name
    ) : MachineOrderKey(orderType, orderKeyName)

    class Timestamp(
        orderType: OrderType,
        orderKeyName: Int = R.string.machine_timestamp_order_key_name
    ) : MachineOrderKey(orderType, orderKeyName)

    fun copy(orderType: OrderType): MachineOrderKey {
        return when (this) {
            is Name -> Name(orderType)
            is Category -> Category(orderType)
            is Timestamp -> Timestamp(orderType)
        }
    }
}