package com.jssdvv.ara.tools.presentation.destination.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.jssdvv.ara.core.domain.type.OrderKey
import com.jssdvv.ara.core.domain.type.OrderState
import com.jssdvv.ara.core.presentation.common.component.AddIcon
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheelScreen
import com.jssdvv.ara.core.presentation.foundation.component.OrderSection
import com.jssdvv.ara.core.presentation.foundation.component.SearchTopBar
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Tool
import com.jssdvv.ara.tools.presentation.destination.tools.component.CreateToolDialog
import com.jssdvv.ara.tools.presentation.destination.tools.component.ToolCard
import com.jssdvv.ara.tools.presentation.destination.tools.component.ToolEditDialog

@Composable
fun ToolsDestination(
    modifier: Modifier = Modifier,
    viewModel: ToolsViewModel = hiltViewModel()
) {
    ToolsScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
internal fun ToolsScreen(
    uiState: ToolsUiState,
    onEvent: (ToolsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        ToolsUiState.Loading -> LoadingWheelScreen()
        is ToolsUiState.Success -> ToolsContent(
            tools = uiState.tools,
            searchQuery = uiState.searchQuery,
            orderState = uiState.orderState,
            onEvent = onEvent,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsContent(
    tools: List<Tool>,
    searchQuery: String,
    orderState: OrderState,
    onEvent: (ToolsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingTool by remember { mutableStateOf<Tool?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SearchTopBar(
                value = searchQuery,
                onValueChange = { onEvent(ToolsEvent.OnSearchTools(it)) },
                placeholder = { Text(stringResource(R.string.search_bar_tools_supporting_text)) },
                bottomRow = {
                    OrderSection(
                        orderState = orderState,
                        usedOrderKeys = listOf(OrderKey.NAME, OrderKey.TYPE, OrderKey.CODE),
                        onChangeOrder = { onEvent(ToolsEvent.OnSortTools(it)) }
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { AddIcon() },
                text = { Text(stringResource(R.string.fab_tools_create_action)) }
            )
        }
    ) { paddingValues ->
        if (tools.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.screen_tools_empty_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = MaterialTheme.spacing.medium),
                contentPadding = PaddingValues(top = MaterialTheme.spacing.small, bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                items(
                    items = tools,
                    key = { it.id }
                ) { tool ->
                    ToolCard(
                        tool = tool,
                        onClick = { },
                        onEditClick = { editingTool = tool },
                        isSelected = editingTool == tool
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateToolDialog(
            onConfirm = { tool ->
                onEvent(ToolsEvent.OnCreateTool(tool))
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    editingTool?.let { tool ->
        ToolEditDialog(
            tool = tool,
            onSave = {
                onEvent(ToolsEvent.OnEditTool(it))
                editingTool = null
            },
            onDismiss = { editingTool = null }
        )
    }
}