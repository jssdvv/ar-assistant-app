package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.machines.domain.type.Axis

@Composable
fun AxisSelector(
    axis: Axis,
    onAxisChange: (Axis) -> Unit,
    modifier: Modifier = Modifier,
    isRotation: Boolean = false
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 3,
        maxLines = 3
    ) {
        Axis.entries.forEach {
            SquareButton(
                selected = it == axis,
                onClick = { onAxisChange(it) },
                text = if (isRotation) stringResource(it.rotationNameId) else it.name
            )
        }
    }
}