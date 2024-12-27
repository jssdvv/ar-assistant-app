package com.jssdvv.ara.machinery.presentation.screen.activities_list.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ActivitiesListFAB(
    onNavigateToAddActivity: () -> Unit
) {
    ExtendedFloatingActionButton(
        text = { Text(text = "Nueva actividad") },
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        },
        onClick = onNavigateToAddActivity
    )
}