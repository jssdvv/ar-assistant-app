package com.jssdvv.ara.machines.presentation.destination.markers.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.DeleteIcon
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheel
import com.jssdvv.ara.machines.domain.model.Marker

@Composable
fun MarkerDialog(
    currentMarker: Marker,
    index: Int?,
    onIndexChange: (Int) -> Unit,
    qrBitmap: Bitmap?,
    sizeCentimeters: Float?,
    onSizeChange: (Float) -> Unit,
    usedMarkerIndexes: List<Int>,
    onDismissRequest: () -> Unit,
    onSaveMarker: () -> Unit,
    onDeleteMarker: () -> Unit
) {
    val isNewMarker = currentMarker.id == 0
    val usedIndexes = usedMarkerIndexes - currentMarker.index

    // The marker index initial input
    var markerIndex by rememberSaveable { mutableStateOf(index.toString()) }

    // The marker size initial input converted to centimeters
    var markerSizeCentimeters by rememberSaveable { mutableStateOf(sizeCentimeters.toString()) }

    // This regex ensures that the markerIndex is a number non-empty value
    val validIndexRegex = "^[1-9]\\d*\$".toRegex()

    val isIndexNumeric = markerIndex.matches(validIndexRegex)
    val isIndexUsed = markerIndex.toIntOrNull()?.let { it in usedIndexes } ?: false

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    if (isNewMarker) stringResource(R.string.marker_dialog_create_title)
                    else stringResource(R.string.marker_dialog_edit_title)
                )
                IconButton(
                    onClick = onDeleteMarker
                ) {
                    DeleteIcon()
                }
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                qrBitmap?.let {
                    Image(
                        modifier = Modifier
                            .size(210.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        bitmap = it.asImageBitmap(),
                        contentDescription = stringResource(R.string.marker_dialog_barcode_content_desc)
                    )
                } ?: LoadingWheel(modifier = Modifier.size(210.dp))

                // The marker codification, e.g. M1P1
                Text(
                    text = "M${currentMarker.machineId}P${markerIndex}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.size(8.dp))

                // The marker index
                OutlinedTextField(
                    value = markerIndex,
                    onValueChange = { index ->
                        val regex = "^(\\d+)?$".toRegex()
                        if (index.matches(regex)) {
                            markerIndex = index
                            if (index.isNotBlank()) onIndexChange(index.toInt())
                        }
                    },
                    label = { Text("Índice de Marcador") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = !isIndexNumeric && !isIndexUsed,
                    supportingText = {
                        when {
                            !markerIndex.matches(validIndexRegex) -> {
                                Text(
                                    "El índice no puede estar vacio",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            markerIndex.toIntOrNull() in usedIndexes -> {
                                Text(
                                    "Este índice ya estaba en uso",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                )

                // The marker size in centimeters
                OutlinedTextField(
                    value = markerSizeCentimeters,
                    onValueChange = { sizeCentimeters ->
                        val regex = "^(\\d+(\\.\\d*)?)?\$".toRegex()
                        if (sizeCentimeters.matches(regex)) {
                            markerSizeCentimeters = sizeCentimeters
                            onSizeChange(
                                if (markerSizeCentimeters.isBlank()) 0f
                                else markerSizeCentimeters.toFloat()
                            )
                        }
                    },
                    label = { Text("Tamaño del Marcador (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveMarker()
                    onDismissRequest()
                },
                enabled = isIndexNumeric && !isIndexUsed && qrBitmap != null
            ) {
                Text(if (isNewMarker) "Agregar" else "Editar")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}

