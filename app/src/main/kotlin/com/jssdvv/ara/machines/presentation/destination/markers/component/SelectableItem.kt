package com.jssdvv.ara.machines.presentation.destination.markers.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.navigation.MarkerIcon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectableItem(
    machineId: Int,
    markerIndex: Int,
    markerSize: Float,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongPress: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .combinedClickable(
                onClick = { if (isSelectionMode) onSelectionChange(!isSelected) else onClick() },
                onLongClick = { onLongPress(isSelected) }
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isSelectionMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = null
            )
        } else {
            MarkerIcon()
        }
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = "M${machineId}P${markerIndex}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = "Tamaño: $markerSize cm",
            style = MaterialTheme.typography.labelMedium
        )
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
}