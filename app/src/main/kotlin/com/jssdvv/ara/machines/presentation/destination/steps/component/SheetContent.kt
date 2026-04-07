package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.mutableFloatStateOf
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
import com.jssdvv.ara.machines.presentation.destination.steps.RenderableState
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.type.Measurement
import com.jssdvv.ara.machines.domain.utility.RenderableInfo
import com.jssdvv.ara.machines.domain.utility.extractSingleAxisDegrees
import com.jssdvv.ara.machines.domain.utility.rememberMultiRotationState
import com.jssdvv.ara.machines.domain.utility.rememberMultiTranslationState
import com.jssdvv.ara.machines.domain.utility.rememberSingleRotationState
import com.jssdvv.ara.machines.domain.utility.rememberSingleTranslationState
import com.jssdvv.ara.machines.domain.utility.rememberTimeState
import com.jssdvv.ara.machines.domain.utility.unidirectionalRotation
import com.jssdvv.ara.machines.domain.utility.unidirectionalTransformPair
import com.jssdvv.ara.machines.domain.utility.unidirectionalTranslation
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.max
import io.github.sceneview.math.Position
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.roundToInt

enum class BottomSheetScreen { MAIN, ENTITIES, OPERATIONS }

@Composable
fun OperationBottomSheet(
    isVisible: Boolean,
    selectedRenderablesStates: Map<RenderableInfo, RenderableState>,
    onUnselectItem: (RenderableInfo) -> Unit,
    onSelectionChange: (isActive: Boolean) -> Unit,
    onSaveClick: (Operation) -> Unit,
    onCancelClick: () -> Unit,
    selectedOperation: Operation,
    onOperationChange: (Operation) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentSheetScreen by remember { mutableStateOf(BottomSheetScreen.MAIN) }
    val lazyListState = rememberLazyListState()

    val interactionSource = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }
    val focus = interactionSource.collectIsFocusedAsState().value
    val focus2 = interactionSource2.collectIsFocusedAsState().value

    LaunchedEffect(focus, focus2) {
        if(currentSheetScreen == BottomSheetScreen.MAIN) {
            onSelectionChange(focus)
        }
        if (focus2) currentSheetScreen = BottomSheetScreen.OPERATIONS
    }

    LaunchedEffect(currentSheetScreen) {
        onSelectionChange(currentSheetScreen == BottomSheetScreen.ENTITIES)
    }

    DraggableBottomSheet(
        isVisible = isVisible,
        modifier = modifier,
        header = {
            AnimatedContent(
                targetState = currentSheetScreen,
                transitionSpec = {
                    if (targetState != BottomSheetScreen.MAIN) {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    } else {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    }
                }
            ) { screen ->
                when (screen) {
                    BottomSheetScreen.MAIN -> {
                        BottomSheetMainHeader(
                            title = "Edit Operation",
                            onSaveClick = { onSaveClick(selectedOperation) },
                            onCancelClick = onCancelClick
                        )
                    }

                    BottomSheetScreen.OPERATIONS -> {
                        BottomSheetSubHeader(
                            title = "Selected operation",
                            onNavigateUp = { currentSheetScreen = BottomSheetScreen.MAIN }
                        )
                    }

                    BottomSheetScreen.ENTITIES -> {
                        BottomSheetSubHeader(
                            title = "Selected entities",
                            onNavigateUp = { currentSheetScreen = BottomSheetScreen.MAIN }
                        )
                    }
                }
            }
        },
    ) {
        AnimatedContent(
            targetState = currentSheetScreen,
            transitionSpec = {
                if (targetState != BottomSheetScreen.MAIN) {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                } else {
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                }
            }
        ) { currentScreen ->

            val settingsContent = when (selectedOperation.type) {
                OperationType.POINT_TO_POINT -> pointToPointSettings(
                    selectedOperation,
                    onOperationChange
                )

                OperationType.SCREW -> screwSettings(selectedOperation, onOperationChange)
                OperationType.CYLINDRICAL -> cylindricalSettings(
                    selectedOperation,
                    onOperationChange
                )

                OperationType.JOINT -> jointSettings(selectedOperation, onOperationChange)
                else -> visibilitySettings(selectedOperation, onOperationChange)
            }

            val lazyListContent = when (currentScreen) {
                BottomSheetScreen.MAIN -> {
                    stepsMainBottomScreen(
                        operation = selectedOperation,
                        onOperationChange = onOperationChange,
                        onNavigateToBottomScreen = { currentSheetScreen = it },
                        selectedItemsCount = selectedRenderablesStates.count(),
                        entitiesInteractionSource = interactionSource,
                        operationsInteractionSource = interactionSource2,
                        settingsContent = settingsContent
                    )

                }

                BottomSheetScreen.ENTITIES -> {
                    stepsOpSelectionBottomScreen(
                        selectedRenderablesStates = selectedRenderablesStates,
                        onUnselectItem = onUnselectItem
                    )
                }

                BottomSheetScreen.OPERATIONS -> {
                    stepsEntitiesBottomScreen(
                        operation = selectedOperation,
                        onOperationChange = onOperationChange,
                        onNavigateToBottomScreen = { currentSheetScreen = it }
                    )
                }
            }

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.medium)
                    .imePadding(),
                state = lazyListState,
                contentPadding = PaddingValues(vertical = MaterialTheme.spacing.small),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                lazyListContent()
            }
        }
    }
}

@Composable
fun stepsMainBottomScreen(
    operation: Operation,
    onOperationChange: (Operation) -> Unit,
    onNavigateToBottomScreen: (BottomSheetScreen) -> Unit,
    selectedItemsCount: Int,
    entitiesInteractionSource: MutableInteractionSource,
    operationsInteractionSource: MutableInteractionSource,
    settingsContent: LazyListScope.() -> Unit
): LazyListScope.() -> Unit {

    var isGlobalOffset by remember { mutableStateOf(false) }
    val delayState = rememberTimeState(operation.delay)
    val durationState = rememberTimeState(operation.duration)

    val updatedCurrentOperation = rememberUpdatedState(operation)

    LaunchedEffect(Unit) {
        snapshotFlow { delayState.seconds to durationState.seconds }
            .distinctUntilChanged()
            .collect { (delay, duration) ->
                onOperationChange(
                    updatedCurrentOperation.value.copy(
                        delay = delay,
                        duration = duration
                    )
                )
            }
    }

    return {
        item {
            OutlinedTextField(
                value = operation.title,
                onValueChange = {
                    onOperationChange(operation.copy(title = it))
                },
                label = { Text("Operation name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            EntitiesTextField(
                selectedItemsCount = selectedItemsCount,
                onClick = { onNavigateToBottomScreen(BottomSheetScreen.ENTITIES) },
                modifier = Modifier.fillMaxWidth(),
                interactionSource = entitiesInteractionSource
            )
        }
        item {
            OperationsTextField(
                currentOperationType = operation.type,
                onClick = { onNavigateToBottomScreen(BottomSheetScreen.OPERATIONS) },
                modifier = Modifier.fillMaxWidth(),
                interactionSource = operationsInteractionSource
            )
        }
        item {
            TimeTextField(
                name = "delay",
                state = delayState,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            TimeTextField(
                name = "duration",
                state = durationState,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (operation.type != OperationType.VISIBILITY) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isGlobalOffset,
                        onCheckedChange = {
                            isGlobalOffset = it
                            onOperationChange(
                                operation.copy(
                                    isGlobal = it
                                )
                            )
                        }
                    )
                    Text("Movel en coordenadas globales")
                }
            }
        }
        settingsContent(this)
    }
}

@Composable
fun stepsOpSelectionBottomScreen(
    selectedRenderablesStates: Map<RenderableInfo, RenderableState>,
    onUnselectItem: (RenderableInfo) -> Unit,
): LazyListScope.() -> Unit {
    return {
        itemsIndexed(selectedRenderablesStates.entries.toList()) { index, (renderable, state) ->
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
}

@Composable
fun stepsEntitiesBottomScreen(
    operation: Operation,
    onOperationChange: (Operation) -> Unit,
    onNavigateToBottomScreen: (BottomSheetScreen) -> Unit
): LazyListScope.() -> Unit {
    return {
        itemsIndexed(OperationType.entries) { index, operationType ->
            if (index > 0) HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable {
                        onOperationChange(operation.copy(type = operationType))
                        onNavigateToBottomScreen(BottomSheetScreen.MAIN)
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
                if (operationType == operation.type) CheckIcon()
            }
        }
    }
}

@Composable
fun pointToPointSettings(
    currentOperation: Operation,
    onOperationChange: (Operation) -> Unit,
): LazyListScope.() -> Unit {
    val multiTranslationState = rememberMultiTranslationState(
        initialXMeters = currentOperation.offsetPosition.x,
        initialYMeters = currentOperation.offsetPosition.y,
        initialZMeters = currentOperation.offsetPosition.z
    )
    val multiRotationState = rememberMultiRotationState(
        initialEulerDegrees = currentOperation.offsetRotation.toEulerAngles()
    )
    val updatedCurrentOperation = rememberUpdatedState(currentOperation)

    LaunchedEffect(Unit) {
        snapshotFlow { multiTranslationState.position to multiRotationState.quaternion }
            .distinctUntilChanged()
            .collect { (position, rotation) ->
                onOperationChange(
                    updatedCurrentOperation.value.copy(
                        offsetPosition = position,
                        offsetRotation = rotation
                    )
                )
            }
    }

    return {
        item { HorizontalDivider() }
        items(
            listOf(
                Axis.X to multiTranslationState.x,
                Axis.Y to multiTranslationState.y,
                Axis.Z to multiTranslationState.z
            )
        ) { (axis, state) ->
            TranslationTextField(
                name = axis.name,
                state = state,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item { HorizontalDivider() }
        items(
            listOf(
                Axis.X to multiRotationState.x,
                Axis.Y to multiRotationState.y,
                Axis.Z to multiRotationState.z
            )
        ) { (axis, state) ->
            RotationTextField(
                name = stringResource(axis.rotationNameId),
                state = state,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun screwSettings(
    operation: Operation,
    onOperationChange: (Operation) -> Unit,
): LazyListScope.() -> Unit {

    var selectedAxis by remember { mutableStateOf(operation.axis) }
    var useTurns by remember { mutableStateOf(false) }

    val translationState =
        rememberSingleTranslationState(initialMeters = max(operation.offsetPosition))
    val pitchState = rememberSingleTranslationState(initialMeters = operation.pitch)
    val turnsState = rememberSingleTranslationState(
        initialMeters = operation.turns,
        initialMeasurement = Measurement.METERS
    )

    fun updateFromPitch() {
        val pitch = pitchState.meters
        if (pitch != 0f) {
            val calculatedTurns = translationState.meters / pitch
            turnsState.updateUnits(calculatedTurns.toString())
        } else {
            turnsState.updateUnits("0")
        }
    }

    fun updateFromTurns() {
        val turns = turnsState.numeric
        if (turns != 0f) {
            val calculatedPitchMeters = translationState.meters / turns
            pitchState.updateMeters(calculatedPitchMeters)
        } else {
            pitchState.updateUnits("0")
        }
    }

    LaunchedEffect(translationState.meters, pitchState.meters, turnsState.numeric, selectedAxis) {
        val turns = turnsState.numeric
        val pitch = pitchState.meters
        val distance = translationState.meters

        onOperationChange(
            operation.copy(
                offsetPosition = unidirectionalTranslation(selectedAxis, distance),
                offsetRotation = unidirectionalRotation(selectedAxis, turns * 360F),
                pitch = pitch,
                turns = turns,
                axis = selectedAxis
            )
        )
    }

    return {
        item {
            AxisSelector(
                axis = selectedAxis,
                onAxisChange = { selectedAxis = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            TranslationTextField(
                name = selectedAxis.name,
                state = translationState,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            PitchTextField(
                name = if (useTurns) "Turns" else "Pitch", // TODO create strings
                state = if (useTurns) turnsState else pitchState,
                onValueChange = { units ->
                    if (useTurns) {
                        turnsState.updateUnits(units)
                        updateFromTurns()
                    } else {
                        pitchState.updateUnits(units)
                        updateFromPitch()
                    }
                },
                isPitch = !useTurns,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = useTurns,
                    onCheckedChange = { checked ->
                        useTurns = checked
                        if (checked) updateFromPitch() else updateFromTurns()
                    }
                )
                Text(stringResource(R.string.operation_checkbox_use_turns_supporting_text))
            }
        }
    }
}

@Composable
fun cylindricalSettings(
    operation: Operation,
    onOperationChange: (Operation) -> Unit,
): LazyListScope.() -> Unit {
    var selectedAxis by remember { mutableStateOf(operation.axis) }
    val translationState = rememberSingleTranslationState(
        initialMeters = max(operation.offsetPosition)
    )
    val rotationState = rememberSingleRotationState(
        initialDegrees = operation.offsetRotation.extractSingleAxisDegrees(selectedAxis)
    )
    val updatedCurrentOperation by rememberUpdatedState(operation)

    LaunchedEffect(Unit) {
        snapshotFlow { translationState.meters to rotationState.degrees }
            .distinctUntilChanged()
            .collect { (meters, degrees) ->
                onOperationChange(
                    updatedCurrentOperation.copy(
                        offsetPosition = unidirectionalTranslation(selectedAxis, meters),
                        offsetRotation = unidirectionalRotation(selectedAxis, degrees),
                        axis = selectedAxis
                    )
                )
            }
    }

    return {
        item {
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
                            offsetRotation = newRotation,
                            axis = axis
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            TranslationTextField(
                name = selectedAxis.name,
                state = translationState,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            RotationTextField(
                name = stringResource(selectedAxis.rotationNameId),
                state = rotationState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun jointSettings(
    operation: Operation,
    onOperationChange: (Operation) -> Unit,
): LazyListScope.() -> Unit {
    val multiRotationState = rememberMultiRotationState(
        initialEulerDegrees = operation.offsetRotation.toEulerAngles()
    )
    val updatedCurrentOperation by rememberUpdatedState(operation)

    LaunchedEffect(Unit) {
        snapshotFlow { multiRotationState.quaternion }
            .distinctUntilChanged()
            .collect { quaternion ->
                onOperationChange(
                    updatedCurrentOperation.copy(
                        offsetRotation = quaternion
                    )
                )
            }
    }

    return {
        items(
            listOf(
                Axis.X to multiRotationState.x,
                Axis.Y to multiRotationState.y,
                Axis.Z to multiRotationState.z
            )
        ) { (axis, state) ->
            RotationTextField(
                name = axis.name,
                state = state,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun visibilitySettings(
    operation: Operation,
    onOperationChange: (Operation) -> Unit
): LazyListScope.() -> Unit {
    val steps = 4
    var alphaPercent by remember { mutableFloatStateOf(operation.alpha * 100F) }

    return {
        item {
            Text(
                text = "Transparency = ${alphaPercent.roundToInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.spacing.extraSmall,
                    vertical = MaterialTheme.spacing.small
                )
            )
        }
        item {
            Slider(
                value = alphaPercent,
                onValueChange = {
                    alphaPercent = it
                    onOperationChange(
                        operation.copy(
                            alpha = (it / 100F).coerceIn(0F..1F),
                            offsetPosition = Position(),
                            offsetRotation = Quaternion()
                        )
                    )
                },
                valueRange = 0F..100F,
                steps = steps - 1
            )
        }
    }
}