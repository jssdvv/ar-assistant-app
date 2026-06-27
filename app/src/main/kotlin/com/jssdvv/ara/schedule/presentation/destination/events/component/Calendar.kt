package com.jssdvv.ara.schedule.presentation.destination.events.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.ArrowDropDownIcon
import com.jssdvv.ara.core.presentation.common.component.ArrowPreviousItemIcon
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.schedule.presentation.destination.events.EventsEvent
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

private val dotColors = listOf(
    Color(0xFF4CAF50),
    Color(0xFF2196F3),
    Color(0xFFFF9800)
)

@Composable
private fun EventDots(
    count: Int,
    modifier: Modifier = Modifier,
) {
    if (count == 0) return

    val maxDots = 4
    val showDots = minOf(count, maxDots)
    val hasMore = count > maxDots

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until showDots) {
            val isLastMore = (i == showDots - 1) && hasMore
            val dotColor = if (isLastMore) {
                Color.White.copy(alpha = 0.8f)
            } else {
                dotColors[i % dotColors.size]
            }
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

@Composable
internal fun CalendarDay(
    day: Int,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    eventCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isToday -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        isToday -> MaterialTheme.colorScheme.onPrimary
        !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4F)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                enabled = isCurrentMonth,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            EventDots(count = eventCount)
        }
    }
}

@Composable
internal fun CalendarHeader(
    displayedDate: LocalDate,
    onToggleCalendar: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = displayedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = displayedDate.year.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = onToggleCalendar
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_date),
                contentDescription = stringResource(R.string.icon_toggle_calendar_content_desc)
            )
        }
    }
}

@Composable
fun EventsCalendar(
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectMonth: () -> Unit,
    currentDate: LocalDate,
    selectedDate: LocalDate,
    isVisible: Boolean,
    eventCountPerDay: Map<LocalDate, Int>,
    onEvent: (EventsEvent) -> Unit,
) {
    val today = LocalDate.now()
    val currentMonthDays = currentDate.lengthOfMonth()
    val firstDayOfWeek = currentDate.withDayOfMonth(1).dayOfWeek.value % 7
    val nextMonthDays = 42 - firstDayOfWeek - currentMonthDays
    val daysOfWeek = stringArrayResource(R.array.calendar_days_symbol_designation)

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(
            animationSpec = tween(300),
            expandFrom = Alignment.Top
        ),
        exit = shrinkVertically(
            animationSpec = tween(300),
            shrinkTowards = Alignment.Top
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onPreviousMonth) {
                        ArrowPreviousItemIcon(
                            contentDescription = stringResource(R.string.icon_previous_month_content_desc)
                        )
                    }
                    TextButton(onClick = onSelectMonth) {
                        Text(stringResource(R.string.button_select_month_label))
                    }
                    IconButton(onClick = onNextMonth) {
                        ArrowPreviousItemIcon(
                            modifier = Modifier.graphicsLayer { rotationZ = 180F },
                            contentDescription = stringResource(R.string.icon_next_month_content_desc)
                        )
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.medium)
                    .padding(bottom = MaterialTheme.spacing.medium),
                contentPadding = PaddingValues(vertical = MaterialTheme.spacing.small)
            ) {
                items(daysOfWeek) { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.medium)
                    )
                }

                items(firstDayOfWeek) { index ->
                    val prevMonth = currentDate.withDayOfMonth(1).minusMonths(1)
                    val day = prevMonth.lengthOfMonth() - firstDayOfWeek + index + 1
                    val date = prevMonth.withDayOfMonth(day)
                    CalendarDay(
                        day = day,
                        isCurrentMonth = false,
                        isToday = date == today,
                        isSelected = date == selectedDate,
                        eventCount = eventCountPerDay[date] ?: 0,
                        onClick = { onEvent(EventsEvent.SelectDay(date)) }
                    )
                }

                items(currentMonthDays) { index ->
                    val day = index + 1
                    val date = currentDate.withDayOfMonth(day)
                    CalendarDay(
                        day = day,
                        isCurrentMonth = true,
                        isToday = date == today,
                        isSelected = date == selectedDate,
                        eventCount = eventCountPerDay[date] ?: 0,
                        onClick = { onEvent(EventsEvent.SelectDay(date)) }
                    )
                }

                items(nextMonthDays) { index ->
                    val day = index + 1
                    val date = currentDate.withDayOfMonth(1).plusMonths(1).withDayOfMonth(day)
                    CalendarDay(
                        day = date.dayOfMonth,
                        isCurrentMonth = false,
                        isToday = date == today,
                        isSelected = date == selectedDate,
                        eventCount = eventCountPerDay[date] ?: 0,
                        onClick = { onEvent(EventsEvent.SelectDay(date)) }
                    )
                }
            }
        }
    }
}

@Composable
fun MonthYearPickerDialog(
    currentDate: LocalDate,
    onMonthYearSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    var displayedYear by remember { mutableStateOf(currentDate.year) }
    var isYearMode by remember { mutableStateOf(false) }

    val yearGridStart = remember(displayedYear, isYearMode) {
        displayedYear - 6
    }

    val months = remember {
        Month.entries.toList()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            TextButton(onClick = { isYearMode = !isYearMode }) {
                Text(
                    text = displayedYear.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isYearMode)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                val rotation by animateFloatAsState(
                    targetValue = if (isYearMode) 180F else 0F
                )
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    ArrowDropDownIcon(
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
        },
        text = {
            AnimatedContent(
                targetState = isYearMode,
                transitionSpec = {
                    if (targetState) {
                        slideInVertically { it } + fadeIn() togetherWith
                                slideOutVertically { -it } + fadeOut()
                    } else {
                        slideInVertically { -it } + fadeIn() togetherWith
                                slideOutVertically { it } + fadeOut()
                    }
                },
                label = "month_year_picker"
            ) { yearMode ->
                if (yearMode) {
                    YearGrid(
                        yearGridStart = yearGridStart,
                        selectedYear = displayedYear,
                        currentYear = LocalDate.now().year,
                        onYearSelected = { year ->
                            displayedYear = year
                            isYearMode = false
                        }
                    )
                } else {
                    MonthGrid(
                        selectedMonth = if (currentDate.year == displayedYear)
                            currentDate.month else null,
                        currentMonth = if (LocalDate.now().year == displayedYear)
                            LocalDate.now().month else null,
                        months = months,
                        onMonthSelected = { month ->
                            onMonthYearSelected(
                                LocalDate.of(displayedYear, month, 1)
                            )
                            onDismiss()
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_event_cancel_action))
            }
        }
    )
}

@Composable
private fun MonthGrid(
    months: List<Month>,
    selectedMonth: Month?,
    currentMonth: Month?,
    onMonthSelected: (Month) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false
    ) {
        items(months) { month ->
            val isSelected = month == selectedMonth
            val isCurrent = month == currentMonth
            MonthYearChip(
                label = month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    .replaceFirstChar { it.uppercase() },
                isSelected = isSelected,
                isCurrent = isCurrent,
                onClick = { onMonthSelected(month) }
            )
        }
    }
}

@Composable
private fun YearGrid(
    yearGridStart: Int,
    selectedYear: Int,
    currentYear: Int,
    onYearSelected: (Int) -> Unit,
) {
    val years = remember(yearGridStart) {
        (yearGridStart until yearGridStart + 16).toList()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false
    ) {
        items(years) { year ->
            MonthYearChip(
                label = year.toString(),
                isSelected = year == selectedYear,
                isCurrent = year == currentYear,
                onClick = { onYearSelected(year) }
            )
        }
    }
}

@Composable
private fun MonthYearChip(
    label: String,
    isSelected: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isCurrent -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
    val borderColor = when {
        isCurrent && !isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}