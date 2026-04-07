package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import com.jssdvv.ara.core.domain.utility.asContentColor
import com.jssdvv.ara.core.domain.utility.asContainerColor
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.type.Axis

@Composable
fun AxisSelector(
    axis: Axis,
    onAxisChange: (Axis) -> Unit,
    modifier: Modifier = Modifier,
    isRotation: Boolean = false
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(
            space = MaterialTheme.spacing.medium,
            alignment = Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(
            space = MaterialTheme.spacing.medium,
            alignment = Alignment.CenterVertically
        ),
        maxItemsInEachRow = 3,
        maxLines = 3
    ) {
        Axis.entries.forEach {
            val color = Color(
                red = it.color[0],
                green = it.color[1],
                blue = it.color[2]
            )
            SquareButton(
                selected = it == axis,
                text = it.name,
                onClick = { onAxisChange(it) },
                color = color.asContainerColor(),
                contentColor = Color.White,
                borderColor = color.asContentColor(),
                focusBorderColor = color.asContentColor()
            )
        }
    }
}