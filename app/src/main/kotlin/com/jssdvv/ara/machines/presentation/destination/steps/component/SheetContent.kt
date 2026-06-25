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
import com.jssdvv.ara.machines.domain.model.OperationTargets
import com.jssdvv.ara.machines.domain.model.Pivot
import com.jssdvv.ara.machines.domain.type.Axis
import com.jssdvv.ara.machines.domain.type.OperationType
import com.jssdvv.ara.machines.presentation.sceneview.node.PivotNode
import com.jssdvv.ara.machines.presentation.sceneview.utility.TransformState
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberTimeState
import com.jssdvv.ara.machines.presentation.sceneview.utility.rememberTransformState
import io.github.sceneview.math.Transform
import io.github.sceneview.math.quaternion
import io.github.sceneview.math.toQuaternion
import kotlinx.coroutines.flow.distinctUntilChanged

enum class BottomSheetScreen { MAIN, ENTITIES, OPERATIONS }

@Composable
fun OperationBottomSheet(
    visible: Boolean,
    selectedPivots: Set<PivotNode>,
    onUnselectPivot: (Pivot) -> Unit,
    onSelectionChange: (active: Boolean) -> Unit,
    editingTargets: OperationTargets,
    onChangeInfo: (Operation) -> Unit,
    onSaveEditing: () -> Unit,
    onCancelEditing: () -> Unit,
    onDeleteOperation: () -> Unit,
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
                            onSaveClick = onSaveEditing,
                            onCancelClick = onCancelEditing
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
            val transformState = rememberTransformState(
                initialPosition = editingTargets.operation.offsetTransform.position,
                initialQuaternion = editingTargets.operation.offsetTransform.quaternion,
                initialAxis = editingTargets.operation.axis,
                initialTurns = editingTargets.operation.turns
            )

            val settingsContent = when (editingTargets.operation.type) {
                OperationType.POINT_TO_POINT -> pointToPointSettings(
                    transformState = transformState,
                    onTranslationChange = {
                        onChangeInfo(
                            editingTargets.operation.copy(
                                offsetTransform = Transform(
                                    position = transformState.multiTranslation.meters,
                                    quaternion = editingTargets.operation.offsetTransform.quaternion
                                )
                            )
                        )
                    },
                    onOrientationChange = {
                        onChangeInfo(
                            editingTargets.operation.copy(
                                offsetTransform = Transform(
                                    position = editingTargets.operation.offsetTransform.position,
                                    quaternion = transformState.multiOrientation.degrees.toQuaternion()
                                )
                            )
                        )
                    }
                )

                OperationType.SCREW -> screwSettings(
                    transformState = transformState,
                    onTranslationChange = {
                        onChangeInfo(
                            editingTargets.operation.copy(
                                offsetTransform = Transform(
                                    position = transformState.singleAxisTranslationMeters,
                                    quaternion = editingTargets.operation.offsetTransform.quaternion
                                )
                            )
                        )
                    },
                    onAxisChange = {
                        onChangeInfo(
                            editingTargets.operation.copy(
                                axis = transformState.universalAxis,
                                offsetTransform = Transform(
                                    position = transformState.singleAxisTranslationMeters,
                                    quaternion = transformState.singleAxisQuaternionDegrees
                                )
                            )
                        )
                    },
                    onTurnsChange = {
                        onChangeInfo(
                            editingTargets.operation.copy(
                                turns = transformState.turns,
                                offsetTransform = Transform(
                                    position = editingTargets.operation.offsetTransform.position,
                                    quaternion = transformState.singleAxisQuaternionDegrees
                                )
                            )
                        )
                    },
                )

                OperationType.CYLINDRICAL -> cylindricalSettings(
                    transformState = transformState,
                    onTranslationChange = {
                        onChangeInfo(
                            editingTargets.operation.copy(
                                offsetTransform = Transform(
                                    position = transformState.singleAxisTranslationMeters,
                                    quaternion = editingTargets.operation.offsetTransform.quaternion
                                )
                            )
                        )
                    },
                    onOrientationChange = {
                        onChangeInfo(
                            editingTargets.operation.copy(
                                offsetTransform = Transform(
                                    position = editingTargets.operation.offsetTransform.position,
                                    quaternion = transformState.singleAxisQuaternionDegrees
                                )
                            )
                        )
                    },
                    onAxisChange = {
                        onChangeInfo(
                            editingTargets.operation.copy(
                                axis = transformState.universalAxis,
                                offsetTransform = Transform(
                                    position = transformState.singleAxisTranslationMeters,
                                    quaternion = transformState.singleAxisQuaternionDegrees
                                )
                            )
                        )
                    }
                )

                else -> jointSettings(
                    transformState = transformState,
                    onOrientationChange = {
                        onChangeInfo(
                            editingTargets.operation.copy(
                                offsetTransform = Transform(
                                    position = editingTargets.operation.offsetTransform.position,
                                    quaternion = transformState.singleAxisQuaternionDegrees
                                )
                            )
                        )
                    }
                )
            }

            val lazyListContent = when (currentScreen) {
                BottomSheetScreen.MAIN -> stepsMainBottomScreen(
                    operation = editingTargets.operation,
                    onChangeEditingOperation = onChangeInfo,
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
                    operation = editingTargets.operation,
                    onOperationChange = onChangeInfo,
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
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            TimeTextField(
                name = "duration",
                state = durationState,
                onValueChange = {},
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
                        //todo check this
                        if (operationType == OperationType.JOINT) {
                            onOperationChange(
                                updatedOperation.value.copy(
                                    type = operationType,
                                    offsetTransform = Transform(
                                        quaternion = updatedOperation.value.offsetTransform.quaternion
                                    )
                                )
                            )
                        } else {
                            onOperationChange(updatedOperation.value.copy(type = operationType))
                        }
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
    transformState: TransformState,
    onTranslationChange: () -> Unit,
    onOrientationChange: () -> Unit,
): LazyListScope.() -> Unit {
    return {
        item { HorizontalDivider() }
        items(
            listOf(
                Axis.X to transformState.multiTranslation.x,
                Axis.Y to transformState.multiTranslation.y,
                Axis.Z to transformState.multiTranslation.z
            )
        ) { (axis, state) ->
            TranslationTextField(
                name = axis.name,
                state = state,
                onValueChange = onTranslationChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item { HorizontalDivider() }
        items(
            listOf(
                Axis.X to transformState.multiOrientation.x,
                Axis.Y to transformState.multiOrientation.y,
                Axis.Z to transformState.multiOrientation.z
            )
        ) { (axis, state) ->
            RotationTextField(
                name = stringResource(axis.rotationNameId),
                state = state,
                onValueChange = onOrientationChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun screwSettings(
    transformState: TransformState,
    onTranslationChange: () -> Unit,
    onAxisChange: () -> Unit,
    onTurnsChange: () -> Unit,
): LazyListScope.() -> Unit {
    return {
        item {
            AxisSelector(
                axis = transformState.universalAxis,
                onAxisChange = {
                    transformState.universalAxis = it
                    onAxisChange()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            TranslationTextField(
                name = transformState.universalAxis.name,
                state = transformState.universalTranslation,
                onValueChange = onTranslationChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            PitchTextField(
                name = if (transformState.universalPitchTurns.useTurns) "Revs" else "Pitch", // TODO create strings
                state = transformState.universalPitchTurns,
                onValueChange = onTurnsChange,
                isPitch = !transformState.universalPitchTurns.useTurns,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { transformState.universalPitchTurns.toggleTurns() }
            ) {
                Checkbox(
                    checked = !transformState.universalPitchTurns.useTurns,
                    onCheckedChange = {
                        transformState.universalPitchTurns.updateUseTurns(!it)
                    }
                )
                Text(stringResource(R.string.operation_checkbox_use_pitch_supporting_text))
            }
        }
    }
}

@Composable
fun cylindricalSettings(
    transformState: TransformState,
    onTranslationChange: () -> Unit,
    onOrientationChange: () -> Unit,
    onAxisChange: () -> Unit
): LazyListScope.() -> Unit {
    return {
        item {
            AxisSelector(
                axis = transformState.universalAxis,
                onAxisChange = {
                    transformState.universalAxis = it
                    onAxisChange()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            TranslationTextField(
                name = transformState.universalAxis.name,
                state = transformState.universalTranslation,
                onValueChange = onTranslationChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            RotationTextField(
                name = stringResource(transformState.universalAxis.rotationNameId),
                state = transformState.universalOrientation,
                onValueChange = onOrientationChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun jointSettings(
    transformState: TransformState,
    onOrientationChange: () -> Unit,
): LazyListScope.() -> Unit {
    return {
        items(
            listOf(
                Axis.X to transformState.multiOrientation.x,
                Axis.Y to transformState.multiOrientation.y,
                Axis.Z to transformState.multiOrientation.z
            )
        ) { (axis, state) ->
            RotationTextField(
                name = axis.name,
                state = state,
                onValueChange = onOrientationChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}