package com.jssdvv.ara.machines.presentation.destination.activities.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.EditButtonWithIcon
import com.jssdvv.ara.core.presentation.foundation.component.BadgeIcon
import com.jssdvv.ara.core.presentation.foundation.component.ButtonWithIcon
import com.jssdvv.ara.core.presentation.foundation.component.HeroImageCard
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.Activity

@Composable
fun ActivityCard(
    onClick: () -> Unit,
    isSelected: Boolean,
    activity: Activity,
    onNavigateToAnimations: () -> Unit,
    onNavigateToARSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeroImageCard(
        onClick = { if (isSelected) onNavigateToARSession() else onClick() },
        isFocused = isSelected,
        imageUri = activity.imageUri,
        modifier = modifier,
        imageContentDescription = stringResource(R.string.activity_image_content_desc)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.extraLarge,
                    vertical = MaterialTheme.spacing.medium
                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.weight(1F),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    // Title
                    Text(
                        text = activity.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3
                    )

                    // Activity Frequency
                    Text(
                        text = "${activity.frequency ?: ""} ${activity.frequencyUnit ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(Modifier.width(MaterialTheme.spacing.extraLarge))

                // Activity Type Icon
                BadgeIcon(
                    painter = painterResource(activity.type.iconResId),
                    contentDescription = stringResource(activity.type.iconContentDescResId)
                )
            }

            HorizontalDivider()

            // Activity Description
            Text(
                text = activity.description ?: "N/A",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
            )

            // Buttons
            FlowRow(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                EditButtonWithIcon(
                    onClick = onNavigateToAnimations,
                    isOutlined = true
                )
                ButtonWithIcon(
                    onClick = onNavigateToARSession,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_augmented_reality_outlined),
                            contentDescription = stringResource(R.string.icon_navigate_to_ar_session_from_activities_content_desc)
                        )
                    },
                    content = { Text(stringResource(R.string.button_navigate_to_ar_session_from_activities_label)) }
                )
            }
        }
    }
}