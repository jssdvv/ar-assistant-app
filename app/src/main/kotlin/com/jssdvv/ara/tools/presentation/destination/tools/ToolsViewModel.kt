package com.jssdvv.ara.tools.presentation.destination.tools

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jssdvv.ara.core.domain.type.OrderKey
import com.jssdvv.ara.core.domain.type.OrderState
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.domain.model.Tool
import com.jssdvv.ara.tools.domain.usecase.ToolsDataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val toolsDataManager: ToolsDataManager
) : ViewModel() {
    private val tools: Flow<List<Tool>> = toolsDataManager.select.invoke()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _orderState = MutableStateFlow(OrderState())
    val orderState: StateFlow<OrderState> = _orderState

    val uiState: StateFlow<ToolsUiState> = combine(
        tools,
        _searchQuery,
        _orderState
    ) { allTools, query, order ->
        val filtered = if (query.isBlank()) allTools
        else allTools.filter {
            it.name.contains(query, ignoreCase = true) ||
                (it.code?.contains(query, ignoreCase = true) == true)
        }

        val sorted = when (order.key) {
            OrderKey.NAME -> filtered.sortedBy { it.name }
            OrderKey.TYPE -> filtered.sortedBy { it.type.name }
            OrderKey.CODE -> filtered.sortedBy { it.code }
            else -> filtered
        }.let { if (order.type == OrderType.DESCENDING) it.reversed() else it }

        ToolsUiState.Success(
            tools = sorted,
            searchQuery = query,
            orderState = order
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ToolsUiState.Loading
    )

    fun onEvent(event: ToolsEvent) {
        when (event) {
            is ToolsEvent.OnSearchTools -> _searchQuery.value = event.query
            is ToolsEvent.OnSortTools -> _orderState.value = event.orderState
            is ToolsEvent.OnCreateTool -> {
                viewModelScope.launch {
                    toolsDataManager.upsert(event.tool)
                }
            }
            is ToolsEvent.OnEditTool -> {
                viewModelScope.launch {
                    toolsDataManager.upsert(event.tool)
                }
            }
        }
    }
}

sealed interface ToolsEvent {
    data class OnSearchTools(val query: String) : ToolsEvent
    data class OnSortTools(val orderState: OrderState) : ToolsEvent
    data class OnCreateTool(val tool: Tool) : ToolsEvent
    data class OnEditTool(val tool: Tool) : ToolsEvent
}

sealed interface ToolsUiState {
    data object Loading : ToolsUiState

    @Immutable
    data class Success(
        val tools: List<Tool> = emptyList(),
        val searchQuery: String = "",
        val orderState: OrderState = OrderState()
    ) : ToolsUiState
}