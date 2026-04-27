package com.jssdvv.ara.core.presentation.foundation.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.core.presentation.common.component.ArrowDropDownIcon
import com.jssdvv.ara.core.presentation.common.component.CheckIcon
import com.jssdvv.ara.core.presentation.common.component.CloseIcon
import com.jssdvv.ara.core.presentation.common.component.SearchIcon
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.type.OrderKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.Companion,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = { SearchIcon() },
    trailingIcon: @Composable (() -> Unit)? = if (value.isNotBlank()) {
        {
            IconButton(
                onClick = { onValueChange("") },
                content = { CloseIcon() }
            )
        }
    } else null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = remember { MutableInteractionSource() },
    navigationIcon: @Composable (() -> Unit)? = null,
    actionIcon: @Composable (() -> Unit)? = null,
    bottomRow: @Composable (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    LaunchedEffect(interactionSource) {
        interactionSource?.interactions?.collect { interaction ->
            when (interaction) {
                is FocusInteraction.Focus -> expanded = true
                is FocusInteraction.Unfocus -> expanded = false
            }
        }
    }

    LaunchedEffect(keyboardVisible) { if (!keyboardVisible) focusManager.clearFocus() }
    BackHandler(expanded) { focusManager.clearFocus() }

    Surface(
        modifier = modifier.pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
    ) {
        Column(
            modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.extraSmall)
                    .requiredHeight(TopAppBarDefaults.TopAppBarExpandedHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = !expanded && navigationIcon != null,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Box(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        content = { navigationIcon?.invoke() }
                    )
                }

                SimpleSearchBar(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    textStyle = textStyle,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    interactionSource = interactionSource
                )

                AnimatedVisibility(
                    visible = !expanded && navigationIcon != null,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Box(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        content = { actionIcon?.invoke() }
                    )
                }
            }
            bottomRow?.invoke()
        }
    }
}

@Composable
fun OrderSection(
    modifier: Modifier = Modifier,
    orderType: OrderType,
    orderKey: OrderKey,
    orderKeys: List<OrderKey>,
    onChangeSorting: (orderKey: OrderKey, OrderType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180F else 0F)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(stringResource(orderKey.orderKeyNameId))
        Box {
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
                        text = { Text(stringResource(key.orderKeyNameId)) },
                        onClick = {
                            onChangeSorting(key, orderType)
                            expanded = false
                        },
                        leadingIcon = { if (orderKey == key) CheckIcon() }
                    )
                }
                HorizontalDivider()
                listOf(
                    OrderType.ASCENDING to orderKey.orderTypeAscendingNameId,
                    OrderType.DESCENDING to orderKey.orderTypeDescendingNameId
                ).forEach { (type, typeNameId) ->
                    DropdownMenuItem(
                        text = { Text(stringResource(typeNameId)) },
                        onClick = {
                            onChangeSorting(orderKey, type)
                            expanded = false
                        },
                        leadingIcon = { if (orderType == type) CheckIcon() }
                    )
                }
            }
        }
    }
}