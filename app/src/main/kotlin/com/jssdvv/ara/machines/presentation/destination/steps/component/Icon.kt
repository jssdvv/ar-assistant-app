package com.jssdvv.ara.machines.presentation.destination.steps.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.jssdvv.ara.R

@Composable
fun PlayIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_play),
    contentDescription: String? = null
) = Icon(
    painter = painter,
    contentDescription = contentDescription,
    modifier = modifier
)

@Composable
fun LoopIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_loop),
    contentDescription: String? = null
) = Icon(
    painter = painter,
    contentDescription = contentDescription,
    modifier = modifier
)

@Composable
fun SkipNextOperationIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_skip_next),
    contentDescription: String? = null
) = Icon(
    painter = painter,
    contentDescription = contentDescription,
    modifier = modifier
)

@Composable
fun SkipPreviousOperationIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_skip_next),
    contentDescription: String? = null
) = Icon(
    painter = painter,
    contentDescription = contentDescription,
    modifier = modifier.rotate(180F)
)

@Composable
fun SkipNextStepIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_skip_next_page),
    contentDescription: String? = null
) = Icon(
    painter = painter,
    contentDescription = contentDescription,
    modifier = modifier
)

@Composable
fun SkipPreviousStepIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_skip_next_page),
    contentDescription: String? = null
) = Icon(
    painter = painter,
    contentDescription = contentDescription,
    modifier = modifier.rotate(180F)
)

@Composable
fun StepIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_step),
    contentDescription: String? = null
) = Icon(
    painter = painter,
    contentDescription = contentDescription,
    modifier = modifier
)

@Composable
fun AnimationIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_animation),
    contentDescription: String? = null
) = Icon(
    painter = painter,
    contentDescription = contentDescription,
    modifier = modifier
)

@Composable
fun ChangeIcon(
    modifier: Modifier = Modifier,
    painter : Painter = painterResource(R.drawable.ic_change),
    contentDescription: String? = null
) = Icon(
    painter = painter,
    contentDescription =contentDescription,
    modifier = modifier
)