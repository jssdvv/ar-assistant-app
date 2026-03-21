package com.jssdvv.ara.core.domain.type

import androidx.annotation.StringRes
import com.jssdvv.ara.R

/**
 * Enum representing the different types of ordering available for a list of items.
 *
 * @property [orderTypeNameId] The name of the order type.
 * @property [orderTypeAlphabetNameId] The name of the order type for alphabetical ordering.
 * @property [orderTypeDateNameId] The name of the order type for date ordering.
 */
enum class OrderType(
    @param:StringRes val orderTypeNameId: Int,
    @param:StringRes val orderTypeAlphabetNameId: Int,
    @param:StringRes val orderTypeDateNameId: Int,
) {
    ASCENDING(
        orderTypeNameId = R.string.order_type_ascending_label,
        orderTypeAlphabetNameId = R.string.order_type_ascending_alphabet_label,
        orderTypeDateNameId = R.string.order_type_ascending_date_label
    ),
    DESCENDING(
        orderTypeNameId = R.string.order_type_descending_label,
        orderTypeAlphabetNameId = R.string.order_type_descending_alphabet_label,
        orderTypeDateNameId = R.string.order_type_descending_date_label
    )
}

fun getOrderTypeAsString(
    orderType: OrderType,
) = when (orderType) {
    OrderType.ASCENDING -> "ASC"
    OrderType.DESCENDING -> "DESC"
}