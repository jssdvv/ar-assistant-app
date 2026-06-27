package com.jssdvv.ara.machines.presentation.destination.calibration.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R

@Composable
fun RowScope.OptionsRow(
    rowHeight: Dp,
    isPlaneEnabled: Boolean,
    isTorchEnabled: Boolean,
    onToggleTorch: () -> Unit,
    onTogglePlane: () -> Unit,
) {
    val iconSize = 20.dp
    val containerColor = Color.Black.copy(alpha = 0.3F)
    val contentColor = Color.White
    val colors = IconButtonDefaults.iconButtonColors(
        containerColor = containerColor,
        contentColor = contentColor
    )

    IconButton(
        onClick = onToggleTorch,
        modifier = Modifier.size(rowHeight),
        colors = if (isTorchEnabled) colors.copy(containerColor = Color(0xFFFDFA72).copy(alpha = 0.4F)) else colors,
        content = {
            Icon(
                painter = painterResource(if (isTorchEnabled) R.drawable.ic_torch_filled else R.drawable.ic_torch_outlined),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
            )
        }
    )

    IconButton(
        onClick = onTogglePlane,
        modifier = Modifier.size(rowHeight),
        colors = if (isPlaneEnabled) colors.copy(containerColor = Color(0xFF00AAE4).copy(alpha = 0.4F)) else colors,
        content = {
            Icon(
                painter = if (isPlaneEnabled) painterResource(R.drawable.ic_plane_renderer_on) else painterResource(
                    R.drawable.ic_plane_renderer_off
                ),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
            )
        }
    )
}