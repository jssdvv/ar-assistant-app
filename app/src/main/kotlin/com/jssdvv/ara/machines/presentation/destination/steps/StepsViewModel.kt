package com.jssdvv.ara.machines.presentation.destination.steps

import android.net.Uri
import androidx.compose.runtime.Immutable
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
import com.jssdvv.ara.machines.domain.model.Pivot
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.usecase.ModelsDataManager
import com.jssdvv.ara.machines.domain.usecase.OperationDataManager
import com.jssdvv.ara.machines.domain.usecase.StepsDataManager
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StepsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    modelsDataManager: ModelsDataManager,
    private val stepsDataManager: StepsDataManager,
    private val opsDataManager: OperationDataManager,
    private val filesManager: FilesManager,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MachinesGraph.StepsRoute>()
    private val machineId = route.machineId
    private val activityId = route.activityId


    private val selectionEnabled = MutableStateFlow(false)

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

    private val selectedOperationId = MutableStateFlow<Int?>(null)
    private val editingOperation = MutableStateFlow<Operation?>(null)
    private val currentOperation: StateFlow<Operation?> = combine(
        editingOperation,
        operationsTargets,
        selectedOperationId
    ) { editingOperation, operationsTargets, selectedOperationId ->
        editingOperation ?: operationsTargets
                .firstOrNull { it.operation.id == selectedOperationId }
                .let { it ?: operationsTargets.lastOrNull() }
                ?.apply { animatedPivots.value = pivots }
                ?.operation
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    private val animatedPivots = MutableStateFlow(emptySet<Pivot>())
    private val transformedTargets: StateFlow<List<OperationTargets>> = combine(
        operationsTargets,
        currentOperation
    ) { opsTargets, current ->
        if (current == null) return@combine emptyList()
        opsTargets.takeWhile { it.operation.id != current.id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val editingStep = MutableStateFlow<Step?>(null)
    private val currentStep: StateFlow<Step?> = combine(
        steps,
        currentOperation
    ) { steps, currentOperation ->
        steps.firstOrNull { it.id == currentOperation?.stepId }
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    val data: StateFlow<StepsData> = combine(
        models,
        steps,
        operationsTargets,
        ::StepsData
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = StepsData()
    )

    val items: StateFlow<StepsItems> = combine(
        currentStep,
        currentOperation,
        editingStep,
        editingOperation,
        ::StepsItems
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = StepsItems()
    )

    val edition: StateFlow<StepsEdition> = combine(
        selectionEnabled,
        editingOperation,
        transformedTargets,
        animatedPivots,
    ) { selectionEnabled, editingOperation, transformedTargets, animatedPivots ->
        val editionEnabled = editingOperation != null
        StepsEdition(
            editionEnabled = editionEnabled,
            selectionEnabled = selectionEnabled && editionEnabled,
            transformedTargets = transformedTargets,
            animatedPivots = animatedPivots,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = StepsEdition()
    )

    val uiState: StateFlow<StepsUiState> = combine(
        data,
        items,
        edition,
        StepsUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = StepsUiState.Loading
    )

    fun onEvent(event: StepsEvent) {
        when (event) {
            StepsEvent.OnCreateStep -> createStep()
            is StepsEvent.OnEditStep -> editStep(event.stepId)
            is StepsEvent.OnChangeEditingStep -> changeEditingStep(event.step)
            is StepsEvent.OnSaveEditingStep -> saveEditingStep()
            is StepsEvent.OnDeleteStep -> {}

            is StepsEvent.OnSelectionChange -> {
                selectionEnabled.value = event.enabled
            }

            is StepsEvent.OnSelectPivots -> selectPivot(event.info)
            is StepsEvent.OnUnselectPivot -> unselectPivot(event.info)

            is StepsEvent.OnCreateOperation -> createOperation(event.stepId)
            StepsEvent.OnEditCurrentOperation -> editCurrentOperation()
            is StepsEvent.OnEditOperation -> editOperation(event.operationId)
            is StepsEvent.OnSelectOperation -> selectOperation(event.operationId)
            is StepsEvent.OnChangeEditingOperation -> changeEditingOperation(event.operation)
            is StepsEvent.OnSaveEditingOperation -> saveEditingOperation()
            StepsEvent.OnCancelEditingOperation -> cancelEditingOperation()
            is StepsEvent.OnDeleteOperation -> {}
        }
    }

    private fun selectPivot(pivot: Pivot) = animatedPivots.update { it + pivot }
    private fun unselectPivot(pivot: Pivot) = animatedPivots.update { it - pivot }

    private fun createStep() {
        editingOperation.value = null
        editingStep.value = Step(activityId = activityId)
    }

    private fun editStep(stepId: Int?) {
        editingStep.value = steps.value.find { it.id == stepId }
    }

    private fun changeEditingStep(step: Step) {
        if (step.id == editingStep.value?.id) editingStep.value = step
    }

    private fun createOperation(stepId: Int) {
        editingOperation.value = Operation(stepId = stepId)
        animatedPivots.value = emptySet()
    }

    private fun selectOperation(operationId: Int) {
        selectedOperationId.value = operationId
    }

    private fun editCurrentOperation() {
        currentOperation.value?.id?.apply(::editOperation)
    }

    private fun editOperation(operationId: Int) {
        operationsTargets.value.firstOrNull { it.operation.id == operationId }?.apply {
            editingOperation.value = operation
            animatedPivots.value = pivots
            selectOperation(operation.id)
        }
    }

    private fun changeEditingOperation(operation: Operation) {
        if (operation.id == editingOperation.value?.id) editingOperation.value = operation
    }

    private fun cancelEditingOperation() {
        editingOperation.value = null
        selectionEnabled.value = false
    }


    private fun saveEditingStep() {
        viewModelScope.launch {
            val step = editingStep.value ?: return@launch
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


    private fun saveEditingOperation() {
        viewModelScope.launch {
            val pivots = animatedPivots.value
            val operation = editingOperation.value ?: return@launch
            val operations = operationsTargets.value
                .filter { it.operation.stepId == operation.stepId }
                .map { it.operation }
                .sortedBy { it.order }

            val existingOperation = operations.find { it.id == operation.id }

            if (existingOperation == null) {
                // New Operation
                val maxOrderNumber = operations.maxOfOrNull { it.order } ?: 0
                val operationTargets = OperationTargets(
                    operation = operation.copy(order = maxOrderNumber + 1),
                    pivots = pivots
                )

                opsDataManager.upsert(operationTargets)
            } else {
                // Existing Operation
                val reorderNumber = operation.order.coerceIn(1, operations.size)
                val updatedList = operations.toMutableList().apply {
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

                if (reorderedOperations.isNotEmpty()) opsDataManager.upsert(*reorderedOperations.toTypedArray())
                opsDataManager.upsert(
                    OperationTargets(
                        operation = editedOperations.first(),
                        pivots = pivots
                    )
                )
            }
            cancelEditingOperation()
        }
    }
}

sealed interface StepsEvent {
    data object OnCreateStep : StepsEvent
    data class OnEditStep(val stepId: Int?) : StepsEvent
    data class OnChangeEditingStep(val step: Step) : StepsEvent
    data object OnSaveEditingStep : StepsEvent
    data class OnDeleteStep(val step: Step) : StepsEvent

    data class OnSelectionChange(val enabled: Boolean) : StepsEvent
    data class OnSelectPivots(val info: Pivot) : StepsEvent
    data class OnUnselectPivot(val info: Pivot) : StepsEvent

    data class OnCreateOperation(val stepId: Int) : StepsEvent
    data object OnEditCurrentOperation : StepsEvent
    data class OnEditOperation(val operationId: Int) : StepsEvent
    data class OnSelectOperation(val operationId: Int) : StepsEvent
    data class OnChangeEditingOperation(val operation: Operation) : StepsEvent
    data object OnSaveEditingOperation : StepsEvent
    data object OnCancelEditingOperation : StepsEvent
    data class OnDeleteOperation(val operationId: Int) : StepsEvent
}

sealed interface StepsUiState {
    data object Loading : StepsUiState
    data object EmptyModels : StepsUiState

    @Immutable
    data class Success(
        val data: StepsData = StepsData(),
        val items: StepsItems = StepsItems(),
        val edition: StepsEdition = StepsEdition()
    ) : StepsUiState
}

@Immutable
data class StepsData(
    val models: List<Model> = emptyList(),
    val steps: List<Step> = emptyList(),
    val operationsTargets: List<OperationTargets> = emptyList()
)

@Immutable
data class StepsItems(
    val currentStep: Step? = null,
    val currentOperation: Operation? = null,
    val editingStep: Step? = null,
    val editingOperation: Operation? = null,
)

@Immutable
data class StepsEdition(
    val editionEnabled: Boolean = false,
    val selectionEnabled: Boolean = false,
    val transformedTargets: List<OperationTargets> = emptyList(),
    val animatedPivots: Set<Pivot> = emptySet()
)