package com.jssdvv.ara.machines.domain.type

import androidx.annotation.StringRes
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.type.OrderType

enum class OrderKey(
    @param:StringRes val orderKeyNameId: Int,
    @param:StringRes val orderTypeAscendingNameId: Int,
    @param:StringRes val orderTypeDescendingNameId: Int
) {
    CODE(
        orderKeyNameId = R.string.machine_order_key_code_label,
        orderTypeAscendingNameId = OrderType.ASCENDING.orderTypeNameId,
        orderTypeDescendingNameId = OrderType.DESCENDING.orderTypeNameId
    ),
    NAME(
        orderKeyNameId = R.string.machine_order_key_name_label,
        orderTypeAscendingNameId = OrderType.ASCENDING.orderTypeAlphabetNameId,
        orderTypeDescendingNameId = OrderType.DESCENDING.orderTypeAlphabetNameId
    ),
    TYPE(
        orderKeyNameId = R.string.machine_order_key_type_label,
        orderTypeAscendingNameId = OrderType.ASCENDING.orderTypeAlphabetNameId,
        orderTypeDescendingNameId = OrderType.DESCENDING.orderTypeAlphabetNameId
    ),
    CREATION_DATE(
        orderKeyNameId = R.string.machine_order_key_creation_date_label,
        orderTypeAscendingNameId = OrderType.ASCENDING.orderTypeDateNameId,
        orderTypeDescendingNameId = OrderType.DESCENDING.orderTypeDateNameId
    ),
    MODIFICATION_DATE(
        orderKeyNameId = R.string.machine_order_key_modification_date_label,
        orderTypeAscendingNameId = OrderType.ASCENDING.orderTypeDateNameId,
        orderTypeDescendingNameId = OrderType.DESCENDING.orderTypeDateNameId
    )
}