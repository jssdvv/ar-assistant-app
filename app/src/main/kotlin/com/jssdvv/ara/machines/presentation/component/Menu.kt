package com.jssdvv.ara.machines.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.foundation.component.MenuTextButton
import com.jssdvv.ara.machines.domain.type.measurement.Translation

@Composable
fun TranslationMeasurementMenu(
    translation: Translation,
    onMeasurementChange: (Translation) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            MenuTextButton(
                onClick = { expanded = !expanded },
                content = { Text(stringResource(translation.symbolTextId)) }
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Translation.entries.forEach {
                DropdownMenuItem(
                    text = { Text(stringResource(it.symbolTextId)) },
                    onClick = { onMeasurementChange(it) }
                )
            }
        }
    }
}