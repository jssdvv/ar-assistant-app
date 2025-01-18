package com.jssdvv.ara.machinery.presentation.activities

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jssdvv.ara.core.domain.model.Activity
import com.jssdvv.ara.machinery.presentation.camera.activities_list.components.ActivitiesListFAB
import com.jssdvv.ara.machinery.presentation.camera.activities_list.components.ActivitiesListTopBar
import com.jssdvv.ara.machinery.presentation.camera.activities_list.components.ActivityCard

@Composable
fun ActivitiesScreen(
    machineId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToAddActivity: () -> Unit,
    onNavigateToEditActivity: (Int) -> Unit,
    onNavigateToARCamera: (Int) -> Unit,
) {
    val viewModel = hiltViewModel<ActivitiesViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.onEvent(
            ActivitiesUiEvent.GetMachineId(machineId)
        )
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ActivitiesListTopBar(
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            if (uiState is ActivitiesUiState.Success) {
                ActivitiesListFAB(
                    onNavigateToAddActivity = onNavigateToAddActivity
                )
            }
        }
    ) { paddingValues ->
        when (uiState) {
            ActivitiesUiState.Loading -> {

            }

            is ActivitiesUiState.Success -> {
                val uiStateSuccess = uiState as ActivitiesUiState.Success
                ActivitiesContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    activities = uiStateSuccess.activities,
                    onNavigateToARCamera = onNavigateToARCamera
                )
            }
        }
    }
}

@Composable
fun ActivitiesContent(
    modifier: Modifier = Modifier,
    activities: List<Activity>,
    onNavigateToARCamera: (Int) -> Unit,
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(activities) { activity ->
            ActivityCard(
                modifier = Modifier.fillParentMaxWidth(),
                entity = activity,
                onNavigateToArCamera = { onNavigateToARCamera(activity.activityId) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}