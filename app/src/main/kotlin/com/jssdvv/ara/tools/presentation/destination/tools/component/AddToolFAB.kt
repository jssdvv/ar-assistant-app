package com.jssdvv.ara.tools.presentation.destination.tools.component

import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.AddIcon

@Composable
fun AddToolFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
    ) {
        AddIcon(
            contentDescription = stringResource(R.string.button_add_action)
        )
    }
}