package com.jssdvv.ara.machines.presentation.destination.markers.component

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.CheckListIcon
import com.jssdvv.ara.core.presentation.common.component.CloseIcon
import com.jssdvv.ara.core.presentation.common.component.ShareIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkersTopBar(
    modifier: Modifier = Modifier,
    isSelectionMode: Boolean,
    onClearSelectedItems: () -> Unit,
    onSelectionModeChange: (Boolean) -> Unit,
    selectedCountItems: Int,
    onNavigateBack: () -> Unit,
    onExportSelectedMarkers: () -> Unit,
) {
    val context = LocalContext.current
    TopAppBar(
        modifier = modifier,
        title = {
            if (isSelectionMode && selectedCountItems > 0) {
                Text(
                    text = "$selectedCountItems Markers"
                )
            } else {
                Text(
                    text = "Markers"
                )
            }
        },
        navigationIcon = {
            if (isSelectionMode) {
                IconButton(
                    onClick = {
                        onClearSelectedItems()
                        onSelectionModeChange(false)
                    },
                    content = { CloseIcon() }
                )
            } else {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = ""
                    )
                }
            }
        },
        actions = {
            if (!isSelectionMode) {
                IconButton(
                    onClick = { onSelectionModeChange(true) },
                    content = { CheckListIcon() }
                )
            } else {
                IconButton(
                    onClick = {
                        if(selectedCountItems > 0){
                            onExportSelectedMarkers()
                        } else {
                            Toast.makeText(
                                context,
                                "No items selected for export",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    content = { ShareIcon() }
                )
            }
        }
    )
}