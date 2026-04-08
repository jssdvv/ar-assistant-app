package com.jssdvv.ara.machines.presentation.destination.ar_session

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.repository.FilesManager
import com.jssdvv.ara.core.domain.repository.PermissionHandler
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.core.domain.utility.PermissionState
import com.jssdvv.ara.machines.domain.model.Marker
import com.jssdvv.ara.machines.domain.model.Model
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.usecase.MarkersDataManager
import com.jssdvv.ara.machines.domain.usecase.ModelsDataManager
import com.jssdvv.ara.machines.domain.usecase.OperationDataManager
import com.jssdvv.ara.machines.domain.usecase.StepsDataManager
import com.jssdvv.ara.machines.domain.utility.RenderableInfo
import com.jssdvv.ara.machines.domain.utility.RestorableState
import com.jssdvv.ara.machines.presentation.destination.ar_session.component.Speed
import com.jssdvv.ara.machines.presentation.destination.calibration.BitmapInfo
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position
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
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.set

@HiltViewModel
class ARSessionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val permissionHandler: PermissionHandler,
    private val filesManager: FilesManager,
    private val markersDataManager: MarkersDataManager,
    private val modelsDataManager: ModelsDataManager,
    private val stepsDataManager: StepsDataManager,
    private val opsDataManager: OperationDataManager,
) : ViewModel() {

    companion object {
        val permissions = listOf(android.Manifest.permission.CAMERA)
    }

    val route = savedStateHandle.toRoute<MachinesGraph.ARSessionRoute>()
    val machineId = route.machineId
    val activityId = route.activityId

    // Permissions Pair<Permission string, Permission state>
    private val _notification = MutableSharedFlow<NotificationEvent>(extraBufferCapacity = 1)
    private val _permissionsStates = MutableStateFlow(emptyList<Pair<String, PermissionState>>())
    private val options = MutableStateFlow(ARSessionOptions())
    private val _renderableInfoStates = mutableStateMapOf<RenderableInfo, RenderableAnimationState>()

    private val currentStep = MutableStateFlow<Step?>(null)
    private val currentOperationTargets = MutableStateFlow<OperationTargets?>(null)

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
            val bitmap =
                filesManager.getBitmapFromInputStream(filesManager.getInputStreamFromUri(imageUri))
            bitmap?.let { BitmapInfo(id, it) }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            null
        )

    private val selectedOperationTargets: StateFlow<OperationTargets?> = combine(
        operationsTargets,
        currentOperationTargets,
    ) { opTargets, currentOpTargets ->
        val current = currentOpTargets?: opTargets.minByOrNull { it.operation.order }
        unselectAllRenderables()
        current?.targets?.forEach { target ->
            val info = RenderableInfo(target.modelId, target.xxh3)
            val currentState = _renderableInfoStates[info] ?: RenderableAnimationState()
            _renderableInfoStates[info] = currentState.copy(isSelectedToPlay = true)
        }
        current
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null
    )

    private val selectedStep: StateFlow<Step?> = combine(
        steps,
        selectedOperationTargets
    ) { steps, selectedOperationTargets ->
        val stepId =  selectedOperationTargets?.operation?.stepId
        val currentStep = steps.find { it.id == stepId }
        currentStep
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null
    )

    private val editorData: StateFlow<EditorData> = combine(
        markers,
        models,
        steps,
        operationsTargets,
        ::EditorData
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = EditorData()
    )

    init { onCheckPermissionsStates(permissions.map { it to true }) }

    val notification = _notification.asSharedFlow()
    val permissionsStates = _permissionsStates.asStateFlow()
    val renderableInfoStates: SnapshotStateMap<RenderableInfo, RenderableAnimationState>
        get() = _renderableInfoStates

    val uiState: StateFlow<ARSessionUiState> = combine(
        editorData,
        selectedMarker,
        selectedMarkerBitmap,
        selectedStep,
        selectedOperationTargets,
        options
    ) { args ->
        val data = args[0] as EditorData
        ARSessionUiState.Success(
            editorData = data,
            selectedMarker = args[1] as Marker?,
            selectedMarkerBitmap = args[2] as BitmapInfo?,
            selectedStep = args[3] as Step?,
            selectedOperationTargets = args[4] as OperationTargets?,
            options = args[5] as ARSessionOptions
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ARSessionUiState.Loading
    )

    fun onEvent(event: ARSessionEvent) {
        when (event) {
            is ARSessionEvent.OnCheckPermissionsStates -> onCheckPermissionsStates(event.permissions)
            is ARSessionEvent.OnPermissionInteraction -> onPermissionInteraction(event.permission)
            is ARSessionEvent.OnSelectMarker -> selectedMarker.update { event.marker }
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

    fun onExternalEvent(event: ARSessionExternalEvent) {
        when (event) {
            is ARSessionExternalEvent.OnLoadRenderables -> loadRenderables(event.infoStates)
            is ARSessionExternalEvent.OnPlayRenderable -> selectRenderable(event.renderableInfo)
            ARSessionExternalEvent.OnStopAllRenderables -> unselectAllRenderables()
            is ARSessionExternalEvent.OnToggleRenderableVisibility -> toggleRenderableVisibility(event.renderableInfo)
            ARSessionExternalEvent.OnHideAllRenderables -> hideAllRenderables()
            ARSessionExternalEvent.OnShowAllRenderables -> showAllRenderables()
        }
    }

    /**
     * This method checks the permissions states and updates the [_permissionsStates] value.
     *
     * @param [permissions] List of pairs of <Permission string, Permission state>
     */
    private fun onCheckPermissionsStates(
        permissions: List<Pair<String, Boolean>>,
    ) {
        _permissionsStates.value = permissions.map {
            it.first to permissionHandler.getPermissionState(it.first, it.second)
        }
    }

    private fun onPermissionInteraction(permission: String) =
        permissionHandler.onPermissionDialogInteraction(permission)

    private fun selectStep(stepId: Int?) {
        currentOperationTargets.value = operationsTargets.value
            .filter{ it.operation.stepId == stepId }
            .minByOrNull { it.operation.order }
    }

    private fun selectNextStep() {
        val currentStep = selectedStep.value
        val nextStep = steps.value.navigate(
            current = currentStep ?: return,
            selector = { it.order },
            offset = 1
        )

        val nextStepId = nextStep?.id
        val operationsTargets = operationsTargets.value
            .filter { it.operation.stepId == nextStepId }
            .sortedBy { it.operation.order }

        currentOperationTargets.value = operationsTargets
            .minByOrNull { it.operation.order }
    }

    private fun selectPreviousStep() {
        val currentStep = selectedStep
        val nextStep = steps.value.navigate(
            current = currentStep.value ?: return,
            selector = { it.order },
            offset = -1
        )

        val nextStepId = nextStep?.id
        val operationsTargets = operationsTargets.value
            .filter { it.operation.stepId == nextStepId }
            .sortedBy { it.operation.order }

        currentOperationTargets.value = operationsTargets
            .minByOrNull { it.operation.order }
    }

    private fun selectOperation(operationId: Int?) {
        currentOperationTargets.value = operationsTargets.value.find { it.operation.id == operationId }
    }

    private fun selectNextOperation() {
        currentOperationTargets.value = operationsTargets.value.navigate(
            current = selectedOperationTargets.value ?: return,
            selector = { it.operation.order },
            offset = 1
        )
    }

    private fun selectPreviousOperation() {
        currentOperationTargets.value = operationsTargets.value.navigate(
            current = selectedOperationTargets.value ?: return,
            selector = { it.operation.order },
            offset = -1
        )
    }

    private fun onTogglePlay() {
        options.update { it.copy(isPlaying = !it.isPlaying) }
        emitNotification(
            if (options.value.isPlaying) {
                NotificationEvent(
                    message = R.string.notification_chip_message_play,
                    iconRes = R.drawable.ic_play
                )
            } else {
                NotificationEvent(
                    message = R.string.notification_chip_message_pause,
                    iconRes = R.drawable.ic_pause
                )
            }
        )
    }

    private fun onToggleTorch() {
        options.update{ it.copy(isTorchEnabled = !it.isTorchEnabled) }
        emitNotification(
            if (options.value.isTorchEnabled) {
                NotificationEvent(
                    message = R.string.notification_chip_message_torch_enabled,
                    iconRes = R.drawable.ic_torch_filled
                )
            } else {
                NotificationEvent(
                    message = R.string.notification_chip_message_torch_disabled,
                    iconRes = R.drawable.ic_torch_outlined
                )
            }
        )
    }

    private fun onToggleLooping() {
        options.update { it.copy(isLoopingEnabled = !it.isLoopingEnabled) }
        emitNotification(
            if (options.value.isLoopingEnabled) {
                NotificationEvent(
                    message = R.string.notification_chip_message_loop_enabled,
                    iconRes = R.drawable.ic_loop
                )
            } else {
                NotificationEvent(
                    message = R.string.notification_chip_message_loop_disabled,
                    iconRes = R.drawable.ic_linear_start
                )
            }
        )
    }

    private fun onToggleSpeed() {
        val nextIndex = (options.value.currentSpeed.ordinal + 1) % Speed.entries.size
        options.update { it.copy(currentSpeed = Speed.entries[nextIndex]) }
        emitNotification(
            when (options.value.currentSpeed) {
                Speed.HALF -> NotificationEvent(
                    message = R.string.notification_chip_message_speed_half,
                    iconRes = R.drawable.ic_slow_motion
                )

                Speed.NORMAL -> NotificationEvent(
                    message = R.string.notification_chip_message_speed_normal,
                    iconRes = R.drawable.ic_play
                )

                Speed.DOUBLE -> NotificationEvent(
                    message = R.string.notification_chip_message_speed_double,
                    iconRes = R.drawable.ic_fast_forward
                )
            }
        )
    }

    private fun emitNotification(event: NotificationEvent) {
        viewModelScope.launch {
            _notification.emit(event)
        }
    }

    private fun loadRenderables(renderables: Map<RenderableInfo, RenderableAnimationState>) {
        renderables.forEach { (info, state) ->
            val currentState = _renderableInfoStates[info] ?: RenderableAnimationState()
            _renderableInfoStates[info] = currentState.copy(
                name = state.name,
                index = state.index,
                initialPosition = state.initialPosition,
                initialQuaternion = state.initialQuaternion
            )
        }
    }

    private fun selectRenderable(vararg renderableInfo: RenderableInfo) {
        renderableInfo.forEach {
            val currentState = _renderableInfoStates[it] ?: RenderableAnimationState()
            _renderableInfoStates[it] = currentState.copy(isSelectedToPlay = true)
        }
    }

    private fun toggleRenderableVisibility(renderableInfo: RenderableInfo) {
        val currentState = _renderableInfoStates[renderableInfo] ?: RenderableAnimationState()
        _renderableInfoStates[renderableInfo] = currentState.copy(isVisible = !currentState.isVisible)
    }

    private fun unselectAllRenderables() {
        _renderableInfoStates.putAll(_renderableInfoStates.mapValues { it.value.copy(isSelectedToPlay = false) })
    }

    private fun showAllRenderables() {
        _renderableInfoStates.putAll(_renderableInfoStates.mapValues { it.value.copy(isVisible = true) })
    }

    private fun hideAllRenderables() {
        _renderableInfoStates.putAll(_renderableInfoStates.mapValues { it.value.copy(isVisible = false) })
    }
}

sealed class ARSessionEvent {
    data class OnCheckPermissionsStates(val permissions: List<Pair<String, Boolean>>) :
        ARSessionEvent()

    data class OnPermissionInteraction(val permission: String) : ARSessionEvent()
    data class OnSelectMarker(val marker: Marker) : ARSessionEvent()
    data class OnSelectStep(val stepId: Int) : ARSessionEvent()
    data object OnSelectNextStep : ARSessionEvent()
    data object OnSelectPreviousStep : ARSessionEvent()
    data class OnSelectOperation(val operationId: Int) : ARSessionEvent()
    data object OnSelectNextOperation : ARSessionEvent()
    data object OnSelectPreviousOperation : ARSessionEvent()
    data object OnTogglePlay : ARSessionEvent()
    data object OnToggleTorch : ARSessionEvent()
    data object OnToggleLoop : ARSessionEvent()
    data object OnToggleSpeed : ARSessionEvent()
}

sealed interface ARSessionExternalEvent {
    data class OnLoadRenderables(
        val infoStates: Map<RenderableInfo, RenderableAnimationState>
    ) : ARSessionExternalEvent

    data class OnPlayRenderable(
        val renderableInfo: RenderableInfo
    ) : ARSessionExternalEvent

    data object OnStopAllRenderables : ARSessionExternalEvent
    data class OnToggleRenderableVisibility(
        val renderableInfo: RenderableInfo
    ): ARSessionExternalEvent

    data object OnHideAllRenderables : ARSessionExternalEvent
    data object OnShowAllRenderables : ARSessionExternalEvent
}

sealed interface ARSessionUiState {

    data object Loading : ARSessionUiState

    data class Success(
        val editorData: EditorData = EditorData(),
        val selectedMarker: Marker? = null,
        val selectedMarkerBitmap: BitmapInfo? = null,
        val selectedStep: Step? = null,
        val selectedOperationTargets: OperationTargets? = null,
        val options: ARSessionOptions = ARSessionOptions()
    ) : ARSessionUiState
}

data class EditorData(
    val markers: List<Marker> = emptyList(),
    val models: List<Model> = emptyList(),
    val steps: List<Step> = emptyList(),
    val operationsTargets: List<OperationTargets> = emptyList(),
)

data class ARSessionOptions(
    val isPlaying: Boolean = false,
    val isTorchEnabled: Boolean = false,
    val currentSpeed: Speed = Speed.NORMAL,
    val isLoopingEnabled: Boolean = true,
)

data class NotificationEvent(
    val id: Long = System.currentTimeMillis(),
    @param:StringRes val message: Int? = null,
    @param:DrawableRes val iconRes: Int? = null
)

data class RenderableAnimationState(
    val name: String = "",
    val isVisible: Boolean = true,
    val isSelectedToPlay: Boolean = false,
    override val index: Int = 0,
    override val initialPosition: Position = Position(),
    override val initialQuaternion: Quaternion = Quaternion()
) : RestorableState

private fun <T> List<T>.navigate(
    current: T,
    selector: (T) -> Int,
    offset: Int
): T? {
    val sorted = sortedBy(selector)
    val index = sorted.indexOf(current)
    if (index == -1) return null
    val clampedIndex = (index + offset).coerceIn(0, sorted.lastIndex)
    return sorted[clampedIndex]
}