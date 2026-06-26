package com.jssdvv.ara.tools.presentation.destination.tools

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.type.OrderKey
import com.jssdvv.ara.core.domain.type.OrderState
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.core.domain.type.UriType
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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val toolsDataManager: ToolsDataManager,
    private val filesManager: FilesManager,
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
                    val tool = event.tool.copy(
                        bodyMediaUri = replaceToolImage(null, event.tool.bodyMediaUri),
                        symbolMediaUri = replaceToolImage(null, event.tool.symbolMediaUri),
                    )
                    toolsDataManager.upsert(tool)
                }
            }
            is ToolsEvent.OnEditTool -> {
                viewModelScope.launch {
                    val oldTool = (uiState.value as? ToolsUiState.Success)
                        ?.tools?.find { it.id == event.tool.id }
                    val tool = event.tool.copy(
                        bodyMediaUri = replaceToolImage(oldTool?.bodyMediaUri, event.tool.bodyMediaUri),
                        symbolMediaUri = replaceToolImage(oldTool?.symbolMediaUri, event.tool.symbolMediaUri),
                    )
                    toolsDataManager.upsert(tool)
                }
            }
            is ToolsEvent.OnDeleteTool -> {
                viewModelScope.launch {
                    val tool = event.tool
                    tool.bodyMediaUri?.path?.let { filesManager.deleteFile(File(it)) }
                    tool.symbolMediaUri?.path?.let { filesManager.deleteFile(File(it)) }
                    toolsDataManager.delete(tool)
                }
            }
        }
    }

    private suspend fun replaceToolImage(currentUri: Uri?, newUri: Uri?): Uri? {
        if (newUri == null) return null
        val uriType = filesManager.getUriType(newUri) ?: return newUri
        if (uriType != UriType.CONTENT) return newUri

        val file = filesManager.copyToolImageToInternalStorage(newUri)?.also {
            currentUri?.path?.let { path ->
                try {
                    filesManager.deleteFile(File(path))
                } catch (_: Exception) { }
            }
        }
        return file?.toUri() ?: currentUri
    }
}

sealed interface ToolsEvent {
    data class OnSearchTools(val query: String) : ToolsEvent
    data class OnSortTools(val orderState: OrderState) : ToolsEvent
    data class OnCreateTool(val tool: Tool) : ToolsEvent
    data class OnEditTool(val tool: Tool) : ToolsEvent
    data class OnDeleteTool(val tool: Tool) : ToolsEvent
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