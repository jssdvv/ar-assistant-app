package com.jssdvv.ara.machines.presentation.destination.machines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.type.OrderKey
import com.jssdvv.ara.core.domain.type.OrderState
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheelScreen
import com.jssdvv.ara.core.presentation.foundation.component.OrderSection
import com.jssdvv.ara.core.presentation.foundation.component.SearchBarSurface
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.presentation.destination.machines.component.MachineCard

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
    MachinesScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
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
    uiState: MachinesUiState,
    onEvent: (MachinesEvent) -> Unit,
    onNavigateToSpecs: (Int) -> Unit,
) {
    when (uiState) {
        MachinesUiState.Loading -> LoadingWheelScreen()

        is MachinesUiState.Success -> {
            MachinesContent(
                orderState = uiState.orderState,
                machines = uiState.machines,
                onEvent = onEvent,
                onNavigateToMachineDetails = onNavigateToSpecs
            )
        }
    }
}

@Composable
internal fun MachinesContent(
    orderState: OrderState,
    machines: List<Machine>,
    onEvent: (MachinesEvent) -> Unit,
    onNavigateToMachineDetails: (Int) -> Unit,
) {
    val textFieldState = rememberTextFieldState()
    var currentMachineId by remember { mutableIntStateOf(0) }
    SearchBarSurface(
        value = textFieldState.text.toString(),
        onValueChange = { textFieldState.setTextAndPlaceCursorAtEnd(it) },
        placeholder = { Text(stringResource(R.string.search_bar_machines_supporting_text)) },
        bottomRow = {
            OrderSection(
                orderState = orderState,
                usedOrderKeys = OrderKey.entries,
                onChangeOrder = { onEvent(MachinesEvent.OrderMachines(it)) }
            )
        }
    ) {
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
                    onClick = { currentMachineId = machine.id },
                    isSelected = currentMachineId == machine.id,
                    onNavigateToDetails = { onNavigateToMachineDetails(it) },
                )
            }
        }
    }
}