package com.jssdvv.ara.core.presentation.common

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon

@Composable
fun NavigationUpIconButton(
    onNavigationUp: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    icon: @Composable () -> Unit = { ArrowBackIcon() }
) = IconButton(
    onClick = onNavigationUp,
    modifier = modifier,
    colors = IconButtonDefaults.iconButtonColors(
        containerColor = containerColor,
        contentColor = contentColor
    ),
    content = { icon() }
)

@Composable
fun EditButtonWithIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false,
) = ButtonWithIcon(
    onClick = onClick,
    modifier = modifier,
    colors = if (isOutlined) ButtonDefaults.outlinedButtonColors() else ButtonDefaults.buttonColors(),
    border = if (isOutlined) ButtonDefaults.outlinedButtonBorder() else null,
    icon = { EditIcon() },
    content = { Text(stringResource(R.string.button_edit_action)) }
)

@Composable
fun ToggleVisibleIconButton(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: IconButtonColors = IconButtonDefaults.iconButtonColors()
) = IconButton(
    onClick = onClick,
    modifier = modifier,
    content = { if (isVisible) VisibleOnIcon() else VisibleOffIcon(tint = color.disabledContentColor) }
)