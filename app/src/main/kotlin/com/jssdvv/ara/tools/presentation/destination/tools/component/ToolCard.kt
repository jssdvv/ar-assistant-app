package com.jssdvv.ara.tools.presentation.destination.tools.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.EditIcon
import com.jssdvv.ara.core.presentation.foundation.component.FocusableCard
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Tool

@Composable
fun ToolCard(
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    tool: Tool,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    FocusableCard(
        onClick = onClick,
        isFocused = isSelected,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (tool.bodyMediaUri != null) {
                    AsyncImage(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(MaterialTheme.spacing.small)),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(tool.bodyMediaUri)
                            .crossfade(true)
                            .build(),
                        error = ColorPainter(Color.Gray),
                        fallback = ColorPainter(Color.Gray),
                        contentDescription = stringResource(R.string.image_tool_body_content_desc),
                        contentScale = ContentScale.Crop
                    )
                }
                if (tool.symbolMediaUri != null) {
                    AsyncImage(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(MaterialTheme.spacing.small)),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(tool.symbolMediaUri)
                            .crossfade(true)
                            .build(),
                        error = ColorPainter(Color.Gray),
                        fallback = ColorPainter(Color.Gray),
                        contentDescription = stringResource(R.string.image_tool_symbol_content_desc),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = MaterialTheme.spacing.small)
            ) {
                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.titleMedium
                )
                tool.code?.let { code ->
                    Text(
                        text = code,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = stringResource(tool.type.labelResId),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(onClick = onEditClick) {
                EditIcon()
            }
        }
    }
}
