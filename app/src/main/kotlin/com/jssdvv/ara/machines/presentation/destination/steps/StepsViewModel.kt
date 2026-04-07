package com.jssdvv.ara.machines.presentation.destination.steps

import android.net.Uri
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
import com.jssdvv.ara.machines.domain.model.RenderableTarget
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.type.OperationType
import com.jssdvv.ara.machines.domain.usecase.ModelsDataManager
import com.jssdvv.ara.machines.domain.usecase.OperationDataManager
import com.jssdvv.ara.machines.domain.usecase.StepsDataManager
import com.jssdvv.ara.machines.domain.utility.RenderableInfo
import com.jssdvv.ara.machines.domain.utility.RestorableState
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

    private val editingStep = MutableStateFlow<Step?>(null)
    private val editingOperation = MutableStateFlow<Operation?>(null)

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
        editingStep
    ) { steps, editingStep ->
        editingStep ?: steps.maxByOrNull { it.order }
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
            val stepOrderMap = steps.value.associate { it.id to it.order }
            opTargets.sortedWith(
                compareBy(
                    { stepOrderMap[it.operation.stepId] ?: 0 },
                    { it.operation.order }
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
        editingOperation,
        selectedStep
    ) { operationsTargets, editingOperation, selectedStep ->
        editingOperation ?: operationsTargets
            .filter { it.operation.stepId == selectedStep?.id }
            .maxByOrNull { it.operation.order }?.operation
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null
    )

    private val _renderableInfoStates = mutableStateMapOf<RenderableInfo, RenderableState>()
    val renderableInfoStates: SnapshotStateMap<RenderableInfo, RenderableState>
        get() = _renderableInfoStates

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
            StepsEvent.OnSelectNextStep -> selectNextStep()
            StepsEvent.OnSelectPreviousStep -> selectPreviousStep()
            is StepsEvent.OnUpdateStep -> updateStep(event.step)
            is StepsEvent.OnSaveStep -> saveStep(event.step)
            is StepsEvent.OnDeleteStep -> {}

            is StepsEvent.OnNewOperation -> newOperation(stepId = event.stepId)
            is StepsEvent.OnSelectOperation -> selectOperation(operationId = event.operationId)
            is StepsEvent.OnUpdateOperation -> updateOperation(operation = event.operation)
            is StepsEvent.OnSaveOperation -> saveOperation(event.operation)
            is StepsEvent.OnDeleteOperation -> {}
        }
    }

    fun onExternalEvent(event: StepsExternalEvent) {
        when (event) {
            is StepsExternalEvent.OnToggleSelection -> {
                _isSelectionEnabled.value = event.isEnabled
            }

            is StepsExternalEvent.OnLoadRenderables -> loadRenderable(event.info, event.state)
            is StepsExternalEvent.OnSelectRenderable -> selectRenderable(event.renderableInfo)
            is StepsExternalEvent.OnSelectExistingRenderables -> selectExistingRenderables(event.targets)
            is StepsExternalEvent.OnUnselectRenderable -> unselectRenderable(event.renderableInfo)
            is StepsExternalEvent.OnToggleRenderableVisibility -> toggleRenderableVisibility(event.renderableInfo)
            StepsExternalEvent.OnUnselectAllRenderables -> unselectAllRenderables()
            StepsExternalEvent.OnShowAllRenderables -> showAllRenderables()
            StepsExternalEvent.OnHideAllRenderables -> hideAllRenderables()
        }
    }

    private fun newStep() {
        // Caches a new step in the state
        // holder with step.id = 0
        editingStep.value = Step(
            activityId = activityId,
            name = "",
            order = 0
        )
        editingOperation.value = null
    }

    private fun selectStep(stepId: Int?) {
        editingStep.value = steps.value.find { it.id == stepId }
    }

    private fun selectNextStep() {
        val current = editingStep.value ?: selectedStep.value ?: return
        val sorted = steps.value.sortedBy { it.order }
        val currentIndex = sorted.indexOfFirst { it.id == current.id }
        val next = sorted.getOrNull(currentIndex + 1) ?: return
        editingStep.value = next
    }

    private fun selectPreviousStep() {
        val current = editingStep.value ?: selectedStep.value ?: return
        val sorted = steps.value.sortedBy { it.order }
        val currentIndex = sorted.indexOfFirst { it.id == current.id }
        val previous = sorted.getOrNull(currentIndex - 1) ?: return
        editingStep.value = previous
    }

    private fun updateStep(step: Step) {
        val current = editingStep.value ?: return
        if (step.id != current.id) return
        editingStep.value = step
    }

    private fun saveStep(step: Step) {
        viewModelScope.launch {
            val sorted = steps.value.sortedBy { it.order }
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
                val maxOrderNumber = sorted.maxOfOrNull { it.order } ?: 0
                val newStep = step.copy(
                    id = 0,
                    activityId = activityId,
                    order = maxOrderNumber + 1,
                    imageUri = imageUri
                )
                val generatedStepId = stepsDataManager.upsert(newStep).lastOrNull()
                editingStep.value = steps.value.find { it.id == generatedStepId?.toInt() }
            } else {
                // Existing step
                val newOrderNumber = step.order.coerceIn(1, sorted.size)
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
                            order = newOrderNumber,
                            description = step.description,
                            imageUri = imageUri
                        )
                    )

                    // This updates the order numbers using
                    // the index of the element in the list
                    forEachIndexed { index, step ->
                        this[index] = step.copy(order = index + 1)
                    }
                }
                stepsDataManager.upsert(*updatedList.toTypedArray())
                editingStep.value = steps.value.find { it.id == existingStep.id }
            }
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
        editingOperation.value = Operation(
            stepId = stepId,
            order = 0,
            title = "",
            type = OperationType.POINT_TO_POINT,
        )
    }

    private fun selectOperation(operationId: Int?) {
        editingOperation.value =
            operationsTargets.value.find { it.operation.id == operationId }?.operation
    }

    private fun updateOperation(operation: Operation) {
        val current = editingOperation.value ?: return
        if (operation.id != current.id) return
        editingOperation.value = operation
    }

    private fun saveOperation(operation: Operation) {
        viewModelScope.launch {
            val currentOperations = operationsTargets.value
                .filter { it.operation.stepId == operation.stepId }
                .map { it.operation }
                .sortedBy { it.order }

            val renderablesTargets = renderableInfoStates
                .filter { it.value.isSelected }
                .map { (renderable, _) ->
                    val state = renderableInfoStates[renderable]

                    RenderableTarget(
                        operationId = operation.id,
                        modelId = renderable.modelId,
                        xxh3 = renderable.xxh3,
                        name = state?.name ?: "",
                    )
                }

            val existingOperation = currentOperations.find { it.id == operation.id }

            if (existingOperation == null) {
                // New Operation
                val maxOrderNumber = currentOperations.maxOfOrNull { it.order } ?: 0

                val operationTargets = OperationTargets(
                    operation = operation.copy(order = maxOrderNumber + 1),
                    targets = renderablesTargets
                )

                opsDataManager.upsert(operationTargets)

                editingOperation.value = null
            } else {
                // Existing Operation
                val reorderNumber = operation.order.coerceIn(1, currentOperations.size)
                val updatedList = currentOperations.toMutableList().apply {
                    val currentIndex = indexOfFirst { it.id == operation.id }
                    if (currentIndex != -1) removeAt(currentIndex)

                    add(
                        index = reorderNumber - 1,
                        element = operation.copy(
                            id = existingOperation.id,
                            stepId = existingOperation.stepId,
                            order = reorderNumber
                        )
                    )

                    forEachIndexed { index, operation ->
                        this[index] = operation.copy(order = index + 1)
                    }
                }

                val (editedOperations, reorderedOperations) =
                    updatedList.partition { it.id == operation.id }

                if(reorderedOperations.isNotEmpty()) opsDataManager.upsert(*reorderedOperations.toTypedArray())
                opsDataManager.upsert(
                    OperationTargets(
                        operation = editedOperations.first(),
                        targets = renderablesTargets
                    )
                )

                editingOperation.value = editedOperations.first()
            }
        }
    }

    private fun deleteOperations(opTargets: OperationTargets) {
        viewModelScope.launch {
            opsDataManager.delete(opTargets.operation)
        }
    }

    private fun loadRenderable(renderableInfo: RenderableInfo, state: RenderableState) {
        _renderableInfoStates.putIfAbsent(renderableInfo, state)
    }

    private fun selectRenderable(vararg renderableInfo: RenderableInfo) {
        renderableInfo.forEach {
            val currentState = _renderableInfoStates[it] ?: RenderableState()
            _renderableInfoStates[it] = currentState.copy(isSelected = true)
        }
    }

    private fun selectExistingRenderables(renderableTargets: List<RenderableTarget>) {
        renderableTargets.forEach { target ->
            val renderableInfo = RenderableInfo(target.modelId, target.xxh3)
            val currentState = _renderableInfoStates[renderableInfo] ?: RenderableState()
            _renderableInfoStates[renderableInfo] = currentState.copy(isSelected = true)
        }
    }

    private fun unselectRenderable(renderableInfo: RenderableInfo) {
        val currentState = _renderableInfoStates[renderableInfo] ?: RenderableState()
        _renderableInfoStates[renderableInfo] = currentState.copy(isSelected = false)
    }

    private fun toggleRenderableVisibility(renderableInfo: RenderableInfo) {
        val currentState = _renderableInfoStates[renderableInfo] ?: RenderableState()
        _renderableInfoStates[renderableInfo] = currentState.copy(isVisible = !currentState.isVisible)
    }

    private fun unselectAllRenderables() {
        _renderableInfoStates.putAll(_renderableInfoStates.mapValues { it.value.copy(isSelected = false) })
    }

    private fun showAllRenderables() {
        _renderableInfoStates.putAll(_renderableInfoStates.mapValues { it.value.copy(isVisible = true) })
    }

    private fun hideAllRenderables() {
        _renderableInfoStates.putAll(_renderableInfoStates.mapValues { it.value.copy(isVisible = false) })
    }
}

sealed interface StepsEvent {
    data object OnNewStep : StepsEvent
    data class OnSelectStep(val stepId: Int?) : StepsEvent
    data object OnSelectNextStep : StepsEvent
    data object OnSelectPreviousStep : StepsEvent
    data class OnUpdateStep(val step: Step) : StepsEvent
    data class OnSaveStep(val step: Step) : StepsEvent
    data class OnDeleteStep(val step: Step) : StepsEvent

    data class OnNewOperation(val stepId: Int) : StepsEvent
    data class OnSelectOperation(val operationId: Int?) : StepsEvent
    data class OnUpdateOperation(val operation: Operation) : StepsEvent
    data class OnSaveOperation(val operation: Operation) : StepsEvent
    data class OnDeleteOperation(val operationId: Int) : StepsEvent
}

sealed interface StepsExternalEvent {
    data class OnToggleSelection(val isEnabled: Boolean) : StepsExternalEvent
    data class OnLoadRenderables(
        val info: RenderableInfo,
        val state: RenderableState
    ) : StepsExternalEvent

    data class OnSelectRenderable(val renderableInfo: RenderableInfo) : StepsExternalEvent
    data class OnToggleRenderableVisibility(val renderableInfo: RenderableInfo) : StepsExternalEvent
    data class OnUnselectRenderable(val renderableInfo: RenderableInfo) : StepsExternalEvent
    data class OnSelectExistingRenderables(val targets: List<RenderableTarget>) : StepsExternalEvent
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

/**
 * Temporal state holder that initializes at nodes loading
 */
data class RenderableState(
    val name: String = "",
    val isVisible: Boolean = true,
    val isSelected: Boolean = false,
    override val index: Int = 0,
    override val initialPosition: Position = Position(),
    override val initialQuaternion: Quaternion = Quaternion()
): RestorableState