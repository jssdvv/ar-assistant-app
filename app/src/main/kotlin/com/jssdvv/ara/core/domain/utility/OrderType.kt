package com.jssdvv.ara.core.domain.utility

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
    @StringRes val orderTypeNameId: Int,
    @StringRes val orderTypeAlphabetNameId: Int,
    @StringRes val orderTypeDateNameId: Int,
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