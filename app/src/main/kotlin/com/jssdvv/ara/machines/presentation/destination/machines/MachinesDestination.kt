package com.jssdvv.ara.machines.presentation.destination.machines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheelScreen
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.type.MachineOrderKey
import com.jssdvv.ara.machines.presentation.destination.machines.component.MachineCard
import com.jssdvv.ara.machines.presentation.destination.machines.component.MachinesOrderSection
import com.jssdvv.ara.machines.presentation.destination.machines.component.MachinesSearchBar

/**
 * Entry point for the Machines screen in the navigation graph.
 *
 * This composable sets up the [MachinesScreen] and provides it with the necessary state and event
 * handlers.
 */
@Composable
fun MachinesDestination(
    onNavigateToSpecs: (Int) -> Unit,
    viewModel: MachineryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MachinesScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToSpecs = onNavigateToSpecs
    )
}

/**
 * Displays the Machines screen, including a search bar with ordering controls and a floating
 * action button.
 *
 * This composable handles the rendering of different UI states, such as loading and success, and
 * manages interactions such as ordering machine and navigating between screens.
 */
@Composable
internal fun MachinesScreen(
    modifier: Modifier = Modifier,
    uiState: MachinesUiState,
    onEvent: (MachinesEvent) -> Unit,
    onNavigateToSpecs: (Int) -> Unit,
) {
    val textFieldState = rememberTextFieldState()
    val successUiState = uiState as? MachinesUiState.Success

    Scaffold(
        modifier = modifier.windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            MachinesSearchBar(
                orderKey = successUiState?.orderKey ?: MachineOrderKey.NAME,
                orderType = successUiState?.orderType ?: OrderType.ASCENDING,
                textFieldState = textFieldState,
                onSearch = { onEvent(MachinesEvent.SearchMachines(it)) },
                searchResults = successUiState?.searchedMachines ?: emptyList(),
                onOrderMachines = { orderKey, orderType ->
                    onEvent(MachinesEvent.OrderMachines(orderKey, orderType))
                },
                onNavigateToMachineDetails = onNavigateToSpecs
            )
        },
        floatingActionButton = {}
    ) { paddingValues ->
        when (uiState) {
            MachinesUiState.Loading -> LoadingWheelScreen()

            is MachinesUiState.Success -> {
                MachinesContent(
                    modifier = Modifier.padding(paddingValues),
                    machines = uiState.machines,
                    orderType = uiState.orderType,
                    orderKey = uiState.orderKey,
                    onOrderMachines = { orderKey, orderType ->
                        onEvent(MachinesEvent.OrderMachines(orderKey, orderType))
                    },
                    onNavigateToMachineDetails = onNavigateToSpecs
                )
            }
        }
    }
}

@Composable
internal fun MachinesContent(
    modifier: Modifier = Modifier,
    machines: List<Machine>,
    orderType: OrderType,
    orderKey: MachineOrderKey,
    onOrderMachines: (MachineOrderKey, OrderType) -> Unit,
    onNavigateToMachineDetails: (Int) -> Unit,
) {
    var selectedMachineId by remember { mutableStateOf<Int?>(null) }
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        MachinesOrderSection(
            orderType = orderType,
            orderKey = orderKey,
            onOrderMachines = onOrderMachines
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.spacing.medium),
            contentPadding = PaddingValues(vertical = MaterialTheme.spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            items(
                items = machines,
                key = { it.id }
            ) { machine ->
                MachineCard(
                    machine = machine,
                    onClick = { selectedMachineId = machine.id },
                    isSelected = selectedMachineId == machine.id,
                    onNavigateToDetails = { onNavigateToMachineDetails(it) },
                )
            }
        }
    }
}