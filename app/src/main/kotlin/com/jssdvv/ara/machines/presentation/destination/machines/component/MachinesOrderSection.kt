package com.jssdvv.ara.machines.presentation.destination.machines.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.core.presentation.common.component.ArrowDropDownIcon
import com.jssdvv.ara.machines.domain.type.MachineOrderKey

@Composable
fun MachinesOrderSection(
    modifier: Modifier = Modifier,
    orderType: OrderType,
    orderKey: MachineOrderKey,
    onOrderMachines: (MachineOrderKey, OrderType) -> Unit,
) {
    val orderTypes = OrderType.entries
    val orderKeys = MachineOrderKey.entries

    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180F else 0F
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(stringResource(orderKey.orderKeyName))
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                modifier = Modifier.rotate(rotation),
                onClick = { expanded = !expanded }
            ) {
                ArrowDropDownIcon(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.inverseOnSurface),
                    contentDescription =
                        if (expanded) {
                            stringResource(R.string.icon_arrow_down_content_desc)
                        } else {
                            stringResource(R.string.icon_arrow_down_opposite_content_desc)
                        }
                )
            }
            DropdownMenu(
                modifier = Modifier.padding(16.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                Text(stringResource(R.string.order_type_sort_action))
                orderKeys.forEach { key ->
                    DropdownMenuItem(
                        text = { Text(stringResource(key.orderKeyName)) },
                        onClick = {
                            onOrderMachines(key, orderType)
                            expanded = false
                        },
                        leadingIcon = {
                            if (orderKey == key) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_check),
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
                HorizontalDivider()
                orderTypes.forEach { type ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(
                                    when (orderKey) {
                                        MachineOrderKey.CODE,
                                            -> type.orderTypeNameId

                                        MachineOrderKey.NAME,
                                        MachineOrderKey.TYPE,
                                            -> type.orderTypeAlphabetNameId

                                        MachineOrderKey.CREATION_DATE,
                                        MachineOrderKey.MODIFICATION_DATE,
                                            -> type.orderTypeDateNameId
                                    }
                                )
                            )
                        },
                        onClick = {
                            onOrderMachines(orderKey, type)
                            expanded = false
                        },
                        leadingIcon = {
                            if (orderType == type) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_check),
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}