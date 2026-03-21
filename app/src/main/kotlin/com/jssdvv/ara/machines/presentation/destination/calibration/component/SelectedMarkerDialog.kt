package com.jssdvv.ara.machines.presentation.destination.calibration.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.CheckIcon
import com.jssdvv.ara.core.presentation.navigation.MarkerIcon
import com.jssdvv.ara.machines.domain.model.Marker

@Composable
fun SelectedMarkerDialog(
    currentMarker: Marker?,
    markers: List<Marker>,
    onDismissRequest: () -> Unit,
    onMarkerClick: (Marker) -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MarkerIcon()
                Text(text = "Calibrar Marcador") // todo create string
            }
        },
        text = {
            Column {
                markers.forEach { marker ->
                    val isCalibrated = marker.calibrated
                    val markerName = stringResource(
                        R.string.marker_codification,
                        marker.machineId,
                        marker.index
                    )

                    ListItem(
                        modifier = Modifier.clickable { onMarkerClick(marker) },
                        headlineContent = { Text(text = markerName) },
                        leadingContent = { if (currentMarker == marker) CheckIcon() },
                        trailingContent = { if (!isCalibrated) Text("Uncalibrated") }
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismissRequest) { Text(text = "Cancel") } },
    )
}