package com.jssdvv.ara.machines.presentation.destination.ar_session


import android.Manifest
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.repository.PermissionHandler
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.core.presentation.common.state.ManifestString
import com.jssdvv.ara.core.presentation.common.state.Permission
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.usecase.MarkersDataManager
import com.jssdvv.ara.machines.domain.usecase.ModelsDataManager
import com.jssdvv.ara.machines.domain.usecase.OperationDataManager
import com.jssdvv.ara.machines.domain.usecase.StepsDataManager
import com.jssdvv.ara.machines.presentation.destination.ar_session.component.Speed
import com.jssdvv.ara.machines.presentation.destination.calibration.BitmapInfo
import com.jssdvv.ara.machines.presentation.destination.calibration.ModelsCalibrationViewModel.Companion.permissionsManifestStrings
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import com.jssdvv.ara.machines.presentation.sceneview.node.MarkerNode
import com.jssdvv.ara.machines.presentation.sceneview.node.OriginNode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ARSessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val permissionHandler: PermissionHandler,
    private val filesManager: FilesManager,
    markersDataManager: MarkersDataManager,
    modelsDataManager: ModelsDataManager,
    stepsDataManager: StepsDataManager,
    private val opsDataManager: OperationDataManager,
) : ViewModel() {

    companion object {
        const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    }

    val route = savedStateHandle.toRoute<MachinesGraph.ARSessionRoute>()
    val machineId = route.machineId
    val activityId = route.activityId

    private val _permissions = MutableStateFlow(
        setOf(
            Permission(
                manifestString = CAMERA_PERMISSION,
                state = permissionHandler.getPermissionState(CAMERA_PERMISSION, true)
            )
        )
    )

    private val _notification = MutableSharedFlow<NotificationEvent>(extraBufferCapacity = 1)

    private val markers: StateFlow<List<Marker>> = markersDataManager
        .select(machineId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

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

    private val selectedMarker = MutableStateFlow<Marker?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val selectedMarkerBitmap: StateFlow<BitmapInfo?> = selectedMarker
        .mapNotNull { it?.let { marker -> marker.id to marker.imageUri } }
        .distinctUntilChanged()
        .mapLatest { (id, imageUri) ->
            filesManager.getBitmap(imageUri)?.let { BitmapInfo(id, it) }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    private val selectedOperationId = MutableStateFlow<Int?>(null)
    private val currentTargets: StateFlow<OperationTargets?> = combine(
        operationsTargets,
        selectedOperationId
    ) { operationsTargets, selectedOperationId ->
        operationsTargets
            .firstOrNull { it.operation.id == selectedOperationId }
            .let { it ?: operationsTargets.firstOrNull() }
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    private val precedentTargets: StateFlow<List<OperationTargets>> = combine(
        operationsTargets,
        currentTargets
    ) { opsTargets, current ->
        if (current == null) return@combine emptyList()
        opsTargets.takeWhile { it.operation.id != current.operation.id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val selectedStepId = MutableStateFlow<Int?>(null)
    private val currentStep: StateFlow<Step?> = combine(
        steps,
        selectedStepId
    ) { steps, selectedStepId ->
        steps.firstOrNull { it.id == selectedStepId } ?: steps.firstOrNull()
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    private val stepOperations: StateFlow<List<OperationTargets>> = combine(
        operationsTargets,
        currentStep
    ) { operationsTargets, currentStep ->
        operationsTargets.filter { it.operation.stepId == currentStep?.id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val data: StateFlow<ARSessionData> = combine(
        markers,
        models,
        steps,
        operationsTargets,
        ::ARSessionData
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ARSessionData()
    )

    private val items: StateFlow<ARSessionItems> = combine(
        selectedMarker,
        selectedMarkerBitmap,
        currentStep,
        ::ARSessionItems
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ARSessionItems()
    )

    private val animation: StateFlow<ARSessionAnimation> = combine(
        precedentTargets,
        currentTargets,
        ::ARSessionAnimation
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ARSessionAnimation()
    )

    private val options = MutableStateFlow(ARSessionOptions())

    val notification = _notification.asSharedFlow()
    val permissions = _permissions.asStateFlow()
    val uiState: StateFlow<ARSessionUiState> = combine(
        data,
        items,
        animation,
        options,
        ARSessionUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ARSessionUiState.Loading
    )

    fun onEvent(event: ARSessionEvent) {
        when (event) {
            ARSessionEvent.OnCheckPermissionsStates -> onCheckPermissionsStates()
            is ARSessionEvent.OnPermissionInteraction -> onPermissionInteraction(event.permission)

            is ARSessionEvent.OnSelectMarker -> selectedMarker.update { event.marker }
            is ARSessionEvent.OnRepositionOrigin -> repositionOrigin(
                event.originNode,
                event.markerNode
            )

            is ARSessionEvent.OnSelectStep -> selectStep(event.stepId)
            ARSessionEvent.OnSelectNextStep -> selectNextStep()
            ARSessionEvent.OnSelectPreviousStep -> selectPreviousStep()

            is ARSessionEvent.OnSelectOperation -> selectOperation(event.operationId)
            ARSessionEvent.OnSelectNextOperation -> selectNextOperation()
            ARSessionEvent.OnSelectPreviousOperation -> selectPreviousOperation()

            ARSessionEvent.OnTogglePlay -> onTogglePlay()
            ARSessionEvent.OnToggleTorch -> onToggleTorch()
            ARSessionEvent.OnToggleLoop -> onToggleLooping()
            ARSessionEvent.OnToggleSpeed -> onToggleSpeed()
        }
    }

    private fun onCheckPermissionsStates() {
        this._permissions.update {
            permissionsManifestStrings.mapTo(mutableSetOf()) { string ->
                Permission(
                    manifestString = string,
                    state = permissionHandler.getPermissionState(string, true)
                )
            }
        }
    }

    private fun onPermissionInteraction(permission: ManifestString) {
        permissionHandler.onPermissionDialogInteraction(permission)
    }

    private fun repositionOrigin(originNode: OriginNode?, markerNode: MarkerNode?) {
        originNode?.repositionToMarker(markerNode, selectedMarker.value?.originOffsetTransform)
        val positionNumber = selectedMarker.value?.index ?: return
        emitNotification(
            NotificationEvent(
                message = R.string.notification_chip_message_origin_repositioned,
                args = listOf("M${machineId}P${positionNumber}")
            )
        )
    }

    private fun selectStep(stepId: Int?) {
        selectedStepId.value = stepId
    }

    private fun selectNextStep() {
        val current = currentStep.value ?: return
        val next = steps.value.next { it.id == current.id } ?: return

        if (current.id != next.id) {
            val firstOpInNext = operationsTargets.value
                .filter { it.operation.stepId == next.id }
                .minByOrNull { it.operation.order }

            selectStep(next.id)
            selectOperation(firstOpInNext?.operation?.id)
        }
    }

    private fun selectPreviousStep() {
        val current = currentStep.value ?: return
        val previous = steps.value.previous { it.id == current.id } ?: return

        if (current.id != previous.id) {
            val firstOpInPrev = operationsTargets.value
                .filter { it.operation.stepId == previous.id }
                .minByOrNull { it.operation.order }

            selectStep(previous.id)
            selectOperation(firstOpInPrev?.operation?.id)
        }
    }

    private fun selectOperation(operationId: Int?) {
        selectedOperationId.value = operationId
    }

    private fun selectNextOperation() {
        val current = currentTargets.value
        val lastInStep = stepOperations.value.maxByOrNull { it.operation.order }
        val isLast = lastInStep?.operation?.id == current?.operation?.id

        if (isLast) {
            selectNextStep()
        } else {
            val next = operationsTargets.value
                .next { it.operation.id == current?.operation?.id }
            selectOperation(next?.operation?.id)
        }
    }

    private fun selectPreviousOperation() {
        val current = currentTargets.value
        val firstInStep = stepOperations.value.minByOrNull { it.operation.order }
        val isFirst = firstInStep?.operation?.id == current?.operation?.id

        if (isFirst) {
            val current = currentStep.value ?: return
            val previous = steps.value.previous { it.id == current.id } ?: return

            if (current.id != previous.id) {
                val firstOpInPrev = operationsTargets.value
                    .filter { it.operation.stepId == previous.id }
                    .maxByOrNull { it.operation.order }

                selectStep(previous.id)
                selectOperation(firstOpInPrev?.operation?.id)
            }
        } else {
            val previous = operationsTargets.value
                .previous { it.operation.id == current?.operation?.id }
            selectOperation(previous?.operation?.id)
        }
    }

    private fun onTogglePlay() {
        options.update { it.copy(playing = !it.playing) }
        val (message, iconRes) = if (options.value.playing) {
            R.string.notification_chip_message_play to R.drawable.ic_play
        } else {
            R.string.notification_chip_message_pause to R.drawable.ic_pause
        }
        emitNotification(NotificationEvent(message = message, iconRes = iconRes))
    }

    private fun onToggleTorch() {
        options.update { it.copy(torchEnabled = !it.torchEnabled) }
        val (message, iconRes) = if (options.value.torchEnabled) {
            R.string.notification_chip_message_torch_enabled to R.drawable.ic_torch_filled
        } else {
            R.string.notification_chip_message_torch_disabled to R.drawable.ic_torch_outlined
        }
        emitNotification(NotificationEvent(message = message, iconRes = iconRes))
    }

    private fun onToggleLooping() {
        options.update { it.copy(looping = !it.looping) }
        val (message, iconRes) = if (options.value.looping) {
            R.string.notification_chip_message_loop_enabled to R.drawable.ic_loop
        } else {
            R.string.notification_chip_message_loop_disabled to R.drawable.ic_linear_start
        }
        emitNotification(NotificationEvent(message = message, iconRes = iconRes))
    }

    private fun onToggleSpeed() {
        val nextIndex = (options.value.speed.ordinal + 1) % Speed.entries.size
        options.update { it.copy(speed = Speed.entries[nextIndex]) }
        val (message, iconRes) = options.value.speed.notificationStringId to
                options.value.speed.notificationIconResId
        emitNotification(NotificationEvent(message = message, iconRes = iconRes))
    }

    private fun emitNotification(event: NotificationEvent) {
        viewModelScope.launch { _notification.emit(event) }
    }
}

sealed interface ARSessionEvent {
    data object OnCheckPermissionsStates : ARSessionEvent
    data class OnPermissionInteraction(val permission: String) : ARSessionEvent

    data class OnSelectMarker(val marker: Marker) : ARSessionEvent

    data class OnRepositionOrigin(
        val originNode: OriginNode?,
        val markerNode: MarkerNode?
    ) : ARSessionEvent

    data class OnSelectStep(val stepId: Int) : ARSessionEvent
    data object OnSelectNextStep : ARSessionEvent
    data object OnSelectPreviousStep : ARSessionEvent

    data class OnSelectOperation(val operationId: Int) : ARSessionEvent
    data object OnSelectNextOperation : ARSessionEvent
    data object OnSelectPreviousOperation : ARSessionEvent

    data object OnTogglePlay : ARSessionEvent
    data object OnToggleTorch : ARSessionEvent
    data object OnToggleLoop : ARSessionEvent
    data object OnToggleSpeed : ARSessionEvent
}

sealed interface ARSessionUiState {
    data object Loading : ARSessionUiState

    @Immutable
    data class Success(
        val data: ARSessionData = ARSessionData(),
        val items: ARSessionItems = ARSessionItems(),
        val animation: ARSessionAnimation = ARSessionAnimation(),
        val options: ARSessionOptions = ARSessionOptions()
    ) : ARSessionUiState
}

@Immutable
data class ARSessionData(
    val markers: List<Marker> = emptyList(),
    val models: List<Model> = emptyList(),
    val steps: List<Step> = emptyList(),
    val operationsTargets: List<OperationTargets> = emptyList(),
)

@Immutable
data class ARSessionItems(
    val selectedMarker: Marker? = null,
    val currentBitmap: BitmapInfo? = null,
    val currentStep: Step? = null,
)

@Immutable
data class ARSessionAnimation(
    val precedentTargets: List<OperationTargets> = emptyList(),
    val currentTargets: OperationTargets? = null
)

@Immutable
data class ARSessionOptions(
    val speed: Speed = Speed.NORMAL,
    val playing: Boolean = false,
    val looping: Boolean = true,
    val torchEnabled: Boolean = false,
)

@Immutable
data class NotificationEvent(
    val id: Long = System.currentTimeMillis(),
    @param:StringRes val message: Int? = null,
    @param:DrawableRes val iconRes: Int? = null,
    val args: List<Any> = emptyList()
)

private fun <T> List<T>.next(predicate: (T) -> Boolean): T? {
    val index = indexOfFirst(predicate)
    return if (index == -1) null else getOrNull((index + 1).coerceAtMost(lastIndex.coerceAtLeast(0)))
}

private fun <T> List<T>.previous(predicate: (T) -> Boolean): T? {
    val index = indexOfFirst(predicate)
    return if (index == -1) null else getOrNull((index - 1).coerceAtLeast(0))
}