package com.jssdvv.ara.machines.presentation.destination.steps

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.type.OperationType
import com.jssdvv.ara.machines.domain.usecase.ModelsDataManager
import com.jssdvv.ara.machines.domain.usecase.OperationDataManager
import com.jssdvv.ara.machines.domain.usecase.StepsDataManager
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StepsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val modelsDataManager: ModelsDataManager,
    private val stepsDataManager: StepsDataManager,
    private val opsDataManager: OperationDataManager,
    private val filesManager: FilesManager,
) : ViewModel() {

    private val machineId = savedStateHandle.toRoute<MachinesGraph.StepsRoute>().machineId
    private val activityId = savedStateHandle.toRoute<MachinesGraph.StepsRoute>().activityId

    private val edittingStep = MutableStateFlow<Step?>(null)
    private val edittingOperation = MutableStateFlow<Operation?>(null)

    private val models: StateFlow<List<Model>> = modelsDataManager
        .select(machineId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    private val steps: StateFlow<List<Step>> = stepsDataManager
        .select(activityId, OrderType.ASCENDING)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    private val selectedStep: StateFlow<Step?> = combine(
        steps,
        edittingStep
    ) { steps, selectedStep ->
        selectedStep ?: steps.maxByOrNull { it.orderNumber }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null
    )

    private val operationsTargets: StateFlow<List<OperationTargets>> = steps
        .map { it.map { step -> step.id } }
        .distinctUntilChanged()
        .flatMapLatest { ids ->
            if (ids.isEmpty()) flowOf(emptyList())
            else opsDataManager.select(ids, OrderType.ASCENDING)
        }
        .map { opTargets ->
            val stepOrderMap = steps.value.associate { it.id to it.orderNumber }
            opTargets.sortedWith(
                compareBy(
                    { stepOrderMap[it.operation.stepId] ?: 0 },
                    { it.operation.orderNumber }
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    private val selectedOperation: StateFlow<Operation?> = combine(
        operationsTargets,
        edittingOperation,
        selectedStep
    ) { operationsTargets, selectedOperation, currentStep ->
        selectedOperation
            ?: operationsTargets.find { it.operation.stepId == currentStep?.id }?.operation
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null
    )

    private val _renderableStates = mutableStateMapOf<Renderable, RenderableState>()
    val renderableStates: SnapshotStateMap<Renderable, RenderableState>
        get() = _renderableStates

    private val _isSelectionEnabled = MutableStateFlow(false)
    val isSelectionEnabled = _isSelectionEnabled.asStateFlow()

    val uiState: StateFlow<StepsUiState> = combine(
        models,
        steps,
        operationsTargets,
        selectedStep,
        selectedOperation,
    ) { models, steps, ops, selectedStep, selectedOperation ->
        if (models.isEmpty()) StepsUiState.EmptyModels
        else StepsUiState.Success(
            models,
            steps,
            ops,
            selectedStep,
            selectedOperation
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = StepsUiState.Loading
    )

    fun onEvent(event: StepsEvent) {
        when (event) {
            StepsEvent.OnNewStep -> newStep()
            is StepsEvent.OnSelectStep -> selectStep(event.stepId)
            StepsEvent.OnDeselectStep -> { edittingStep.value = null }
            StepsEvent.OnSelectNextStep -> selectNextStep()
            StepsEvent.OnSelectPreviousStep -> selectPreviousStep()
            is StepsEvent.OnUpdateStep -> updateStep(event.step)
            is StepsEvent.OnSaveStep -> saveStep(event.step)
            is StepsEvent.OnDeleteStep -> {}

            is StepsEvent.OnNewOperation -> newOperation(stepId = event.stepId)
            is StepsEvent.OnSelectOperation -> selectOperation(operationId = event.operationId)
            StepsEvent.OnDeselectOperation -> { edittingOperation.value = null }
            is StepsEvent.OnUpdateOperation -> updateOperation(operation = event.operation)
            is StepsEvent.OnSaveOperation -> {}
            is StepsEvent.OnDeleteOperation -> {}
        }
    }

    fun onExternalEvent(event: StepsExternalEvent) {
        when (event) {
            is StepsExternalEvent.OnToggleSelection -> {
                _isSelectionEnabled.value = event.isEnabled
            }

            is StepsExternalEvent.OnLoadRenderables -> loadRenderable(event.renderable)
            is StepsExternalEvent.OnSelectRenderable -> selectRenderable(event.renderable)
            is StepsExternalEvent.OnUnselectRenderable -> unselectRenderable(event.renderable)
            is StepsExternalEvent.OnToggleRenderableVisibility -> toggleRenderableVisibility(event.renderable)
            StepsExternalEvent.OnUnselectAllRenderables -> unselectAllRenderables()
            StepsExternalEvent.OnShowAllRenderables -> showAllRenderables()
            StepsExternalEvent.OnHideAllRenderables -> hideAllRenderables()
        }
    }

    private fun newStep() {
        // Caches a new step in the state
        // holder with step.id = 0
        edittingStep.value = Step(
            activityId = activityId,
            name = "",
            orderNumber = 0
        )
    }

    private fun selectStep(stepId: Int?) {
        edittingStep.value = steps.value.find { it.id == stepId }
    }

    private fun selectNextStep() {
        val current = edittingStep.value ?: selectedStep.value ?: return
        val sorted = steps.value.sortedBy { it.orderNumber }
        val currentIndex = sorted.indexOfFirst { it.id == current.id }
        val next = sorted.getOrNull(currentIndex + 1) ?: return
        edittingStep.value = next
    }

    private fun selectPreviousStep() {
        val current = edittingStep.value ?: selectedStep.value ?: return
        val sorted = steps.value.sortedBy { it.orderNumber }
        val currentIndex = sorted.indexOfFirst { it.id == current.id }
        val previous = sorted.getOrNull(currentIndex - 1) ?: return
        edittingStep.value = previous
    }

    private fun updateStep(step: Step) {
        val current = edittingStep.value ?: return
        if (step.id != current.id) return
        edittingStep.value = step
    }

    private fun saveStep(step: Step) {
        viewModelScope.launch {
            val sorted = steps.value.sortedBy { it.orderNumber }
            val existingStep = sorted.find { it.id == step.id }

            // No changes = return
            if (existingStep == step) return@launch

            val imageUri = when {
                step.imageUri == existingStep?.imageUri -> existingStep?.imageUri
                step.imageUri == null -> existingStep?.imageUri
                else -> replaceImage(existingStep?.imageUri, step.imageUri)?.toUri()
                    ?: existingStep?.imageUri
            }

            if (existingStep == null) {
                // New step
                val maxOrderNumber = sorted.maxOfOrNull { it.orderNumber } ?: 0
                val newStep = step.copy(
                    id = 0,
                    activityId = activityId,
                    orderNumber = maxOrderNumber + 1,
                    imageUri = imageUri
                )
                stepsDataManager.upsert(newStep)
            } else {
                // Existing step
                val newOrderNumber = step.orderNumber.coerceIn(1, sorted.size)
                val updatedList = sorted.toMutableList().apply {
                    val currentIndex = indexOfFirst { it.id == step.id }
                    if (currentIndex != -1) removeAt(currentIndex)

                    // This adds the new step in the index
                    // position of its order number and shifts
                    // the rest of steps 1 to the right
                    add(
                        index = newOrderNumber - 1,
                        element = existingStep.copy(
                            name = step.name,
                            orderNumber = newOrderNumber,
                            description = step.description,
                            imageUri = imageUri
                        )
                    )

                    // This updates the order numbers using
                    // the index of the element in the list
                    forEachIndexed { index, step ->
                        this[index] = step.copy(orderNumber = index + 1)
                    }
                }
                stepsDataManager.upsert(*updatedList.toTypedArray())
            }
            edittingStep.value = null
        }
    }

    // Returns the new image file or null if no change
    private fun replaceImage(
        currentImageUri: Uri? = null,
        newImageContentUri: Uri? = null,
    ): File? {
        if (newImageContentUri == null) return null

        return filesManager.copyImageToInternalStorage(newImageContentUri, machineId)?.also {
            currentImageUri?.path?.let { currentImageFilePath ->
                try {
                    filesManager.deleteFile(File(currentImageFilePath))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun newOperation(stepId: Int) {
        edittingOperation.value = Operation(
            stepId = stepId,
            orderNumber = 0,
            title = "",
            type = OperationType.POINT_TO_POINT,
        )
    }

    private fun selectOperation(operationId: Int) {
        edittingOperation.value = operationsTargets.value.find { it.operation.id == operationId }?.operation
    }

    private fun updateOperation(operation: Operation) {
        Log.d("TEST_UPDATE", operation.toString())
        val current = edittingOperation.value ?: return
        if (operation.id != current.id) return
        edittingOperation.value = operation
    }







    private fun upsertOperation(opTargets: OperationTargets) {
        viewModelScope.launch {
            opsDataManager.upsert(opTargets)
        }
    }

    private fun deleteOperations(opTargets: OperationTargets) {
        viewModelScope.launch {
            opsDataManager.delete(opTargets.operation)
        }
    }

    private fun loadRenderable(renderable: Renderable) {
        _renderableStates.putIfAbsent(renderable, RenderableState())
    }

    private fun selectRenderable(renderable: Renderable) {
        val currentState = _renderableStates[renderable] ?: RenderableState()
        _renderableStates[renderable] = currentState.copy(isSelected = true)
    }

    private fun unselectRenderable(renderable: Renderable) {
        val currentState = _renderableStates[renderable] ?: RenderableState()
        _renderableStates[renderable] = currentState.copy(isSelected = false)
    }

    private fun toggleRenderableVisibility(renderable: Renderable) {
        val currentState = _renderableStates[renderable] ?: RenderableState()
        _renderableStates[renderable] = currentState.copy(isVisible = !currentState.isVisible)
    }

    private fun unselectAllRenderables() {
        _renderableStates.keys.forEach {
            _renderableStates[it] = _renderableStates[it]
                ?.copy(isSelected = false) ?: RenderableState()
        }
    }

    private fun showAllRenderables() {
        _renderableStates.keys.forEach {
            _renderableStates[it] = _renderableStates[it]
                ?.copy(isVisible = true) ?: RenderableState()
        }
    }

    private fun hideAllRenderables() {
        _renderableStates.keys.forEach {
            _renderableStates[it] = _renderableStates[it]
                ?.copy(isVisible = false) ?: RenderableState()
        }
    }
}

sealed interface StepsEvent {

//    data class OnUpsertStep(
//        val id: Int,
//        val orderNumber: Int = 0,
//        val name: String,
//        val desc: String? = null,
//        val imageUri: Uri? = null
//    ) : StepsEvent

    data object OnNewStep : StepsEvent
    data class OnSelectStep(val stepId: Int?) : StepsEvent
    data object OnSelectNextStep : StepsEvent
    data object OnDeselectStep: StepsEvent
    data object OnSelectPreviousStep : StepsEvent
    data class OnUpdateStep(val step: Step) : StepsEvent
    data class OnSaveStep(val step: Step) : StepsEvent
    data class OnDeleteStep(val step: Step) : StepsEvent

    data class OnNewOperation(val stepId: Int) : StepsEvent
    data class OnSelectOperation(val operationId: Int) : StepsEvent
    data object OnDeselectOperation : StepsEvent
    data class OnUpdateOperation(val operation: Operation) : StepsEvent
    data class OnSaveOperation(val operation: Operation) : StepsEvent
    data class OnDeleteOperation(val operationId: Int) : StepsEvent
}

sealed interface StepsExternalEvent {
    data class OnToggleSelection(val isEnabled: Boolean) : StepsExternalEvent
    data class OnLoadRenderables(val renderable: Renderable) : StepsExternalEvent
    data class OnSelectRenderable(val renderable: Renderable) : StepsExternalEvent
    data class OnToggleRenderableVisibility(val renderable: Renderable) : StepsExternalEvent
    data class OnUnselectRenderable(val renderable: Renderable) : StepsExternalEvent
    data object OnUnselectAllRenderables : StepsExternalEvent
    data object OnShowAllRenderables : StepsExternalEvent
    data object OnHideAllRenderables : StepsExternalEvent
}

sealed interface StepsUiState {
    data object Loading : StepsUiState
    data object EmptyModels : StepsUiState
    data class Success(
        val models: List<Model> = emptyList(),
        val steps: List<Step> = emptyList(),
        val operationTargets: List<OperationTargets> = emptyList(),
        val selectedStep: Step? = null,
        val selectedOp: Operation? = null,
    ) : StepsUiState
}

data class Renderable(
    val modelId: Int,
    val index: Int,
    val name: String,
    val initialPosition: Position = Position(),
    val initialQuaternion: Quaternion = Quaternion()
)

data class RenderableState(
    val isVisible: Boolean = true,
    val isSelected: Boolean = false
)