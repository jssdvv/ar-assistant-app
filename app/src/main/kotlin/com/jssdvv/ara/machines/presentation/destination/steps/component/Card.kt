package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.ArrowDownIcon
import com.jssdvv.ara.core.presentation.common.WarningIcon
import com.jssdvv.ara.core.presentation.foundation.component.FocusableCard
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.model.Step
import com.jssdvv.ara.machines.domain.utility.RenderableInfo
import kotlinx.coroutines.launch

@Composable
fun StepCard(
    onClick: () -> Unit,
    step: Step,
    isSelected: Boolean,
    onEditStep: (Step) -> Unit,
    operations: List<Operation>,
    selectedOperation: Operation?,
    onSelectOperation: (Int) -> Unit,
    onAddOperation: () -> Unit,
    onEditOperation: (Operation) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(isSelected) }
    val noOperations = operations.isEmpty()
    val interactionSource = remember { MutableInteractionSource() }
    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 180f else 0f,
        label = "chevron_rotation"
    )

    LaunchedEffect(isSelected) { expanded = isSelected }

    FocusableCard(
        onClick = {
            onClick()
            expanded = !expanded
        },
        isFocused = isSelected,
        modifier = modifier.fillMaxWidth(),
        color = when {
            expanded -> MaterialTheme.colorScheme.surfaceVariant
            noOperations -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5F)
            else -> MaterialTheme.colorScheme.surface
        },
        contentColor = MaterialTheme.colorScheme.onSurface,
        borderColor = when {
            expanded -> MaterialTheme.colorScheme.surfaceVariant
            noOperations -> MaterialTheme.colorScheme.error.copy(alpha = 0.5F)
            else -> MaterialTheme.colorScheme.surface
        }
    ) {
        Column(
            modifier = Modifier
                .indication(interactionSource, ripple())
                .padding(
                    horizontal = MaterialTheme.spacing.small, //All components needs compensation
                    vertical = MaterialTheme.spacing.medium
                )
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            // To compensate the horizontal padding: medium = 2 * small
            with(Modifier.padding(horizontal = MaterialTheme.spacing.small)) {
                Row(
                    modifier = this.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    Text(
                        text = "${step.order.toString().padStart(2, '0')}.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = step.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    ArrowDownIcon(Modifier.graphicsLayer { rotationZ = rotation })
                }

                if (expanded) {
                    Text(
                        text = step.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = this
                    )
                    EditStepButton(this.align(Alignment.End)) { onEditStep(step) }

                    HorizontalDivider(
                        modifier = this,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )

                    if (noOperations) {
                        Column (
                            modifier = this
                                .fillMaxWidth()
                                .padding(vertical = MaterialTheme.spacing.small),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                        ) {
                            WarningIcon()
                            Text(
                                text = stringResource(R.string.card_step_operations_empty_message),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = "Operations",
                            modifier = this,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        // OperationItem is the only component that doesn't use padding compensation
                        operations.sortedBy { it.order }.forEach { operation ->
                            OperationItem(
                                onClick = { onSelectOperation((operation.id)) },
                                isSelected = selectedOperation?.id == operation.id,
                                operation = operation,
                                onEditOperation = { onEditOperation(operation) },
                            )
                        }
                    }
                }
                if (noOperations || expanded) {
                    AddOperationButton(
                        modifier = this.align(Alignment.End),
                        onClick = onAddOperation
                    )
                }
            }
        }
    }
}

@Composable
fun SelectableRenderablesCard(
    modifier: Modifier = Modifier,
    items: List<RenderableInfo>,
    onActivateSelection: () -> Unit,
    onDeleteItem : (RenderableInfo) -> Unit
) {
    val scope = rememberCoroutineScope()
    val itemHeight = 56.dp
    val maxVisibleItems = 3
    val listState = rememberLazyListState()

    val itemsRemainingBelow by remember(items) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsSize = layoutInfo.visibleItemsInfo.size
            if(items.size <= maxVisibleItems) 0
            else items.size - (listState.firstVisibleItemIndex + visibleItemsSize)
        }
    }

    val showBackToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    FocusableCard(
        onClick = { },
        isFocused = false,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column{
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onActivateSelection() }
                    .padding(MaterialTheme.spacing.medium),
                contentAlignment = Alignment.CenterStart,
            ){
                Text(
                    text = "Select a part...", // todo create string res??
                    style = MaterialTheme.typography.labelLarge
                )
            }
            if(items.isNotEmpty()) HorizontalDivider()
            Box(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.heightIn(max = itemHeight * maxVisibleItems)
                ) {
                    items(
                        items = items,
                        key = { "${it.modelId}_${it.xxh3}" }
                    ) {
                        // todo fix this
                        RemovableRenderableItem(
                            name = "it.xxh3",
                            onRemoveClick = { /*onDeleteItem(it)*/ },
                        )
                    }
                }

                this@Column.AnimatedVisibility(
                    visible = itemsRemainingBelow > 0,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Surface(
                        modifier = Modifier.padding(bottom = 4.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            text = "↓ $itemsRemainingBelow más",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                this@Column.AnimatedVisibility(
                    visible = showBackToTop,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)
                ) {
                    FilledIconButton(
                        onClick = {
                            scope.launch { listState.animateScrollToItem(0) }
                        },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        ArrowDownIcon(
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(180F)
                        )
                    }
                }
            }
        }
    }
}