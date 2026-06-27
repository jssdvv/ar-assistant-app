package com.jssdvv.ara.machines.presentation.destination.activities.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.NavigationUpIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesListTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(stringResource(R.string.top_bar_activities_title)) },
        navigationIcon = { NavigationUpIconButton(onBackClick) }
    )
}