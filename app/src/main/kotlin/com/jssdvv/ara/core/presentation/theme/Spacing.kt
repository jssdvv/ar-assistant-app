package com.jssdvv.ara.core.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
class Spacing
internal constructor(
    val tiny: Dp = SpacingTokens.tiny,
    val extraSmall: Dp = SpacingTokens.extraSmall,
    val small: Dp = SpacingTokens.small,
    val medium: Dp = SpacingTokens.medium,
    val large: Dp = SpacingTokens.large,
    val extraLarge: Dp = SpacingTokens.extraLarge,
)

internal object SpacingTokens {
    val tiny: Dp = 2.dp
    val extraSmall: Dp = 4.dp
    val small: Dp = 8.dp
    val medium: Dp = 16.dp
    val large: Dp = 24.dp
    val extraLarge: Dp = 32.dp
}

val LocalSpacing = staticCompositionLocalOf { Spacing() }