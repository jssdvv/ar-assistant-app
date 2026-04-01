package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.CheckIcon
import com.jssdvv.ara.core.presentation.common.CloseIcon
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.type.OperationType
import com.jssdvv.ara.machines.presentation.destination.steps.Renderable
import com.jssdvv.ara.machines.presentation.destination.steps.RenderableState
import com.jssdvv.ara.machines.domain.utility.Axis
import com.jssdvv.ara.machines.domain.utility.AxisSelector
import com.jssdvv.ara.machines.domain.utility.extractSingleAxisDegrees
import com.jssdvv.ara.machines.domain.utility.rememberMultiRotationState
import com.jssdvv.ara.machines.domain.utility.rememberMultiTranslationState
import com.jssdvv.ara.machines.domain.utility.rememberRotationState
import com.jssdvv.ara.machines.domain.utility.rememberTranslationState
import com.jssdvv.ara.machines.domain.utility.unidirectionalRotation
import com.jssdvv.ara.machines.domain.utility.unidirectionalTransformPair
import com.jssdvv.ara.machines.domain.utility.unidirectionalTranslation
import dev.romainguy.kotlin.math.max
import kotlinx.coroutines.flow.distinctUntilChanged

sealed interface BottomSheetScreen {
    data object Main : BottomSheetScreen
    data object Entities : BottomSheetScreen
    data object Operations : BottomSheetScreen
}

@Composable
fun OperationBottomSheet(
    isVisible: Boolean,
    selectedRenderablesStates: Map<Renderable, RenderableState>,
    onUnselectItem: (Renderable) -> Unit,
    onSelectionChange: (isActive: Boolean) -> Unit,
    onSaveClick: (Operation) -> Unit,
    onCancelClick: () -> Unit,
    selectedOperation: Operation,
    onOperationChange: (Operation) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentSheetScreen by remember { mutableStateOf<BottomSheetScreen>(BottomSheetScreen.Main) }

    val interactionSource = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }
    val focus = interactionSource.collectIsFocusedAsState().value
    val focus2 = interactionSource2.collectIsFocusedAsState().value

    LaunchedEffect(focus, focus2) {
        onSelectionChange(focus)
        if (focus2) currentSheetScreen = BottomSheetScreen.Operations
    }

    DraggableBottomSheet(
        isVisible = isVisible,
        modifier = modifier,
        header = {
            AnimatedContent(
                targetState = currentSheetScreen,
                transitionSpec = {
                    if (targetState !is BottomSheetScreen.Main) {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    } else {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    }
                }
            ) { screen ->
                when (screen) {
                    is BottomSheetScreen.Main -> {
                        BottomSheetMainHeader(
                            title = "Edit Operation",
                            onSaveClick = { onSaveClick(selectedOperation) },
                            onCancelClick = onCancelClick
                        )
                    }

                    is BottomSheetScreen.Operations -> {
                        BottomSheetSubHeader(
                            title = "Selected operation",
                            onNavigateUp = { currentSheetScreen = BottomSheetScreen.Main }
                        )
                    }

                    else -> {
                        BottomSheetSubHeader(
                            title = "Selected entities",
                            onNavigateUp = { currentSheetScreen = BottomSheetScreen.Main }
                        )
                    }
                }
            }
        },
    ) {
        AnimatedContent(
            targetState = currentSheetScreen,
            transitionSpec = {
                if (targetState !is BottomSheetScreen.Main) {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                } else {
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                }
            }
        ) { currentScreen ->
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.medium)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (currentScreen) {
                    BottomSheetScreen.Main -> {
                        OutlinedTextField(
                            value = selectedOperation.title,
                            onValueChange = {
                                onOperationChange(selectedOperation.copy(title = it))
                            },
                            label = { Text("Operation name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        EntitiesTextField(
                            selectedItemsCount = selectedRenderablesStates.count(),
                            onClick = { currentSheetScreen = BottomSheetScreen.Entities },
                            modifier = Modifier.fillMaxWidth(),
                            interactionSource = interactionSource
                        )
                        OperationsTextField(
                            currentOperationType = selectedOperation.type,
                            onClick = { currentSheetScreen = BottomSheetScreen.Operations },
                            modifier = Modifier.fillMaxWidth(),
                            interactionSource = interactionSource2
                        )
                        when (selectedOperation.type) {
                            OperationType.POINT_TO_POINT -> FreeOperationSettings(
                                currentOperation = selectedOperation,
                                onOperationChange = onOperationChange
                            )

                            OperationType.SCREW -> ScrewOperationSettings(
                                currentOperation = selectedOperation,
                                onOperationChange = onOperationChange
                            )

                            OperationType.CYLINDRICAL -> CylindricalOperationSettings(
                                currentOperation = selectedOperation,
                                onOperationChange = onOperationChange
                            )

                            OperationType.JOINT -> JointOperationSettings(
                                currentOperation = selectedOperation,
                                onOperationChange = onOperationChange
                            )

                            else -> VisibilityOperationSettings()
                        }
                    }

                    is BottomSheetScreen.Entities -> {
                        selectedRenderablesStates.entries.forEachIndexed { index, (renderable, state) ->
                            if (index > 0) HorizontalDivider()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_renderable),
                                    contentDescription = null
                                )
                                Text(
                                    text = state.name,
                                    modifier = Modifier.weight(1F)
                                )
                                IconButton(
                                    onClick = { onUnselectItem(renderable) },
                                    content = { CloseIcon() }
                                )
                            }
                        }
                    }

                    is BottomSheetScreen.Operations -> {
                        OperationType.entries.forEachIndexed { index, operationType ->
                            if (index > 0) HorizontalDivider()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clickable {
                                        onOperationChange(selectedOperation.copy(type = operationType))
                                        currentSheetScreen = BottomSheetScreen.Main
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Icon(
                                    painter = painterResource(operationType.iconResId),
                                    contentDescription = null
                                )
                                Text(
                                    text = stringResource(operationType.labelResId),
                                    modifier = Modifier.weight(1F)
                                )
                                if (operationType == selectedOperation.type) CheckIcon()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FreeOperationSettings(
    currentOperation: Operation,
    onOperationChange: (Operation) -> Unit,
) {
    val multiTranslationState = rememberMultiTranslationState(
        initialXMeters = currentOperation.offsetPosition.x,
        initialYMeters = currentOperation.offsetPosition.y,
        initialZMeters = currentOperation.offsetPosition.z
    )
    val multiRotationState = rememberMultiRotationState(
        initialEulerDegrees = currentOperation.offsetQuaternion.toEulerAngles()
    )
    val updatedCurrentOperation = rememberUpdatedState(currentOperation)

    LaunchedEffect(Unit) {
        snapshotFlow { multiTranslationState.position to multiRotationState.quaternion }
            .distinctUntilChanged()
            .collect { (position, rotation) ->
                onOperationChange(
                    updatedCurrentOperation.value.copy(
                        offsetPosition = position,
                        offsetQuaternion = rotation
                    )
                )
            }
    }

    HorizontalDivider()

    listOf(
        Axis.X to multiTranslationState.x,
        Axis.Y to multiTranslationState.y,
        Axis.Z to multiTranslationState.z
    ).forEach { (axis, state) ->
        TranslationTextField(
            name = axis.name,
            state = state,
            modifier = Modifier.fillMaxWidth()
        )
    }

    HorizontalDivider()

    listOf(
        Axis.X to multiRotationState.x,
        Axis.Y to multiRotationState.y,
        Axis.Z to multiRotationState.z
    ).forEach { (axis, state) ->
        RotationTextField(
            name = stringResource(axis.rotationNameId),
            state = state,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ScrewOperationSettings(
    currentOperation: Operation,
    onOperationChange: (Operation) -> Unit,
) {
    var selectedAxis by remember { mutableStateOf(Axis.fromVector(currentOperation.axis)) }
    val translationState = rememberTranslationState(
        initialMeters = max(currentOperation.offsetPosition),
    )
    val pitchOrTurnsState = rememberTranslationState(
        initialMeters = currentOperation.screwPitch
    )
    var useTurns by remember { mutableStateOf(false) }
    val updatedUseTurns by rememberUpdatedState(useTurns)
    val updatedCurrentOperation by rememberUpdatedState(currentOperation)

    LaunchedEffect(Unit) {
        snapshotFlow { translationState.meters to pitchOrTurnsState }
            .distinctUntilChanged()
            .collect { (distance, pitchOrTurnsState) ->
                val (turns, pitch) = if (updatedUseTurns) {
                    pitchOrTurnsState.numeric to
                            (if (pitchOrTurnsState.numeric != 0F) distance / pitchOrTurnsState.numeric else 0F)
                } else {
                    (if (pitchOrTurnsState.meters != 0F) distance / pitchOrTurnsState.meters else 0F) to
                            pitchOrTurnsState.meters
                }

                onOperationChange(
                    updatedCurrentOperation.copy(
                        offsetPosition = unidirectionalTranslation(selectedAxis, distance),
                        offsetQuaternion = unidirectionalRotation(selectedAxis, turns * 360F),
                        screwPitch = pitch,
                        axis = selectedAxis.unitVector
                    )
                )
            }
    }

    AxisSelector(
        axis = selectedAxis,
        onAxisChange = { axis ->
            selectedAxis = axis
            val (newPosition, newRotation) = unidirectionalTransformPair(
                axis = axis,
                translationUnits = translationState.meters,
                rotationUnits = if (pitchOrTurnsState.meters != 0F) {
                    translationState.meters / pitchOrTurnsState.meters * 360F
                } else 0F
            )
            onOperationChange(
                updatedCurrentOperation.copy(
                    offsetPosition = newPosition,
                    offsetQuaternion = newRotation,
                    axis = axis.unitVector
                )
            )
        },
        modifier = Modifier.fillMaxWidth()
    )

    TranslationTextField(
        name = selectedAxis.name,
        state = translationState,
        modifier = Modifier.fillMaxWidth()
    )

    PitchTextField(
        name = if (useTurns) "Turns" else "Pitch",
        state = pitchOrTurnsState,
        isPitch = !useTurns,
        modifier = Modifier.fillMaxWidth()
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = useTurns,
            onCheckedChange = { useTurns = it }
        )
        Text(stringResource(R.string.operation_checkbox_use_turns_supporting_text))
    }
}

@Composable
fun CylindricalOperationSettings(
    currentOperation: Operation,
    onOperationChange: (Operation) -> Unit,
) {
    var selectedAxis by remember { mutableStateOf(Axis.fromVector(currentOperation.axis)) }
    val translationState = rememberTranslationState(
        initialMeters = max(currentOperation.offsetPosition)
    )
    val rotationState = rememberRotationState(
        initialDegrees = currentOperation.offsetQuaternion.extractSingleAxisDegrees(selectedAxis)
    )
    val updatedCurrentOperation by rememberUpdatedState(currentOperation)

    LaunchedEffect(Unit) {
        snapshotFlow { translationState.meters to rotationState.degrees }
            .distinctUntilChanged()
            .collect { (meters, degrees) ->
                onOperationChange(
                    updatedCurrentOperation.copy(
                        offsetPosition = unidirectionalTranslation(selectedAxis, meters),
                        offsetQuaternion = unidirectionalRotation(selectedAxis, degrees),
                        axis = selectedAxis.unitVector
                    )
                )
            }
    }

    AxisSelector(
        axis = selectedAxis,
        onAxisChange = { axis ->
            selectedAxis = axis
            val (newPosition, newRotation) = unidirectionalTransformPair(
                axis = axis,
                translationUnits = translationState.meters,
                rotationUnits = rotationState.degrees,
            )
            onOperationChange(
                updatedCurrentOperation.copy(
                    offsetPosition = newPosition,
                    offsetQuaternion = newRotation,
                    axis = axis.unitVector
                )
            )
        },
        modifier = Modifier.fillMaxWidth()
    )

    TranslationTextField(
        name = selectedAxis.name,
        state = translationState,
        modifier = Modifier.fillMaxWidth()
    )

    RotationTextField(
        name = stringResource(selectedAxis.rotationNameId),
        state = rotationState,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun JointOperationSettings(
    currentOperation: Operation,
    onOperationChange: (Operation) -> Unit,
) {
    val multiRotationState = rememberMultiRotationState(
        initialEulerDegrees = currentOperation.offsetQuaternion.toEulerAngles()
    )
    val updatedCurrentOperation by rememberUpdatedState(currentOperation)

    LaunchedEffect(Unit) {
        snapshotFlow { multiRotationState.quaternion }
            .distinctUntilChanged()
            .collect { quaternion ->
                onOperationChange(
                    updatedCurrentOperation.copy(
                        offsetQuaternion = quaternion
                    )
                )
            }
    }

    listOf(
        Axis.X to multiRotationState.x,
        Axis.Y to multiRotationState.y,
        Axis.Z to multiRotationState.z
    ).forEach { (axis, state) ->
        RotationTextField(
            name = axis.name,
            state = state,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// todo fix this implementation
@Composable
fun VisibilityOperationSettings(
    modifier: Modifier = Modifier
) {
    var alpha by remember { mutableStateOf(1F) }

    Text("Transparency = ${(alpha * 100).toInt()}%")

    Slider(
        value = alpha,
        onValueChange = { alpha = it },
        steps = 10
    )
}