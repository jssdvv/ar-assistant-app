package com.jssdvv.ara.machines.domain.type

import androidx.annotation.StringRes
import com.jssdvv.ara.R

enum class MachineOrderKey(
    @param:StringRes val orderKeyName: Int,
) {
    CODE(
        orderKeyName = R.string.machine_order_key_code_label
    ),
    NAME(
        orderKeyName = R.string.machine_order_key_name_label
    ),
    TYPE(
        orderKeyName = R.string.machine_order_key_type_label
    ),
    CREATION_DATE(
        orderKeyName = R.string.machine_order_key_creation_date_label
    ),
    MODIFICATION_DATE(
        orderKeyName = R.string.machine_order_key_modification_date_label
    )
}