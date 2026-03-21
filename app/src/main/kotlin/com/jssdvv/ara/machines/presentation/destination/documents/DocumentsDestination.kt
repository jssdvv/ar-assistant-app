package com.jssdvv.ara.machines.presentation.destination.documents

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun DocumentsDestination(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    viewModel: DocumentsViewModel = hiltViewModel()
) {
    DocumentsScreen()
}

@Composable
internal fun DocumentsScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {

    }
}