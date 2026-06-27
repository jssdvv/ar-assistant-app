package com.jssdvv.ara.schedule.presentation.destination.events.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.utility.recurrenceLabel
import com.jssdvv.ara.core.presentation.common.component.DeleteIcon
import com.jssdvv.ara.core.presentation.common.component.EditIcon
import com.jssdvv.ara.schedule.domain.model.Event
import com.jssdvv.ara.schedule.domain.type.RecurrenceUnit
import java.time.LocalDate

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall
                )
                event.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (event.recurrent && event.recurrenceUnit != RecurrenceUnit.ONCE) {
                    Text(
                        text = recurrenceLabel(event.quantity, event.recurrenceUnit),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (event.recurrent) {
                Icon(
                    painter = painterResource(R.drawable.ic_recurrent),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more_vert),
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.button_edit_action)) },
                        leadingIcon = { EditIcon() },
                        onClick = {
                            isMenuExpanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.button_delete_action),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        leadingIcon = {
                            DeleteIcon(tint = MaterialTheme.colorScheme.error)
                        },
                        onClick = {
                            isMenuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun EventCardPreview() {
    MaterialTheme {
        EventCard(
            event = Event(
                activityId = 1,
                title = "Machine Maintenance",
                description = "Monthly inspection and lubrication of machine components",
                date = LocalDate.now(),
                recurrent = true,
                quantity = 1,
                recurrenceUnit = RecurrenceUnit.MONTHS
            ),
            onClick = {},
            onEdit = {},
            onDelete = {}
        )
    }
}