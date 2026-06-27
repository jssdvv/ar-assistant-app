package com.jssdvv.ara.machines.presentation.component

import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon

@Composable
fun ShutterActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconInFront: Boolean = false,
    icon: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    ButtonWithIcon(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        iconInFront = iconInFront,
        icon = icon,
        content = content
    )
}