package com.jssdvv.ara.inventory.presentation.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun TabPager(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    containerColor: Color = TabRowDefaults.primaryContainerColor,
    contentColor: Color = TabRowDefaults.primaryContentColor,
    divider: @Composable () -> Unit = @Composable { },
    beyondViewportPageCount: Int = 1,
    userScrollEnabled: Boolean = true,
    tabs: @Composable () -> Unit,
    pageContent: @Composable (PagerScope.(page: Int) -> Unit),
) {
    Column(
        modifier = modifier
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            containerColor = containerColor,
            contentColor = contentColor,
            indicator = @Composable { tabPositions ->
                if (pagerState.currentPage < tabPositions.size) {
                    TabPagerIndicator(
                        Modifier.tabPagerIndicatorOffset(
                            settledPage = pagerState.settledPage,
                            currentPage = pagerState.currentPage,
                            currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
                            isDragging = pagerState.interactionSource.collectIsDraggedAsState().value,
                            indicatorWidth = tabPositions[pagerState.currentPage].width,
                            indicatorLeft = tabPositions[pagerState.currentPage].left
                        )
                    )
                }
            },
            divider = divider,
            tabs = tabs
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            userScrollEnabled = userScrollEnabled,
            pageContent = pageContent,
            beyondViewportPageCount = beyondViewportPageCount
        )
    }
}

@Composable
internal fun Modifier.tabPagerIndicatorOffset(
    settledPage: Int,
    currentPage: Int,
    currentPageOffsetFraction: Float,
    isDragging: Boolean,
    indicatorWidth: Dp,
    indicatorLeft: Dp,
): Modifier = composed(
    inspectorInfo =
    debugInspectorInfo {
        name = "tabPagerIndicatorOffset"
        value = currentPageOffsetFraction
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = indicatorWidth,
        animationSpec = tween(durationMillis = 50, easing = FastOutSlowInEasing),
        label = String()
    )
    val indicatorOffset by animateDpAsState(
        targetValue = if (currentPage == settledPage && isDragging) {
            indicatorLeft + currentTabWidth * currentPageOffsetFraction
        } else {
            indicatorLeft
        },
        animationSpec =
        if (settledPage == currentPage) {
            tween(
                durationMillis = 50,
                easing = FastOutSlowInEasing
            )
        } else {
            spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessMedium
            )
        },
        label = String()
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset { IntOffset(x = indicatorOffset.roundToPx(), y = 0) }
        .width(currentTabWidth)
}

@Composable
fun TabPagerIndicator(
    modifier: Modifier = Modifier,
    width: Dp = 24.dp,
    height: Dp = 3.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = RoundedCornerShape(
        topStart = 3.0.dp,
        topEnd = 3.0.dp
    ),
) {
    Spacer(
        modifier = modifier
            .requiredHeight(height)
            .requiredWidth(width)
            .background(color = color, shape = shape)
    )
}
