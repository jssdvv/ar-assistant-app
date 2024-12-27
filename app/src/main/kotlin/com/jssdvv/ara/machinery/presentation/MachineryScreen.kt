package com.jssdvv.ara.machinery.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jssdvv.ara.R
import com.jssdvv.ara.machinery.domain.utility.MachineOrderKey
import com.jssdvv.ara.machinery.presentation.component.MachineCard
import com.jssdvv.ara.machinery.presentation.component.MachinesListFAB
import com.jssdvv.ara.machinery.presentation.component.MachinesListOrderSection

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MachinesListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddMachine: () -> Unit,
    onNavigateToEditMachine: (Int) -> Unit,
    onNavigateToActivitiesList: (Int) -> Unit,
) {
    val viewModel = hiltViewModel<MachineryViewModel>()
    val state by remember { viewModel.state }.collectAsState()
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        floatingActionButton = {
            MachinesListFAB(
                onNavigateToAddMachine = onNavigateToAddMachine
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            FilteringRow(
                Modifier.fillMaxWidth(),
                orderKey = state.machineOrderKey,
                onOrderKeyChange = { orderKey ->
                    viewModel.onEvent(MachineryEvent.OrderMachines(orderKey))
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.machines) { machineEntity ->
                    MachineCard(
                        modifier = Modifier.fillMaxWidth(),
                        entity = machineEntity,
                        onNavigateToActivitiesList = onNavigateToActivitiesList,
                        onNavigateToEditMachine = onNavigateToEditMachine
                    )
                }
            }
        }
    }
}

@Composable
fun FilteringRow(
    modifier: Modifier = Modifier,
    orderKey: MachineOrderKey,
    onOrderKeyChange: (MachineOrderKey) -> Unit,
) {

    var selectedChip by rememberSaveable { mutableStateOf(false) }
    var isOrderSectionVisible by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = selectedChip,
            onClick = { selectedChip = !selectedChip },
            label = {
                Text(text = stringResource(R.string.categories_chip_all_options_label))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(
                        if (selectedChip) {
                            R.drawable.machines_filled
                        } else {
                            R.drawable.machines_outlined
                        }
                    ),
                    contentDescription = stringResource(R.string.categories_chip_leading_icon_content_description),
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = if (selectedChip) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = stringResource(R.string.categories_chip_trailing_icon_content_description),
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )
        IconButton(
            onClick = {
                isOrderSectionVisible = !isOrderSectionVisible
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.filter_outlined),
                contentDescription = stringResource(R.string.icon_filter_button_content_description)
            )
        }
    }
    AnimatedVisibility(
        visible = isOrderSectionVisible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        MachinesListOrderSection(
            modifier = Modifier.fillMaxWidth(),
            orderKey = orderKey,
            onOrderKeyChange = onOrderKeyChange,
        )
    }
}
