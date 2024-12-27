package com.jssdvv.ara.core.presentation.theme

import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.foundation.shape.RoundedCornerConcaveShape
import com.jssdvv.ara.core.presentation.foundation.shape.ShapeEdge
import com.jssdvv.ara.core.presentation.foundation.shape.TwoCornerBasedConcaveShape

class TubShapes(
    val none: Shape = TubShapeTopDefaults.None,
    val extraSmall: TwoCornerBasedConcaveShape = TubShapeTopDefaults.ExtraSmallTop,
    val small: TwoCornerBasedConcaveShape = TubShapeTopDefaults.SmallTop,
    val medium: TwoCornerBasedConcaveShape = TubShapeTopDefaults.MediumTop,
    val large: TwoCornerBasedConcaveShape = TubShapeTopDefaults.LargeTop,
    val extraLarge: TwoCornerBasedConcaveShape = TubShapeTopDefaults.ExtraLargeTop,
)

object TubShapeTopDefaults {
    /** No sized corner shape */
    val None: Shape = TubShapeTopTokens.CornerNoneTop

    /** Extra small sized corner shape */
    val ExtraSmallTop: TwoCornerBasedConcaveShape = TubShapeTopTokens.CornerExtraSmallTop

    /** Small sized corner shape */
    val SmallTop: TwoCornerBasedConcaveShape = TubShapeTopTokens.CornerSmallTop

    /** Medium sized corner shape */
    val MediumTop: TwoCornerBasedConcaveShape = TubShapeTopTokens.CornerMediumTop

    /** Large sized corner shape */
    val LargeTop: TwoCornerBasedConcaveShape = TubShapeTopTokens.CornerLargeTop

    /** Extra large sized corner shape */
    val ExtraLargeTop: TwoCornerBasedConcaveShape = TubShapeTopTokens.CornerExtraLargeTop
}

internal object TubShapeTopTokens {
    val CornerNoneTop = RectangleShape
    val CornerExtraSmallTop = RoundedCornerConcaveShape(4.0.dp,ShapeEdge.Top)
    val CornerSmallTop = RoundedCornerConcaveShape(8.0.dp,ShapeEdge.Top)
    val CornerMediumTop = RoundedCornerConcaveShape(12.0.dp,ShapeEdge.Top)
    val CornerLargeTop = RoundedCornerConcaveShape(16.0.dp,ShapeEdge.Top)
    val CornerExtraLargeTop = RoundedCornerConcaveShape(28.0.dp,ShapeEdge.Top)
}
