package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.common.ArrowPreviousItemIcon
import com.jssdvv.ara.core.presentation.common.CheckIcon
import com.jssdvv.ara.core.presentation.common.CloseIcon
import com.jssdvv.ara.core.presentation.common.MenuCloseIcon
import com.jssdvv.ara.core.presentation.common.MenuIcon
import com.jssdvv.ara.core.presentation.common.NavigationUpIconButton
import com.jssdvv.ara.core.presentation.theme.spacing
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Stable
class DraggableDrawerState(
    initialWidth: Dp = 160.dp,
    val minWidth: Dp = 140.dp,
    val maxWidth: Dp = 300.dp,
) {
    private val threshold = minWidth / 2

    internal var lastDrag by mutableStateOf(initialWidth)

    internal val drag = Animatable(
        0.dp,
        TwoWayConverter(
            convertToVector = { AnimationVector1D(it.value) },
            convertFromVector = { it.value.dp }
        )
    )

    val width by derivedStateOf { if (drag.value < minWidth) minWidth else drag.value }
    val offset by derivedStateOf { if (drag.value < minWidth) minWidth - drag.value else 0.dp }
    val isOpen by derivedStateOf { drag.value >= threshold }

    suspend fun open() = drag.animateTo(lastDrag)
    suspend fun close() = drag.animateTo(0.dp)
    suspend fun toggle() = if (isOpen) close() else open()

    internal suspend fun snapTo(value: Dp) {
        drag.snapTo(value.coerceIn(0.dp, maxWidth))
    }

    internal suspend fun onDragEnd() {
        if (isOpen) {
            val fixedDrag = drag.value.coerceAtLeast(minWidth)
            drag.animateTo(fixedDrag)
            lastDrag = fixedDrag
        } else {
            close()
        }
    }
}

@Composable
fun rememberDraggableDrawerState(
    initialWidth: Dp = 160.dp,
    minWidth: Dp = 140.dp,
    maxWidth: Dp = 300.dp
): DraggableDrawerState {
    return remember(initialWidth, minWidth, maxWidth) {
        DraggableDrawerState(
            initialWidth = initialWidth,
            minWidth = minWidth,
            maxWidth = maxWidth
        )
    }
}

@Composable
fun DraggableDrawer(
    modifier: Modifier = Modifier,
    state: DraggableDrawerState = rememberDraggableDrawerState(),
    gap: Dp = MaterialTheme.spacing.extraSmall, // 4.Dp
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Layout(
        modifier = modifier
            .offset {
                with(density) { IntOffset(-state.offset.toPx().roundToInt(), 0) }
            },
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(state.width)
                    .graphicsLayer {
                        clip = true
                        shape = object : Shape {
                            override fun createOutline(
                                size: Size,
                                layoutDirection: LayoutDirection,
                                density: Density
                            ): Outline {
                                val clipWidth = with(density) { state.width.toPx() }
                                return Outline.Rectangle(
                                    Rect(0f, 0f, clipWidth, size.height)
                                )
                            }
                        }
                    },
                color = containerColor,
                contentColor = contentColor,
            ) {
                Box { content() }
            }

            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch { state.snapTo(state.drag.value + dragAmount.toDp()) }
                            },
                            onDragEnd = { scope.launch { state.onDragEnd() } }
                        )
                    },
                onClick = {
                    focusManager.clearFocus()
                    scope.launch { state.toggle() }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                content = { if (state.isOpen) MenuCloseIcon() else MenuIcon() }
            )
        }
    ) { measurables, constraints ->
        val surfacePlaceable = measurables[0].measure(constraints)
        val buttonPlaceable = measurables[1].measure(
            constraints.copy(minWidth = 0, minHeight = 0)
        )

        val totalWidth = with(density) {
            (state.width.toPx() + gap.toPx()).roundToInt()
        } + buttonPlaceable.width
        val totalHeight = maxOf(surfacePlaceable.height, buttonPlaceable.height)

        layout(totalWidth, totalHeight) {
            val buttonY = (totalHeight - buttonPlaceable.height) / 2

            surfacePlaceable.place(0, 0)
            buttonPlaceable.place(
                x = with(density) { (state.width.toPx() + gap.toPx()).roundToInt() },
                y = buttonY
            )
        }
    }
}

@Stable
class DraggableBottomSheetState(
    initialHeight: Dp = 160.dp
) {
    var heightLimit by mutableStateOf(initialHeight)
    val bottomSheetHeaderHeight = 56.dp
    val hintHeight = 24.dp
    private val minDrag = bottomSheetHeaderHeight + hintHeight

    internal var lastDrag by mutableStateOf(initialHeight.coerceAtLeast(minDrag))

    internal val drag = Animatable(
        initialHeight,
        TwoWayConverter(
            convertToVector = { AnimationVector1D(it.value) },
            convertFromVector = { it.value.dp }
        )
    )

    val height by derivedStateOf {
        if (heightLimit == Dp.Unspecified) initialHeight
        else drag.value.coerceIn(minDrag, heightLimit)
    }

    companion object {
        val Saver = Saver<DraggableBottomSheetState, Float>(
            save = { it.lastDrag.value },
            restore = { DraggableBottomSheetState(initialHeight = it.dp) }
        )
    }

    internal suspend fun snapTo(value: Dp, heightLimit: Dp) {
        drag.snapTo(value.coerceIn(minDrag, heightLimit))
    }

    internal suspend fun onDragEnd() {
        lastDrag = drag.value.coerceAtLeast(minDrag)

        drag.animateTo(
            targetValue = lastDrag,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }
}

@Composable
fun rememberDraggableBottomSheetState(
    initialHeight: Dp = 160.dp
): DraggableBottomSheetState {
    return rememberSaveable(saver = DraggableBottomSheetState.Saver) {
        DraggableBottomSheetState(initialHeight = initialHeight)
    }
}

@Composable
fun DraggableBottomSheet(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    header: @Composable RowScope.() -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    var dragJob = remember { mutableStateOf<Job?>(null) }
    val state = rememberDraggableBottomSheetState()
    val focusManager = LocalFocusManager.current

    val enterDuration = 300
    val exitDuration = 250

    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier.imePadding(),
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = enterDuration, easing = EaseOutCubic)
        ) + fadeIn(tween(enterDuration)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = exitDuration, easing = EaseInCubic)
        ) + fadeOut(tween(exitDuration))
    ) {
        BoxWithConstraints(
            contentAlignment = Alignment.BottomCenter
        ) {
            val heightLimit = maxHeight

            LaunchedEffect(heightLimit) { state.heightLimit = heightLimit }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(state.height)
                    .align(Alignment.BottomCenter),
                contentColor = contentColor
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(state.bottomSheetHeaderHeight)
                            .background(containerColor)
                            .pointerInput(Unit) {
                                detectVerticalDragGestures(
                                    onVerticalDrag = { change, dragAmount ->
                                        change.consume()
                                        dragJob.value?.cancel()
                                        dragJob.value = scope.launch {
                                            state.snapTo(
                                                value = state.drag.value - dragAmount.toDp(),
                                                heightLimit = heightLimit
                                            )
                                        }
                                    },
                                    onDragEnd = { scope.launch { state.onDragEnd() } }
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        content = header
                    )
                    content()
                }
            }
        }
    }
}

@Composable
fun BottomSheetMainHeader(
    title: String,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = MaterialTheme.spacing.medium,
                end = MaterialTheme.spacing.extraSmall,
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1F),
            maxLines = 1
        )
        Spacer(Modifier.width(MaterialTheme.spacing.extraSmall))
        IconButton(
            onClick = onCancelClick,
            content = { CloseIcon() }
        )
        IconButton(
            onClick = onSaveClick,
            content = { CheckIcon() }
        )
    }
}

@Composable
fun BottomSheetSubHeader(
    title: String,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = MaterialTheme.spacing.medium,
                end = MaterialTheme.spacing.extraSmall,
            ),
        contentAlignment = Alignment.Center
    ) {
        NavigationUpIconButton(
            onNavigationUp = onNavigateUp,
            modifier = Modifier.align(Alignment.CenterStart),
            icon = { ArrowPreviousItemIcon() }
        )
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}