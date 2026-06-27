package com.jssdvv.ara.schedule.presentation.destination.events

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jssdvv.ara.core.data.repository.NotificationService
import com.jssdvv.ara.machines.data.local.relation.MachineWithActivities
import com.jssdvv.ara.machines.domain.model.Activity
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineActivities
import com.jssdvv.ara.machines.domain.usecase.SelectMachines
import com.jssdvv.ara.schedule.domain.model.Event
import com.jssdvv.ara.schedule.domain.usecase.EventsDataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventsDataManager: EventsDataManager,
    private val notificationService: NotificationService,
    private val selectMachines: SelectMachines
) : ViewModel() {

    private val events: Flow<List<Event>> = eventsDataManager.select.invoke()

    private val currentDate = MutableStateFlow(LocalDate.now())
    private val displayedDate = MutableStateFlow(LocalDate.now())

    private val selectedMachine = MutableStateFlow<Machine?>(null)
    private val selectedActivity = MutableStateFlow<Activity?>(null)

    private val _navigationEvent = Channel<Int>(Channel.BUFFERED)
    val navigationEvent: Flow<Int> = _navigationEvent.receiveAsFlow()

    private val machinesWithActivities: Flow<List<MachineActivities>> =
        selectMachines.selectMachineWithActivities()

    val dialogState: StateFlow<EventDialogState> = combine(
        machinesWithActivities,
        selectedMachine,
        selectedActivity
    ) { machinesWithActivities, machine, activity ->
        EventDialogState(
            machinesWithActivities = machinesWithActivities,
            selectedMachine = machine,
            selectedActivity = activity
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = EventDialogState()
    )

    val uiState: StateFlow<EventsUiState> = combine(
        displayedDate,
        currentDate,
        events
    ) { displayed, selected, allEvents ->
        val eventCountPerDay = allEvents.groupBy { it.date }.mapValues { it.value.size }
        val selectedDayEvents = allEvents.filter { it.date == selected }
        EventsUiState.Success(
            currentDate = displayed,
            selectedDate = selected,
            events = selectedDayEvents,
            eventCountPerDay = eventCountPerDay
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = EventsUiState.Loading
    )

    fun onEvent(event: EventsEvent) {
        when (event) {
            is EventsEvent.PreviousMonth -> navigateToMonth(-1)
            is EventsEvent.NextMonth -> navigateToMonth(1)
            is EventsEvent.SelectMonth -> {
                displayedDate.update { event.date }
                currentDate.update { event.date }
            }
            is EventsEvent.SelectDay -> selectDay(event.date)
            is EventsEvent.CreateEvent -> createEvent(event.event)
            is EventsEvent.SelectEvent -> selectEvent(event.event)
            is EventsEvent.EditEvent -> editEvent(event.event)
            is EventsEvent.DeleteEvent -> deleteEvent(event.event)
            is EventsEvent.SelectMachine -> {
                selectedMachine.update { event.machine }
                selectedActivity.update { null }
            }
            is EventsEvent.SelectActivity -> {
                selectedActivity.update { event.activity }
            }
        }
    }

    private fun navigateToMonth(offset: Long) {
        displayedDate.update { it.withDayOfMonth(1).plusMonths(offset) }
    }

    private fun selectDay(date: LocalDate) {
        currentDate.update { date }
        displayedDate.update { date.withDayOfMonth(1) }
    }

    private suspend fun getMachineIdForActivity(activityId: Int): Int? {
        return machinesWithActivities.first()
            .firstOrNull { it.activities.any { activity -> activity.id == activityId } }
            ?.machine?.id
    }

    private fun selectEvent(event: Event) {
        viewModelScope.launch {
            val machineId = getMachineIdForActivity(event.activityId) ?: return@launch
            _navigationEvent.send(machineId)
        }
    }

    private fun createEvent(event: Event) {
        viewModelScope.launch {
            val id = eventsDataManager.upsert(event).toInt()
            if (id != -1) {
                notificationService.schedule(event.copy(id = id))
            }
        }
    }

    private fun editEvent(event: Event) {
        viewModelScope.launch {
            eventsDataManager.upsert(event)
            notificationService.reschedule(event)
        }
    }

    private fun deleteEvent(event: Event) {
        viewModelScope.launch {
            eventsDataManager.delete(event)
            notificationService.cancel(event)
        }
    }
}

sealed interface EventsEvent {
    data object PreviousMonth : EventsEvent
    data object NextMonth : EventsEvent
    data class SelectMonth(val date: LocalDate) : EventsEvent
    data class SelectDay(val date: LocalDate) : EventsEvent

    data class CreateEvent(val event: Event) : EventsEvent
    data class SelectEvent(val event: Event) : EventsEvent
    data class EditEvent(val event: Event) : EventsEvent
    data class DeleteEvent(val event: Event) : EventsEvent
    data class SelectMachine(val machine: Machine): EventsEvent
    data class SelectActivity(val activity: Activity): EventsEvent
}

sealed interface EventsUiState {
    data object Loading : EventsUiState

    @Immutable
    data class Success(
        val currentDate: LocalDate,
        val selectedDate: LocalDate,
        val events: List<Event> = emptyList(),
        val eventCountPerDay: Map<LocalDate, Int> = emptyMap()
    ) : EventsUiState
}

@Immutable
data class EventDialogState(
    val machinesWithActivities: List<MachineActivities> = emptyList(),
    val selectedMachine: Machine? = null,
    val selectedActivity: Activity? = null
)
