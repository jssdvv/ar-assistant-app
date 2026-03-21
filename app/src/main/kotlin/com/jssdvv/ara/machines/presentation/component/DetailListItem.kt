package com.jssdvv.ara.machines.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.theme.spacing

@Composable
fun DetailListItem(
    headline: String,
    text: String,
    painter: Painter,
    modifier: Modifier = Modifier,
    iconDescription: String? = null,
    maxLines: Int = Int.MAX_VALUE,
    iconSize: Dp = 24.dp,
    iconRotation: Float = 0F
) {
    if (text.isNotBlank()) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            Box(
                modifier = Modifier.size(iconSize),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painter,
                    contentDescription = iconDescription,
                    modifier = Modifier
                        .size(iconSize)
                        .rotate(iconRotation),
                )
            }
            Column {
                Text(
                    text = headline,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = maxLines.coerceAtLeast(1)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailListItemShortPreview() {
    MaterialTheme {
        DetailListItem(
            painter = painterResource(R.drawable.ic_code),
            headline = stringResource(R.string.text_field_code_label),
            text = "This is a short text",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailListItemLargePreview() {
    MaterialTheme {
        DetailListItem(
            painter = painterResource(R.drawable.ic_code),
            headline = stringResource(R.string.text_field_code_label),
            text = "This is a long piece of text that spans multiple lines. It is " +
                    "designed to test how the Text composable handles wrapping and truncation " +
                    "when the maxLines parameter is set to 4. The text should ideally wrap to " +
                    "the next line if it exceeds the available width, but it should not exceed " +
                    "the specified number of lines.",
            maxLines = 4
        )
    }
}