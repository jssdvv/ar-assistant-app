package com.jssdvv.ara.machines.presentation.destination.calibration.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.presentation.common.AddIcon
import com.jssdvv.ara.core.presentation.common.RemoveIcon

@Composable
fun SubtractButton(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        start = 12.dp,
        end = 8.dp,
        top = 8.dp,
        bottom = 8.dp
    ),
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.semantics { role = Role.Button },
        onClick = onClick,
        shape = RoundedCornerShape(
            topStart = CornerSize(50),
            topEnd = CornerSize(4.dp),
            bottomStart = CornerSize(50),
            bottomEnd = CornerSize(4.dp)
        ),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        border = ButtonDefaults.outlinedButtonBorder(),
    ) {
        Row(
            Modifier
                .defaultMinSize(
                    minHeight = 40.dp,
                    minWidth = 40.dp
                )
                .padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RemoveIcon(modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        start = 8.dp,
        end = 12.dp,
        top = 8.dp,
        bottom = 8.dp
    ),
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.semantics { role = Role.Button },
        onClick = onClick,
        shape = RoundedCornerShape(
            topStart = CornerSize(4.dp),
            topEnd = CornerSize(50),
            bottomStart = CornerSize(4.dp),
            bottomEnd = CornerSize(50)
        ),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        border = ButtonDefaults.outlinedButtonBorder(),
    ) {
        Row(
            Modifier
                .defaultMinSize(
                    minHeight = 40.dp,
                    minWidth = 40.dp
                )
                .padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AddIcon(modifier = Modifier.size(18.dp))
        }
    }
}