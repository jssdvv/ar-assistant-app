package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jssdvv.ara.core.presentation.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnimatedToolBar(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    leadingContent : @Composable RowScope.() -> Unit = {},
    trailingContent: @Composable RowScope.() -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    val enterDuration = 300
    val exitDuration = 250
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = enterDuration, easing = EaseOutCubic)
        ) + fadeIn(tween(enterDuration)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = exitDuration, easing = EaseInCubic)
        ) + fadeOut(tween(exitDuration))
    ) {
        HorizontalFloatingToolbar(
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.small),
            expanded = expanded,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            content = content
        )
    }
}