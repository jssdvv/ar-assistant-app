package com.jssdvv.ara.core.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R

@Composable
fun ActivitiesIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_activities),
    contentDescription: String = stringResource(R.string.icon_activities_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun CalibrationIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_calibration),
    contentDescription: String = stringResource(R.string.icon_calibration_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun DocumentsIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_documents),
    contentDescription: String = stringResource(R.string.icon_documents_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)

@Composable
fun MarkerIcon(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_markers),
    contentDescription: String = stringResource(R.string.icon_markers_content_desc),
) = Icon(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
)