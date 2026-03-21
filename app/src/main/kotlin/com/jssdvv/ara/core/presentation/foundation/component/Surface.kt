package com.jssdvv.ara.core.presentation.foundation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.common.NavigationUpIconButton
import com.jssdvv.ara.core.presentation.theme.cornerRadius

@Composable
fun SceneSurface(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Black,
    onNavigationUp: () -> Unit = {},
    actions: @Composable RowScope.(rowHeight: Dp) -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    val topItemsSize = DpSize(48.dp,48.dp)
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(containerColor)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .clip(
                    RoundedCornerShape(
                        topStart = MaterialTheme.cornerRadius.large,
                        topEnd = MaterialTheme.cornerRadius.large
                    )
                )
                .fillMaxSize(),
        ){
            content()

            Row(
                modifier = Modifier
                    .offset(-MaterialTheme.cornerRadius.small, MaterialTheme.cornerRadius.small)
                    .height(topItemsSize.height)
                    .align(Alignment.TopEnd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.cornerRadius.small),
                content = { actions(topItemsSize.height) }
            )

            NavigationUpIconButton(
                modifier = Modifier
                    .offset(MaterialTheme.cornerRadius.small, MaterialTheme.cornerRadius.small)
                    .size(topItemsSize)
                    .align(Alignment.TopStart),
                onNavigationUp = onNavigationUp,
                containerColor = Color.Black.copy(alpha = 0.5F),
                contentColor = Color.White
            )
        }
    }
}