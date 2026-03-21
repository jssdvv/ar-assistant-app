package com.jssdvv.ara.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalCornerRadius provides CornerRadius(),
        LocalSpacing provides Spacing(),
        LocalTubShapes provides TubShapes()
    ) {
        MaterialTheme(
            colorScheme = if(darkTheme) DarkColorScheme else LightColorScheme,
            shapes = shapes,
            typography = Typography,
            content = content
        )
    }
}

val MaterialTheme.cornerRadius : CornerRadius
    @Composable
    @ReadOnlyComposable
    get() = LocalCornerRadius.current

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

val MaterialTheme.tubShapes: TubShapes
    @Composable
    @ReadOnlyComposable
    get() = LocalTubShapes.current