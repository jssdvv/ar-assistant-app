package com.jssdvv.ara.machinery.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jssdvv.ara.machinery.presentation.screen.activities_list.components.ActivitiesListFAB
import com.jssdvv.ara.machinery.presentation.screen.activities_list.components.ActivitiesListTopBar
import com.jssdvv.ara.machinery.presentation.screen.activities_list.components.ActivityCard

@Composable
fun ActivitiesListScreen(
    machineId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToAddActivity: () -> Unit,
    onNavigateToEditActivity: (Int) -> Unit,
    onNavigateToARCamera: (Int) -> Unit,
    viewModel: ActivitiesListViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.collectAsState().value
    LaunchedEffect(true) {
        viewModel.onEvent(
            ActivitiesListEvent.GetMachineId(machineId)
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
            ActivitiesListFAB(
                onNavigateToAddActivity = onNavigateToAddActivity
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(uiState.activities) { activity ->
                ActivityCard(
                    modifier = Modifier.fillParentMaxWidth(),
                    entity = activity,
                    onNavigateToArCamera = { onNavigateToARCamera(activity.activityId) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
