package com.jssdvv.ara.core.domain.utility

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun PaddingValues.horizontalMirrored(): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = this.calculateEndPadding(layoutDirection),
        top = this.calculateTopPadding(),
        end = this.calculateStartPadding(layoutDirection),
        bottom = this.calculateBottomPadding()
    )
}

@Composable
fun PaddingValues.verticalMirrored(): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection),
        top = this.calculateBottomPadding(),
        end = this.calculateEndPadding(layoutDirection),
        bottom = this.calculateTopPadding()
    )
}