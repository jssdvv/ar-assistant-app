package com.jssdvv.ara.schedule.presentation.destination.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.AddIcon
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheelScreen
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.schedule.domain.model.Event
import com.jssdvv.ara.schedule.presentation.destination.events.component.CalendarHeader
import com.jssdvv.ara.schedule.presentation.destination.events.component.CreateEventDialog
import com.jssdvv.ara.schedule.presentation.destination.events.component.DeleteEventDialog
import com.jssdvv.ara.schedule.presentation.destination.events.component.EditEventDialog
import com.jssdvv.ara.schedule.presentation.destination.events.component.EventCard
import com.jssdvv.ara.schedule.presentation.destination.events.component.EventsCalendar
import java.time.LocalDate

@Composable
fun EventsDestination(
    viewModel: EventsViewModel = hiltViewModel(),
) {
    EventsScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun EventsScreen(
    uiState: EventsUiState,
    onEvent: (EventsEvent) -> Unit,
) {
    when (uiState) {
        EventsUiState.Loading -> LoadingWheelScreen()

        is EventsUiState.Success -> {
            EventsContent(
                events = uiState.events,
                eventCountPerDay = uiState.eventCountPerDay,
                currentDate = uiState.currentDate,
                selectedDate = uiState.selectedDate,
                onEvent = onEvent,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsContent(
    events: List<Event>,
    eventCountPerDay: Map<LocalDate, Int>,
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onEvent: (EventsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isCalendarVisible by remember { mutableStateOf(true) }

    var showCreateDialog by remember { mutableStateOf(false) }

    var eventToEdit by remember { mutableStateOf<Event?>(null) }
    var eventToDelete by remember { mutableStateOf<Event?>(null) }

    if (showCreateDialog) {
        CreateEventDialog(
            selectedDate = selectedDate,
            onConfirm = {
                onEvent(EventsEvent.CreateEvent(it))
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }
    eventToEdit?.let { event ->
        EditEventDialog(
            event = event,
            onConfirm = {
                onEvent(EventsEvent.EditEvent(it))
                eventToEdit = null
            },
            onDismiss = { eventToEdit = null }
        )
    }
    eventToDelete?.let { event ->
        DeleteEventDialog(
            event = event,
            onConfirm = {
                onEvent(EventsEvent.DeleteEvent(event))
                eventToDelete = null
            },
            onDismiss = { eventToDelete = null }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { AddIcon() },
                text = { Text(stringResource(R.string.fab_events_create_action)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            CalendarHeader(
                displayedDate = currentDate,
                onToggleCalendar = { isCalendarVisible = !isCalendarVisible }
            )
            EventsCalendar(
                currentDate = currentDate,
                selectedDate = selectedDate,
                isVisible = isCalendarVisible,
                eventCountPerDay = eventCountPerDay,
                onPreviousMonth = { onEvent(EventsEvent.PreviousMonth) },
                onNextMonth = { onEvent(EventsEvent.NextMonth) },
                onSelectMonth = { onEvent(EventsEvent.SelectMonth) },
                onEvent = onEvent,
            )
            HorizontalDivider()

            if (events.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(MaterialTheme.spacing.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.screen_events_empty_message),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = MaterialTheme.spacing.medium),
                    contentPadding = PaddingValues(top = MaterialTheme.spacing.small, bottom = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    items(
                        items = events,
                        key = { it.id }
                    ) { event ->
                        EventCard(
                            event = event,
                            onClick = { onEvent(EventsEvent.SelectEvent(event)) },
                            onEdit = { eventToEdit = event },
                            onDelete = { eventToDelete = event }
                        )
                    }
                }
            }
        }
    }
}
