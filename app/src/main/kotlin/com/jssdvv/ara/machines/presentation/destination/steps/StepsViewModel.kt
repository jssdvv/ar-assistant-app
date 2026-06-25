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
import com.jssdvv.ara.machines.domain.model.Activity
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Pivot
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.type.ActivityType
import com.jssdvv.ara.machines.domain.usecase.ActivitiesDataManager
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


@HiltViewModel
class StepsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    modelsDataManager: ModelsDataManager,
    private val stepsDataManager: StepsDataManager,
    private val opsDataManager: OperationDataManager,
    private val activitiesDataManager: ActivitiesDataManager,
    private val filesManager: FilesManager,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MachinesGraph.StepsRoute>()
    private val machineId = route.machineId
    private val activityId = route.activityId

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

    private val activity: StateFlow<Activity?> = activitiesDataManager
        .select(machineId)
        .map { list -> list.find { it.id == activityId } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
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
    private val selectionEnabled = MutableStateFlow(false)
    private val editingTargets = MutableStateFlow<OperationTargets?>(null)
    private val currentTargets: StateFlow<OperationTargets?> = combine(
        editingTargets,
        operationsTargets,
        selectedOperationId
    ) { editingTargets, operationsTargets, selectedOperationId ->
        editingTargets ?: operationsTargets.firstOrNull { it.operation.id == selectedOperationId }
        ?: operationsTargets.lastOrNull()
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    private val precedentTargets: StateFlow<List<OperationTargets>> = combine(
        operationsTargets,
        currentTargets
    ) { operationTargets, currentTargets ->
        if (currentTargets == null) return@combine emptyList()
        operationTargets.takeWhile { it.operation.id != currentTargets.operation.id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val editingStep = MutableStateFlow<Step?>(null)
    private val currentStep: StateFlow<Step?> = combine(
        steps,
        editingStep,
        currentTargets
    ) { steps, editingStep, currentTargets ->
        editingStep ?: steps.firstOrNull { it.id == currentTargets?.operation?.stepId }
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
        activity,
        ::StepsData
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = StepsData()
    )

    val items: StateFlow<StepsItems> = combine(
        selectionEnabled,
        currentStep,
        editingTargets
    ) { selectionEnabled, currentStep, editingTargets ->
        val editionEnabled = editingTargets != null
        StepsItems(
            selectionEnabled = selectionEnabled && editionEnabled,
            editionEnabled = editionEnabled,
            currentStep = currentStep,
            currentEditingTargets = editingTargets
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = StepsItems()
    )

    val animation: StateFlow<StepsAnimation> = combine(
        precedentTargets,
        currentTargets,
        ::StepsAnimation
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = StepsAnimation()
    )

    val uiState: StateFlow<StepsUiState> = combine(
        data,
        items,
        animation,
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

            is StepsEvent.OnSelectPivots -> selectPivot(event.pivot)
            is StepsEvent.OnUnselectPivot -> unselectPivot(event.pivot)

            is StepsEvent.OnCreateOperation -> createOperation(event.stepId)
            StepsEvent.OnEditCurrentOperation -> editCurrentOperation()
            is StepsEvent.OnEditOperation -> editOperation(event.operationId)
            is StepsEvent.OnSelectOperation -> selectOperation(event.operationId)
            is StepsEvent.OnChangeEditingOperation -> changeEditingOperation(event.operation)
            is StepsEvent.OnSaveEditingOperation -> saveEditingOperation()
            StepsEvent.OnCancelEditingOperation -> cancelEditingOperation()
            is StepsEvent.OnDeleteOperation -> {}
            is StepsEvent.OnSaveActivityData -> onSaveActivityData(event)
            StepsEvent.OnDeleteActivity -> onDeleteActivity()
        }
    }

    private fun selectPivot(pivot: Pivot) {
        editingTargets.update {
            val pivots = it?.pivots ?: return@update null
            it.copy(pivots = pivots + pivot)
        }
    }
    private fun unselectPivot(pivot: Pivot) {
        editingTargets.update {
            val pivots = it?.pivots ?: return@update null
            it.copy(pivots = pivots - pivot)
        }
    }

    private fun createStep() {
        editingTargets.value = null
        editingStep.value = Step(activityId = activityId)
    }

    private fun editStep(stepId: Int?) {
        editingStep.value = steps.value.find { it.id == stepId }
    }

    private fun changeEditingStep(step: Step) {
        if (step.id == editingStep.value?.id) editingStep.value = step
    }

    private fun createOperation(stepId: Int) {
        editingTargets.value = OperationTargets(
            operation = Operation(stepId = stepId),
            pivots = emptySet()
        )
    }

    private fun selectOperation(operationId: Int) {
        selectedOperationId.value = operationId
    }

    private fun editCurrentOperation() {
        currentTargets.value?.operation?.id?.let(::editOperation)
    }

    private fun editOperation(operationId: Int) {
        operationsTargets.value.firstOrNull { it.operation.id == operationId }?.apply {
            editingTargets.value = this
            selectOperation(operation.id)
        }
    }

    private fun changeEditingOperation(operation: Operation) {
        if (operation.id == editingTargets.value?.operation?.id) {
            editingTargets.update { it?.copy(operation = operation) }
        }
    }

    private fun cancelEditingOperation() {
        editingTargets.value = null
        selectionEnabled.value = false
    }


    private fun saveEditingStep() {
        viewModelScope.launch {
            val step = editingStep.value ?: return@launch
            val sorted = steps.value.sortedBy { it.order }
            val existingStep = sorted.find { it.id == step.id }

            // No changes = return
            if (existingStep == step) return@launch

            val imageUri = when (step.imageUri) {
                existingStep?.imageUri,
                null -> existingStep?.imageUri

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
            }
            editingStep.value = null
        }
    }

    private fun onSaveActivityData(event: StepsEvent.OnSaveActivityData) {
        viewModelScope.launch {
            val currentActivity = activity.value ?: return@launch
            val imageUri = when (event.imageUri) {
                currentActivity.imageUri, null -> currentActivity.imageUri
                else -> replaceImage(currentActivity.imageUri, event.imageUri)?.toUri()
                    ?: currentActivity.imageUri
            }
            activitiesDataManager.upsert(
                currentActivity.copy(
                    name = event.name,
                    type = event.type,
                    description = event.description,
                    frequency = event.frequency,
                    frequencyUnit = event.frequencyUnit,
                    imageUri = imageUri,
                )
            )
        }
    }

    private fun onDeleteActivity() {
        viewModelScope.launch {
            val currentActivity = activity.value ?: return@launch
            currentActivity.imageUri?.path?.let { imagePath ->
                try {
                    filesManager.deleteFile(File(imagePath))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            activitiesDataManager.delete(currentActivity)
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
            val editingTargets = this@StepsViewModel.editingTargets.value ?: return@launch
            val stepOperations = this@StepsViewModel.operationsTargets.value
                .filter { it.operation.stepId == editingTargets.operation.stepId }
                .map { it.operation }

            if (stepOperations.none { it.id == editingTargets.operation.id }) {
                saveNewOperation(editingTargets, stepOperations)
            } else {
                saveExistingOperation(editingTargets, stepOperations)
            }
            cancelEditingOperation()
        }
    }

    private suspend fun saveNewOperation(
        editingTargets: OperationTargets,
        stepOperations: List<Operation>
    ) {
        val newOrder = (stepOperations.maxOfOrNull { it.order } ?: 0) + 1
        opsDataManager.upsert(
            editingTargets.copy(
                operation = editingTargets.operation.copy(order = newOrder),
            )
        )
    }

    private suspend fun saveExistingOperation(
        editingTargets: OperationTargets,
        stepOperations: List<Operation>
    ) {
        val reorderNumber = editingTargets.operation.order.coerceIn(1, stepOperations.size)
        val updatedList = stepOperations
            .filter { it.id != editingTargets.operation.id }
            .toMutableList()
            .apply { add(reorderNumber - 1, editingTargets.operation.copy(order = reorderNumber)) }
            .mapIndexed { index, operation -> operation.copy(order = index + 1) }

        val reorderedOperations = updatedList.filter { it.id != editingTargets.operation.id }
        if (reorderedOperations.isNotEmpty()) {
            opsDataManager.upsert(*reorderedOperations.toTypedArray())
        }
        opsDataManager.upsert(
            editingTargets.copy(
                operation = updatedList.first { it.id == editingTargets.operation.id },
            )
        )
    }
}

sealed interface StepsEvent {
    data object OnCreateStep : StepsEvent
    data class OnEditStep(val stepId: Int?) : StepsEvent
    data class OnChangeEditingStep(val step: Step) : StepsEvent
    data object OnSaveEditingStep : StepsEvent
    data class OnDeleteStep(val step: Step) : StepsEvent

    data class OnSelectionChange(val enabled: Boolean) : StepsEvent
    data class OnSelectPivots(val pivot: Pivot) : StepsEvent
    data class OnUnselectPivot(val pivot: Pivot) : StepsEvent

    data class OnCreateOperation(val stepId: Int) : StepsEvent
    data object OnEditCurrentOperation : StepsEvent
    data class OnEditOperation(val operationId: Int) : StepsEvent
    data class OnSelectOperation(val operationId: Int) : StepsEvent
    data class OnChangeEditingOperation(val operation: Operation) : StepsEvent
    data object OnSaveEditingOperation : StepsEvent
    data object OnCancelEditingOperation : StepsEvent
    data class OnDeleteOperation(val operationId: Int) : StepsEvent
    data class OnSaveActivityData(
        val name: String,
        val type: ActivityType,
        val description: String?,
        val frequency: Int?,
        val frequencyUnit: String?,
        val imageUri: Uri?,
    ) : StepsEvent
    data object OnDeleteActivity : StepsEvent
}

sealed interface StepsUiState {
    data object Loading : StepsUiState
    data object EmptyModels : StepsUiState

    @Immutable
    data class Success(
        val data: StepsData = StepsData(),
        val items: StepsItems = StepsItems(),
        val animation: StepsAnimation = StepsAnimation()
    ) : StepsUiState
}

@Immutable
data class StepsData(
    val models: List<Model> = emptyList(),
    val steps: List<Step> = emptyList(),
    val operationsTargets: List<OperationTargets> = emptyList(),
    val activity: Activity? = null,
)

@Immutable
data class StepsItems(
    val selectionEnabled: Boolean = false,
    val editionEnabled: Boolean = false,
    val currentStep: Step? = null,
    val currentEditingTargets: OperationTargets? = null
)

@Immutable
data class StepsAnimation(
    val precedentTargets: List<OperationTargets> = emptyList(),
    val currentTargets: OperationTargets? = null
)