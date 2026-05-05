package com.jssdvv.ara.machines.presentation.destination.documents.component

import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import coil.compose.AsyncImage
import com.jssdvv.ara.core.presentation.common.component.SearchIcon
import com.jssdvv.ara.core.presentation.common.component.ShareIcon
import com.jssdvv.ara.core.presentation.foundation.component.DocumentSearchTopBar
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Document
import com.jssdvv.ara.machines.presentation.destination.documents.InDocumentSearchState
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@Composable
fun DocumentVisor(
    document: Document,
    pageCount: Int = 0,
    pageSizes: Map<Int, IntSize> = emptyMap(),
    pageBitmaps: Map<Int, Bitmap> = emptyMap(),
    currentPage: Int = 0,
    search: InDocumentSearchState = InDocumentSearchState(),
    onLoadPage: (Int) -> Unit,
    onCloseDocument: () -> Unit,
    onShareDocument: (Document) -> Unit
) {
    val density = LocalDensity.current
    var searching by remember { mutableStateOf(false) }
    var zoom by remember { mutableFloatStateOf(1F) }
    var translationX by remember { mutableStateOf(0F) }
    val state = rememberLazyListState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(state.firstVisibleItemIndex) {
        onLoadPage(state.firstVisibleItemIndex)
    }

    BackHandler(true) { if (searching) searching = false else onCloseDocument() }

    Scaffold(
        topBar = {
            DocumentSearchTopBar(
                searching = searching,
                query = search.query,
                onQueryChange = {},
                title = document.name,
                onNavigateUp = { if (searching) searching = false else onCloseDocument() },
                actions = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                        IconButton(
                            onClick = { searching = true },
                            content = { SearchIcon() }
                        )
                    }
                    IconButton(
                        onClick = { onShareDocument(document) },
                        content = { ShareIcon() }
                    )
                }
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val pageWidthPx = with(density) { maxWidth.toPx() }
            val pageWidthDp = maxWidth
            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)

                            do {
                                val event = awaitPointerEvent()
                                val pointerCount = event.changes.count { it.pressed }
                                val canceled = event.changes.any { it.isConsumed }

                                if (!canceled) {
                                    when {
                                        pointerCount >= 2 -> {
                                            val gestureZoom = event.calculateZoom()
                                            val centroid = event.calculateCentroid()
                                            val newZoom = (zoom * gestureZoom).coerceIn(1F, 2f)

                                            translationX = translationX
                                                .minus(centroid.x * (newZoom - zoom))
                                                .coerceIn(-(pageWidthPx * (newZoom - 1F)), 0F)

                                            zoom = newZoom

                                            scope.launch {
                                                state.scrollBy(
                                                    centroid.y
                                                        .plus(state.firstVisibleItemScrollOffset)
                                                        .times(gestureZoom - 1F)
                                                )
                                            }
                                        }

                                        pointerCount == 1 -> {
                                            val pan = event.calculatePan()
                                            translationX = translationX
                                                .plus(pan.x * sqrt(zoom))
                                                .coerceIn(-(pageWidthPx * (zoom - 1F)), 0F)

                                            scope.launch {
                                                state.scrollBy(
                                                    -pan.y * sqrt(zoom)
                                                )
                                            }
                                        }
                                    }
                                    event.changes.forEach { it.consume() }
                                }
                            } while (!canceled && event.changes.any { it.pressed })
                        }
                    },
                state = state,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                overscrollEffect = null
            ) {
                items(
                    count = pageCount,
                    key = { index -> index },
                ) { index ->
                    val bitmap = pageBitmaps[index]
                    val size = pageSizes[index]

                    size?.let {
                        AsyncImage(
                            modifier = Modifier
                                .wrapContentWidth(Alignment.Start, unbounded = true)
                                .width { pageWidthDp * zoom }
                                .aspectRatio(size.width.toFloat() / size.height.toFloat())
                                .graphicsLayer { this.translationX = translationX },
                            model = bitmap,
                            contentDescription = null,
                            error = ColorPainter(Color.White),
                            fallback = ColorPainter(Color.White),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Modifier.width(width: () -> Dp) = layout { measurable, constraints ->
    val widthPx = width().roundToPx()
    val targetConstraints = constraints.copy(
        minWidth = widthPx,
        maxWidth = widthPx
    )
    val placeable = measurable.measure(targetConstraints)
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(0, 0)
    }
}