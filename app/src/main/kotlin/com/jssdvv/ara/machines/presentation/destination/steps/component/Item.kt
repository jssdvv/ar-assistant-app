package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.CloseIcon
import com.jssdvv.ara.core.presentation.common.EditIcon
import com.jssdvv.ara.core.presentation.common.ToggleVisibleIconButton
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Operation
import com.jssdvv.ara.machines.domain.type.OperationType

@Composable
fun RenderableItem(
    name: String,
    isSelected: Boolean,
    isVisible: Boolean,
    onVisibilityChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 360f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "IconRotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.graphicsLayer(rotationZ = rotation),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(targetState = isSelected, label = "IconChange") { selected ->
                    Icon(
                        painter = painterResource(
                            if (selected) R.drawable.ic_check_circle
                            else R.drawable.ic_renderable
                        ),
                        contentDescription = null,
                    )
                }
            }

            Text(
                modifier = Modifier.weight(1F),
                text = name,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium
            )

            Box(
                contentAlignment = Alignment.Center
            ) {
                ToggleVisibleIconButton(
                    isVisible = isVisible,
                    onClick = onVisibilityChange,
                )
            }
        }
    }
}

@Composable
fun OperationItem(
    onClick: () -> Unit,
    isSelected: Boolean,
    operation: Operation,
    onEditOperation: () -> Unit,
    modifier: Modifier = Modifier
) = Box(
    modifier = if (isSelected) {
        Modifier
            .border(
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium
            )
    } else Modifier
) {
    Row(
        modifier = modifier
            .padding(
                horizontal = MaterialTheme.spacing.extraSmall,
                vertical = MaterialTheme.spacing.small
            )
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
    ) {
        Icon(
            painter = painterResource(operation.type.iconResId),
            contentDescription = null,
            modifier = Modifier.padding(12.dp),
        )

        Column(modifier = Modifier.weight(1F)) {
            Text(
                text = operation.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = LocalContext.current.getString(
                    R.string.operation_item_duration_label,
                    operation.duration
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = LocalContext.current.getString(
                    R.string.operation_item_starting_delay_label,
                    operation.delay
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = onEditOperation,
            content = { EditIcon() }
        )
    }
}

@Preview
@Composable
fun OperationsTest(modifier: Modifier = Modifier) {
    val ops = listOf(
        Operation(
            id = 1,
            stepId = 1,
            orderNumber = 1,
            title = "Operation 1",
            type = OperationType.SCREW,
            duration = 10F,
        ),
        Operation(
            id = 2,
            stepId = 1,
            orderNumber = 2,
            title = "Operation 2",
            type = OperationType.POINT_TO_POINT,
            duration = 10F,
        )
    )

    Column {
        ops.forEach { op ->
            OperationItem(
                operation = op,
                onClick = {},
                onEditOperation = {},
                modifier = modifier,
                isSelected = op.id == 1
            )
        }
    }
}

@Composable
fun RemovableRenderableItem(
    name: String,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) = Row (
    modifier = modifier
        .height(56.dp)
        .padding(horizontal = MaterialTheme.spacing.medium),
    verticalAlignment = Alignment.CenterVertically,
) {
    Text(
        text = name,
        modifier = Modifier.weight(1F),
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )
    IconButton(onRemoveClick) { CloseIcon() }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimationItem(
    isSelected: Boolean,
    itemText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .combinedClickable(
                onClick = onClick,
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = null
        )
        Icon(
            painter = painterResource(R.drawable.ic_animation),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = itemText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubModelItem(
    isSelected: Boolean,
    itemText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .combinedClickable(
                onClick = onClick,
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = null
        )
        Icon(
            painter = painterResource(
                if (isSelected) R.drawable.ic_augmented_reality_filled else R.drawable.ic_augmented_reality_outlined
            ),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = itemText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
}