package com.jssdvv.ara.core.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.foundation.shape.RoundedCornerConcaveShape
import com.jssdvv.ara.core.presentation.foundation.shape.ShapeEdge
import com.jssdvv.ara.core.presentation.foundation.shape.TwoCornerBasedConcaveShape

@Immutable
class CornerRadius
internal constructor(
    val tiny: Dp = CornerRadiusTokens.tiny,
    val extraSmall: Dp = CornerRadiusTokens.extraSmall,
    val small: Dp = CornerRadiusTokens.small,
    val medium: Dp = CornerRadiusTokens.medium,
    val large: Dp = CornerRadiusTokens.large,
    val extraLarge: Dp = CornerRadiusTokens.extraLarge,
)

internal object CornerRadiusTokens {
    val tiny: Dp = 2.dp
    val extraSmall: Dp = 4.dp
    val small: Dp = 8.dp
    val medium: Dp = 16.dp
    val large: Dp = 24.dp
    val extraLarge: Dp = 32.dp
}

val LocalCornerRadius = staticCompositionLocalOf { CornerRadius() }

@Immutable
class TubShapes
internal constructor(
    val tiny: TwoCornerBasedConcaveShape = TubShapeTopDefaults.TinyTop,
    val extraSmall: TwoCornerBasedConcaveShape = TubShapeTopDefaults.ExtraSmallTop,
    val small: TwoCornerBasedConcaveShape = TubShapeTopDefaults.SmallTop,
    val medium: TwoCornerBasedConcaveShape = TubShapeTopDefaults.MediumTop,
    val large: TwoCornerBasedConcaveShape = TubShapeTopDefaults.LargeTop,
    val extraLarge: TwoCornerBasedConcaveShape = TubShapeTopDefaults.ExtraLargeTop,
)

object TubShapeTopDefaults {
    val TinyTop: TwoCornerBasedConcaveShape = TubShapeTopTokens.CornerTinyTop
    val ExtraSmallTop: TwoCornerBasedConcaveShape = TubShapeTopTokens.CornerExtraSmallTop
    val SmallTop: TwoCornerBasedConcaveShape = TubShapeTopTokens.CornerSmallTop
    val MediumTop: TwoCornerBasedConcaveShape = TubShapeTopTokens.CornerMediumTop
    val LargeTop: TwoCornerBasedConcaveShape = TubShapeTopTokens.CornerLargeTop
    val ExtraLargeTop: TwoCornerBasedConcaveShape = TubShapeTopTokens.CornerExtraLargeTop
}

internal object TubShapeTopTokens {
    val CornerTinyTop = RoundedCornerConcaveShape(CornerRadiusTokens.tiny, ShapeEdge.Top)
    val CornerExtraSmallTop =
        RoundedCornerConcaveShape(CornerRadiusTokens.extraSmall, ShapeEdge.Top)
    val CornerSmallTop = RoundedCornerConcaveShape(CornerRadiusTokens.small, ShapeEdge.Top)
    val CornerMediumTop = RoundedCornerConcaveShape(CornerRadiusTokens.medium, ShapeEdge.Top)
    val CornerLargeTop = RoundedCornerConcaveShape(CornerRadiusTokens.large, ShapeEdge.Top)
    val CornerExtraLargeTop =
        RoundedCornerConcaveShape(CornerRadiusTokens.extraLarge, ShapeEdge.Top)
}

val LocalTubShapes = staticCompositionLocalOf { TubShapes() }

internal val shapes = Shapes(
    extraSmall = RoundedCornerShape(CornerRadiusTokens.extraSmall),
    small = RoundedCornerShape(CornerRadiusTokens.small),
    medium = RoundedCornerShape(CornerRadiusTokens.medium),
    large = RoundedCornerShape(CornerRadiusTokens.large),
    extraLarge = RoundedCornerShape(CornerRadiusTokens.extraLarge)
)