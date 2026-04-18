package com.jssdvv.ara.machines.presentation.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.jssdvv.ara.R

@Composable
fun RenderableIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_renderable),
    contentDescription: String? = null
) = Icon(
    painter = painter,
    contentDescription = contentDescription,
    modifier = modifier
)