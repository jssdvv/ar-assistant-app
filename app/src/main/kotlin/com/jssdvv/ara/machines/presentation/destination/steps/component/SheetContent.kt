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
import com.jssdvv.ara.core.presentation.common.component.CheckIcon
import com.jssdvv.ara.core.presentation.common.component.CloseIcon
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.Pivot
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.type.OperationType
import com.jssdvv.ara.machines.domain.type.measurement.Translation
import com.jssdvv.ara.machines.presentation.destination.steps.functions.extractSingleAxisDegrees
import com.jssdvv.ara.machines.presentation.destination.steps.functions.rememberMultiRotationState
import com.jssdvv.ara.machines.presentation.destination.steps.functions.rememberMultiTranslationState
import com.jssdvv.ara.machines.presentation.destination.steps.functions.rememberSingleRotationState
import com.jssdvv.ara.machines.presentation.destination.steps.functions.rememberSingleTranslationState
import com.jssdvv.ara.machines.presentation.destination.steps.functions.rememberTimeState
import com.jssdvv.ara.machines.presentation.destination.steps.functions.unidirectionalRotation
import com.jssdvv.ara.machines.presentation.destination.steps.functions.unidirectionalTransformPair
import com.jssdvv.ara.machines.presentation.destination.steps.functions.unidirectionalTranslation
import com.jssdvv.ara.machines.presentation.sceneview.node.PivotNode
import dev.romainguy.kotlin.math.max
import io.github.sceneview.math.Transform
import io.github.sceneview.math.quaternion
import kotlinx.coroutines.flow.distinctUntilChanged

enum class BottomSheetScreen { MAIN, ENTITIES, OPERATIONS }

@Composable
fun OperationBottomSheet(
    visible: Boolean,
    selectedPivots: Set<PivotNode>,
    onUnselectPivot: (Pivot) -> Unit,
    onSelectionChange: (isActive: Boolean) -> Unit,
    editingOperation: Operation,
    onChangeEditingOperation: (Operation) -> Unit,
    onSaveEditingOperation: () -> Unit,
    onCancelEditingOperation: () -> Unit,
    onDeleteEditingOperation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentSheetScreen by remember { mutableStateOf(BottomSheetScreen.MAIN) }
    val lazyListState = rememberLazyListState()

    val interactionSource = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }
    val focus = interactionSource.collectIsFocusedAsState().value
    val focus2 = interactionSource2.collectIsFocusedAsState().value

    LaunchedEffect(focus, focus2) {
        if (currentSheetScreen == BottomSheetScreen.MAIN) {
            onSelectionChange(focus)
        }
        if (focus2) currentSheetScreen = BottomSheetScreen.OPERATIONS
    }

    LaunchedEffect(currentSheetScreen) {
        onSelectionChange(currentSheetScreen == BottomSheetScreen.ENTITIES)
    }

    DraggableBottomSheet(
        isVisible = visible,
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
                            onSaveClick = onSaveEditingOperation,
                            onCancelClick = onCancelEditingOperation
                        )
                    }

                    BottomSheetScreen.OPERATIONS -> {
                        BottomSheetSubHeader(
                            title = "Selected operation",
                            onNavigateBack = { currentSheetScreen = BottomSheetScreen.MAIN }
                        )
                    }

                    BottomSheetScreen.ENTITIES -> {
                        BottomSheetSubHeader(
                            title = "Selected entities",
                            onNavigateBack = { currentSheetScreen = BottomSheetScreen.MAIN }
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
            val settingsContent = when (editingOperation.type) {
                OperationType.POINT_TO_POINT -> pointToPointSettings(
                    editingOperation,
                    onChangeEditingOperation
                )

                OperationType.SCREW -> screwSettings(editingOperation, onChangeEditingOperation)
                OperationType.CYLINDRICAL -> cylindricalSettings(
                    editingOperation,
                    onChangeEditingOperation
                )

                else -> jointSettings(editingOperation, onChangeEditingOperation)
            }

            val lazyListContent = when (currentScreen) {
                BottomSheetScreen.MAIN -> stepsMainBottomScreen(
                    operation = editingOperation,
                    onChangeEditingOperation = onChangeEditingOperation,
                    onNavigateToBottomScreen = { currentSheetScreen = it },
                    selectedEntitiesCount = selectedPivots.size,
                    entitiesInteractionSource = interactionSource,
                    operationsInteractionSource = interactionSource2,
                    settingsContent = settingsContent
                )

                BottomSheetScreen.ENTITIES -> selectedPivotsBottomScreen(
                    selectedPivots = selectedPivots,
                    onUnselectItem = onUnselectPivot
                )

                BottomSheetScreen.OPERATIONS -> selectedOperationBottomScreen(
                    operation = editingOperation,
                    onOperationChange = onChangeEditingOperation,
                    onNavigateBack = { currentSheetScreen = BottomSheetScreen.MAIN }
                )
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
    selectedEntitiesCount: Int,
    onChangeEditingOperation: (Operation) -> Unit,
    entitiesInteractionSource: MutableInteractionSource,
    operationsInteractionSource: MutableInteractionSource,
    onNavigateToBottomScreen: (BottomSheetScreen) -> Unit,
    settingsContent: LazyListScope.() -> Unit
): LazyListScope.() -> Unit {
    val delayState = rememberTimeState(operation.delay)
    val durationState = rememberTimeState(operation.duration)
    val updatedCurrentOperation = rememberUpdatedState(operation)

    LaunchedEffect(Unit) {
        snapshotFlow { delayState.seconds to durationState.seconds }
            .distinctUntilChanged()
            .collect { (delay, duration) ->
                onChangeEditingOperation(
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
                onValueChange = { onChangeEditingOperation(operation.copy(title = it)) },
                label = { Text("Operation name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            EntitiesTextField(
                selectedItemsCount = selectedEntitiesCount,
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
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val (text, drawableId) = if (operation.global) {
                    "Orientación global" to R.drawable.ic_orientation_global
                } else {
                    "Orientación local" to R.drawable.ic_orientation_local
                }
                ButtonWithIcon(
                    onClick = { onChangeEditingOperation(operation.copy(global = !operation.global)) },
                    icon = {
                        Icon(
                            painter = painterResource(drawableId),
                            contentDescription = null
                        )
                    },
                    content = { Text(text) }
                )
            }
        }
        settingsContent(this)
    }
}


@Composable
fun selectedPivotsBottomScreen(
    selectedPivots: Set<PivotNode>,
    onUnselectItem: (Pivot) -> Unit
): LazyListScope.() -> Unit {
    return {
        if (selectedPivots.isEmpty()) {
            item {
                Text(
                    "No hay piezas seleccionadas",
                    modifier = Modifier.padding(MaterialTheme.spacing.medium),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            itemsIndexed(selectedPivots.toList()) { index, pivot ->
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
                        text = pivot.name ?: "",
                        modifier = Modifier.weight(1F)
                    )
                    IconButton(
                        onClick = {
                            val info = Pivot(pivot.modelId, pivot.hash)
                            onUnselectItem(info)
                                  },
                        content = { CloseIcon() }
                    )
                }
            }
        }
    }
}

@Composable
fun selectedOperationBottomScreen(
    operation: Operation,
    onOperationChange: (Operation) -> Unit,
    onNavigateBack: () -> Unit
): LazyListScope.() -> Unit {
    val updatedOperation = rememberUpdatedState(operation)
    return {
        itemsIndexed(OperationType.entries) { index, operationType ->
            if (index > 0) HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable {
                        onOperationChange(updatedOperation.value.copy(type = operationType))
                        onNavigateBack()
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
        initialXMeters = currentOperation.offsetTransform.position.x,
        initialYMeters = currentOperation.offsetTransform.position.y,
        initialZMeters = currentOperation.offsetTransform.position.z
    )
    val multiRotationState = rememberMultiRotationState(
        initialEulerDegrees = currentOperation.offsetTransform.quaternion.toEulerAngles()
    )
    val updatedCurrentOperation = rememberUpdatedState(currentOperation)

    LaunchedEffect(Unit) {
        snapshotFlow { multiTranslationState.position to multiRotationState.quaternion }
            .distinctUntilChanged()
            .collect { (position, rotation) ->
                onOperationChange(
                    updatedCurrentOperation.value.copy(
                        offsetTransform = Transform(
                            position = position,
                            quaternion = rotation
                        )
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
    var usePitch by remember { mutableStateOf(false) }

    val translationState =
        rememberSingleTranslationState(initialMeters = max(operation.offsetTransform.position))
    val pitchState = rememberSingleTranslationState(
        initialMeters = if (operation.turns == 0F) {
            0F
        } else max(operation.offsetTransform.position) / operation.turns
    )
    val turnsState = rememberSingleTranslationState(
        initialMeters = operation.turns,
        initialTranslation = Translation.METERS
    )

    fun updateFromDistance() {
        val distance = translationState.meters
        val pitch = pitchState.meters
        val turns = turnsState.numeric

        when {
            distance == 0F -> return
            usePitch -> {
                val calculatedTurns = if (pitch == 0F) 0F else distance / pitch
                turnsState.updateUnits(calculatedTurns.toString())
            }

            else -> {
                val calculatedPitch = if (turns == 0F) 0F else distance / turns
                pitchState.updateMeters(calculatedPitch)
            }
        }
    }

    fun updateFromPitch() {
        val pitch = pitchState.meters
        val distance = translationState.meters

        when {
            pitch > distance -> turnsState.updateUnits("1")
            distance != 0F && pitch != 0f -> {
                val calculatedTurns = translationState.meters / pitch
                turnsState.updateUnits(calculatedTurns.toString())
            }
        }
    }

    fun updateFromTurns() {
        val turns = turnsState.numeric
        val distance = translationState.meters

        when {
            distance != 0F && turns != 0F -> {
                val calculatedPitchMeters = distance / turns
                pitchState.updateMeters(calculatedPitchMeters)
            }

            else -> {
                pitchState.updateUnits("0")
            }
        }
    }

    LaunchedEffect(
        translationState.meters,
        pitchState.meters,
        turnsState.numeric,
        selectedAxis,
        usePitch
    ) {
        val pitch = pitchState.meters
        val distance = translationState.meters
        val turns = when {
            usePitch -> if(pitch != 0F) distance / pitch else 0F
            else -> turnsState.numeric
        }

        onOperationChange(
            operation.copy(
                offsetTransform = Transform(
                    position = unidirectionalTranslation(selectedAxis, distance),
                    quaternion = unidirectionalRotation(selectedAxis, turns * 360F)
                ),
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
                onValueChange = {
                    updateFromDistance()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            PitchTextField(
                name = if (usePitch) "Pitch" else "Revs", // TODO create strings
                state = if (usePitch) pitchState else turnsState,
                onValueChange = { units ->
                    if (usePitch) {
                        pitchState.updateUnits(units)
                        updateFromPitch()
                    } else {
                        turnsState.updateUnits(units)
                        updateFromTurns()
                    }
                },
                isPitch = usePitch,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val checked = !usePitch
                        usePitch = checked
                        if (checked) updateFromPitch() else updateFromTurns()
                    }
            ) {
                Checkbox(
                    checked = usePitch,
                    onCheckedChange = { checked ->
                        usePitch = checked
                        if (checked) updateFromPitch() else updateFromTurns()
                    }
                )
                Text(stringResource(R.string.operation_checkbox_use_pitch_supporting_text))
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
        initialMeters = max(operation.offsetTransform.position)
    )
    val rotationState = rememberSingleRotationState(
        initialDegrees = operation.offsetTransform.quaternion.extractSingleAxisDegrees(selectedAxis)
    )
    val updatedCurrentOperation by rememberUpdatedState(operation)

    LaunchedEffect(Unit) {
        snapshotFlow { translationState.meters to rotationState.degrees }
            .distinctUntilChanged()
            .collect { (meters, degrees) ->
                onOperationChange(
                    updatedCurrentOperation.copy(
                        offsetTransform = Transform(
                            position = unidirectionalTranslation(selectedAxis, meters),
                            quaternion =unidirectionalRotation(selectedAxis, degrees)
                        ),
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
                            offsetTransform = Transform(newPosition, newRotation),
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
        initialEulerDegrees = operation.offsetTransform.quaternion.toEulerAngles()
    )
    val updatedCurrentOperation by rememberUpdatedState(operation)

    LaunchedEffect(Unit) {
        snapshotFlow { multiRotationState.quaternion }
            .distinctUntilChanged()
            .collect { quaternion ->
                onOperationChange(
                    updatedCurrentOperation.copy(
                        offsetTransform = Transform(quaternion = quaternion)
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