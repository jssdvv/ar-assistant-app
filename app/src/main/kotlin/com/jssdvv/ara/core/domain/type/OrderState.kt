package com.jssdvv.ara.core.domain.type

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.jssdvv.ara.R

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
    );

    val queryString: String
        get() = if (this == ASCENDING) "ASC" else "DESC"
}

@Immutable
data class OrderState(
    val key: OrderKey = OrderKey.NAME,
    val type: OrderType = OrderType.ASCENDING
)