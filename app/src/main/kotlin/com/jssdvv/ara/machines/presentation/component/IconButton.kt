package com.jssdvv.ara.machines.presentation.component

import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jssdvv.ara.core.presentation.navigation.MarkerIcon

@Composable
fun SceneMarkerIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.filledIconButtonColors().copy(
            containerColor = Color.Black.copy(alpha = 0.4F),
            contentColor = Color.White
        ),
        content = { MarkerIcon() }
    )
}