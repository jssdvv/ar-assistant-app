package com.jssdvv.ara.core.presentation.foundation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.core.domain.utility.horizontalMirrored
import com.jssdvv.ara.core.presentation.theme.cornerRadius
import com.jssdvv.ara.core.presentation.theme.spacing

@Composable
fun CounterButton(
    onClick: () -> Unit,
    title: String,
    count: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    counterContainerColor: Color = MaterialTheme.colorScheme.primary,
    counterContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    icon: @Composable () -> Unit,
) {
    val minWith = 100.dp
    val minHeight = 70.dp
    val radius = MaterialTheme.cornerRadius.small
    Column(
        modifier = modifier
            .sizeIn(minWith, minHeight)
            .size(minWith, minHeight)
            .clickable { onClick() }
            .clip(RoundedCornerShape(radius)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .background(containerColor)
                .wrapContentSize(Alignment.Center),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .background(counterContainerColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides counterContentColor) {
                icon()
                Spacer(Modifier.width(MaterialTheme.spacing.small))
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
fun ButtonWithIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    border: BorderStroke? = null,
    iconInFront: Boolean = true,
    icon: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        enabled = enabled,
        colors = colors,
        border = border,
        contentPadding = if (iconInFront) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ButtonWithIconContentPadding.horizontalMirrored()
        },
        content = {
            if (iconInFront) {
                icon()
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                content()
            } else {
                content()
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                icon()
            }
        }
    )
}