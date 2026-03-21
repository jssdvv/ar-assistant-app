package com.jssdvv.ara.core.presentation.foundation.component

import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.domain.utility.horizontalMirrored
import com.jssdvv.ara.core.presentation.common.ArrowDownIcon

@Composable
fun MenuTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    content: @Composable () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        enabled = enabled,
        colors = colors,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding.horizontalMirrored()
    ) {
        content()
        ArrowDownIcon()
    }
}