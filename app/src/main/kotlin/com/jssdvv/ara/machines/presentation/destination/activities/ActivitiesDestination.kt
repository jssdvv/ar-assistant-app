package com.jssdvv.ara.machines.presentation.destination.activities

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.AddIcon
import com.jssdvv.ara.core.presentation.foundation.component.CounterButton
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheelScreen
import com.jssdvv.ara.core.presentation.navigation.CalibrationIcon
import com.jssdvv.ara.core.presentation.navigation.MarkerIcon
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Activity
import com.jssdvv.ara.machines.presentation.destination.activities.components.ActivitiesListTopBar
import com.jssdvv.ara.machines.presentation.destination.activities.components.ActivityCard
import com.jssdvv.ara.machines.presentation.destination.activities.components.CreateActivityDialog

@Composable
fun ActivitiesDestination(
    onNavigateBack: () -> Unit,
    onNavigateToMarkers: (machineId: Int) -> Unit,
    onNavigateToCalibration: (machineId: Int) -> Unit,
    onNavigateToARSession: (Int, Int) -> Unit,
    onNavigateToAnimations: (Int, Int) -> Unit,
    viewModel: ActivitiesViewModel = hiltViewModel(),
) {
    ActivitiesScreen(
        machineId = viewModel.machineId,
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToMarkers = onNavigateToMarkers,
        onNavigateToModels = onNavigateToCalibration,
        onNavigateToARCamera = onNavigateToARSession,
        onNavigateToEditActivity = onNavigateToAnimations
    )
}

@Composable
fun ActivitiesScreen(
    modifier: Modifier = Modifier,
    machineId: Int,
    uiState: ActivitiesUiState,
    onNavigateBack: () -> Unit,
    onNavigateToMarkers: (Int) -> Unit,
    onNavigateToModels: (Int) -> Unit,
    onNavigateToARCamera: (Int, Int) -> Unit,
    onNavigateToEditActivity: (Int, Int) -> Unit,
    onEvent: (ActivitiesEvent) -> Unit,
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ActivitiesListTopBar(
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { AddIcon() },
                text = { Text(stringResource(R.string.fab_activities_create_action)) }
            )
        }
    ) { paddingValues ->
        when (uiState) {
            ActivitiesUiState.Loading -> LoadingWheelScreen()

            is ActivitiesUiState.Success -> {
                ActivitiesContent(
                    modifier = modifier.padding(paddingValues),
                    machineId = machineId,
                    activities = uiState.activities,
                    counters = uiState.counters,
                    onNavigateToMarkers = { onNavigateToMarkers(machineId) },
                    onNavigateToCalibration = { onNavigateToModels(machineId) },
                    onNavigateToARSession = onNavigateToARCamera,
                    onNavigateToAnimations = onNavigateToEditActivity
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateActivityDialog(
            machineId = machineId,
            onConfirm = { activity ->
                onEvent(ActivitiesEvent.OnCreateActivity(activity))
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivitiesContent(
    machineId: Int,
    activities: List<Activity>,
    counters: ActivitiesMiniButtonsCounters,
    onNavigateToMarkers: () -> Unit,
    onNavigateToCalibration: () -> Unit,
    onNavigateToAnimations: (Int, Int) -> Unit,
    onNavigateToARSession: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedActivityId by remember { mutableStateOf<Int?>(null) }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            CounterButton(
                onClick = onNavigateToMarkers,
                title = stringResource(R.string.counter_button_markers_label),
                count = counters.markersCount,
                icon = { MarkerIcon() },
            )
            CounterButton(
                onClick = onNavigateToCalibration,
                title = stringResource(R.string.counter_button_calibration_label),
                count = counters.markersCalibratedCount,
                icon = { CalibrationIcon() }
            )
        }

        // Activity Cards
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(horizontal = MaterialTheme.spacing.medium),
            contentPadding = PaddingValues(top = MaterialTheme.spacing.small, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            items(
                items = activities,
                key = { it.id }
            ) { activity ->
                ActivityCard(
                    activity = activity,
                    onClick = { selectedActivityId = activity.id },
                    isSelected = selectedActivityId == activity.id,
                    onNavigateToAnimations = { onNavigateToAnimations(machineId, activity.id) },
                    onNavigateToARSession = { onNavigateToARSession(machineId, activity.id) },
                )
            }
        }
    }
}