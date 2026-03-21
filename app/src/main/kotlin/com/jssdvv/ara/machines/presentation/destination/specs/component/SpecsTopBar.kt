package com.jssdvv.ara.machines.presentation.destination.specs.component

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jssdvv.ara.core.presentation.common.NavigationUpIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecsTopBar(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    isTitleShown: Boolean = false,
    title: String
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { if (isTitleShown) Text(text = title) },
        navigationIcon = { NavigationUpIconButton(onNavigateBack) },
    )
}