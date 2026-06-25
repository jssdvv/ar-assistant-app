package com.jssdvv.ara.core.presentation.foundation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.common.component.NavigationUpIconButton
import com.jssdvv.ara.core.presentation.theme.cornerRadius

@Composable
fun ARSceneSurface(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Black,
    onNavigationUp: () -> Unit = {},
    trailingAction: @Composable BoxScope.(size: DpSize) -> Unit = {},
    optionsRow: @Composable RowScope.(rowHeight: Dp) -> Unit = {},
    notificationChip: @Composable BoxScope.() -> Unit = {},
    bottomSheet: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val trailingActionSize = DpSize(48.dp, 48.dp)
    val optionsRowHeight = 40.dp
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(containerColor)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .clip(RoundedCornerShape(MaterialTheme.cornerRadius.large))
                .fillMaxSize(),
        ) {
            content()
            NavigationUpIconButton(
                modifier = Modifier
                    .offset(MaterialTheme.cornerRadius.small, MaterialTheme.cornerRadius.small)
                    .size(trailingActionSize)
                    .align(Alignment.TopStart),
                onNavigationUp = onNavigationUp,
                containerColor = Color.Black.copy(alpha = 0.3F),
                contentColor = Color.White
            )
            Row(
                modifier = Modifier
                    .offset(y = 12.dp)
                    .height(optionsRowHeight)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = { optionsRow(optionsRowHeight) }
            )
            Box(
                modifier = Modifier
                    .offset(y = 60.dp)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center,
                content = { notificationChip() }
            )
            Box(
                modifier = Modifier
                    .offset(-MaterialTheme.cornerRadius.small, MaterialTheme.cornerRadius.small)
                    .size(trailingActionSize)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center,
                content = { trailingAction(trailingActionSize) }
            )
            Box(
                modifier = Modifier.align(Alignment.BottomCenter),
                content = { bottomSheet() }
            )
        }
    }
}

@Composable
fun SearchBarSurface(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    bottomRow: @Composable (() -> Unit)? = null,
    floatingActionButton: @Composable (() -> Unit) = {},
    isExpandable: Boolean = false,
    content: @Composable (() -> Unit)
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SearchTopBar(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                bottomRow = bottomRow,
                navigationIcon = navigationIcon,
                isExpandable = isExpandable
            )
        },
        content = {
            Box(
                modifier = Modifier.padding(it),
                content = { content() }
            )
        },
        floatingActionButton = floatingActionButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyColumnScaffold(
    topBarTitle: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit) = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    floatingActionButton: @Composable (() -> Unit) = {},
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: LazyListScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(topBarTitle) },
                navigationIcon = navigationIcon,
                actions = actions
            )
        },
        content = {
            LazyColumn(
                modifier = modifier
                    .padding(it)
                    .fillMaxSize(),
                state = lazyListState,
                contentPadding = contentPadding,
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        },
        floatingActionButton = floatingActionButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxedScaffold(
    topBarTitle: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit) = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    floatingActionButton: @Composable (() -> Unit) = {},
    content: @Composable BoxScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(topBarTitle) },
                navigationIcon = navigationIcon,
                actions = actions
            )
        },
        content = {
            Box(
                modifier = modifier
                    .padding(it)
                    .fillMaxSize(),
                content = content
            )
        },
        floatingActionButton = floatingActionButton
    )
}