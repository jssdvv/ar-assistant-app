package com.jssdvv.ara.machines.presentation.destination.machines.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.foundation.component.BadgeIcon
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.foundation.component.HeroImageCard
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.presentation.component.DetailListItem

@Composable
fun MachineCard(
    machine: Machine,
    isSelected: Boolean,
    onClick: () -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    HeroImageCard(
        onClick = { if (isSelected) onNavigateToDetails(machine.id) else onClick() },
        isFocused = isSelected,
        imageUri = machine.imageUri,
        modifier = modifier,
        imageContentDescription = stringResource(R.string.machine_image_content_desc)
    ) {
        // Content Padding
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.extraLarge,
                    vertical = MaterialTheme.spacing.medium
                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            // Headline
            Row(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.weight(1F),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
                ) {
                    // Title
                    Text(
                        text = machine.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3
                    )

                    // Machine Code
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_code),
                            contentDescription = stringResource(R.string.icon_machine_code_content_desc),
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "M${machine.id}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Spacer(Modifier.width(MaterialTheme.spacing.extraLarge))

                // Machine Type Icon
                BadgeIcon(
                    painter = painterResource(machine.type.iconResId),
                    contentDescription = stringResource(machine.type.iconContentDescResId)
                )
            }

            HorizontalDivider()

            // Machine Specs Items
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                listOf(
                    Triple(
                        R.drawable.ic_model,
                        R.string.text_field_model_label,
                        machine.model
                    ),
                    Triple(
                        R.drawable.ic_serial_number,
                        R.string.text_field_serial_number_label,
                        machine.serial
                    ),
                    Triple(
                        R.drawable.ic_location,
                        R.string.text_field_location_label,
                        machine.location
                    )
                ).forEach { (iconId, headlineTextId, supportingText) ->
                    DetailListItem(
                        painter = painterResource(iconId),
                        headline = stringResource(headlineTextId),
                        text = supportingText ?: ""
                    )
                }
            }

            ButtonWithIcon(
                onClick = {
                    onClick()
                    onNavigateToDetails(machine.id)
                },
                modifier = Modifier.align(Alignment.End),
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_specs),
                        contentDescription = stringResource(R.string.icon_navigate_to_specs_from_machines_content_desc)
                    )
                }
            ) {
                Text(stringResource(R.string.button_navigate_to_specs_from_machines_label))
            }
        }
    }
}