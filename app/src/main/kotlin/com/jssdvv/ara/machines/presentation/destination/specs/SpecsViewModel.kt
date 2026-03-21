package com.jssdvv.ara.machines.presentation.destination.specs

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineDetails
import com.jssdvv.ara.machines.domain.model.machine.MachineSpecs
import com.jssdvv.ara.machines.domain.model.machine.MotorIdentity
import com.jssdvv.ara.machines.domain.model.machine.MotorSpecs
import com.jssdvv.ara.machines.domain.usecase.CountActivities
import com.jssdvv.ara.machines.domain.usecase.CountDocuments
import com.jssdvv.ara.machines.domain.usecase.SelectMachineAndDetails
import com.jssdvv.ara.machines.domain.usecase.SelectMotors
import com.jssdvv.ara.machines.domain.usecase.UpdateMotorSpecs
import com.jssdvv.ara.machines.domain.usecase.UpsertMachineSpecs
import com.jssdvv.ara.machines.domain.usecase.UpsertMachines
import com.jssdvv.ara.machines.presentation.destination.specs.MachineDetailsCard.MACHINE
import com.jssdvv.ara.machines.presentation.destination.specs.MachineDetailsCard.MACHINE_SPECS
import com.jssdvv.ara.machines.presentation.destination.specs.MachineDetailsCard.MOTOR
import com.jssdvv.ara.machines.presentation.destination.specs.MachineDetailsCard.MOTOR_SPECS
import com.jssdvv.ara.machines.presentation.destination.specs.MachineDetailsCard.NONE
import com.jssdvv.ara.machines.presentation.navigation.MachinesGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MachineDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val selectMachineAndDetailsUseCase: SelectMachineAndDetails,
    private val upsertMachinesUseCase: UpsertMachines,
    private val upsertMachineSpecsUseCase: UpsertMachineSpecs,
    private val selectMotorsUseCase: SelectMotors,
    private val updateMotorSpecsUseCase: UpdateMotorSpecs,
    private val countActivitiesUseCase: CountActivities,
    private val countDocumentsUseCase: CountDocuments,
) : ViewModel() {

    private val machineId = savedStateHandle.toRoute<MachinesGraph.SpecsRoute>().machineId

    private val machineState = MutableStateFlow<Machine?>(null)
    private val machineSpecsState = MutableStateFlow<MachineSpecs?>(null)
    private val motorIdentityState = MutableStateFlow<MotorIdentity?>(null)
    private val motorSpecsState = MutableStateFlow<MotorSpecs?>(null)
    private val counters = MutableStateFlow(MachineDetailsCounters())

    private val currentCard = MutableStateFlow(NONE)
    private val isEditingCard = MutableStateFlow(false)

    init {
        getCounters(machineId)
        viewModelScope.launch {
            getMachineDetails(machineId).also { machineDetails ->
                machineState.value = machineDetails.machine
                machineSpecsState.value = machineDetails.machineSpecs
                motorIdentityState.value = machineDetails.motorIdentity
                motorSpecsState.value = machineDetails.motorSpecs
            }
        }
    }

    val uiState: StateFlow<MachineDetailsUiState> = combine(
        currentCard,
        isEditingCard,
        counters,
        ::MachineDetailsUiState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MachineDetailsUiState()
    )

    val cardsUiState: StateFlow<MachineDetailsCardsUiState> = combine(
        machineState,
        machineSpecsState,
        motorIdentityState,
        motorSpecsState,
        MachineDetailsCardsUiState::Success
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MachineDetailsCardsUiState.Loading
    )

    fun uiEvent(event: MachineDetailsUiEvent) {
        when (event) {
            is MachineDetailsUiEvent.OnEditCard -> {
                currentCard.value = event.card
            }

            is MachineDetailsUiEvent.OnSaveMachineIdentificationCard -> {
                machineState.value = machineState.value?.copy(
                    // Note: createdAt should never be updated
                    code = event.machine.code,
                    name = event.machine.name,
                    type = event.machine.type,
                    location = event.machine.location,
                    brand = event.machine.brand,
                    model = event.machine.model,
                    serial = event.machine.serial,
                    fabricationYear = event.machine.fabricationYear,
                    price = event.machine.price,
                    acquisitionDate = event.machine.acquisitionDate,
                    modifiedAt = Date(System.currentTimeMillis()),
                )
                machineState.value?.let {
                    viewModelScope.launch {
                        try {
                            upsertMachinesUseCase(it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                currentCard.value = NONE
            }

            is MachineDetailsUiEvent.OnSaveMachineSpecificationsCard -> {
                machineSpecsState.value = machineSpecsState.value?.copy(
                    // Note: createdAt should never be updated
                    serviceCapacity = event.machineSpecs.serviceCapacity,
                    speed = event.machineSpecs.speed,
                    lubricant = event.machineSpecs.lubricant,
                    powerSupply = event.machineSpecs.powerSupply,
                    weight = event.machineSpecs.weight,
                    height = event.machineSpecs.height,
                    length = event.machineSpecs.length,
                    width = event.machineSpecs.width,
                    jobDesc = event.machineSpecs.jobDesc,
                    hoursPerDay = event.machineSpecs.hoursPerDay,
                    roomTemp = event.machineSpecs.roomTemp,
                    additionalDesc = event.machineSpecs.additionalDesc,
                    modifiedAt = Date(System.currentTimeMillis())
                )
                machineSpecsState.value?.let {
                    viewModelScope.launch {
                        try {
                            upsertMachineSpecsUseCase(it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                currentCard.value = NONE
            }

            is MachineDetailsUiEvent.OnSaveMotorIdentificationCard -> {
                motorIdentityState.value = motorIdentityState.value?.copy(
                    // Note: createdAt should never be updated
                    brand = event.motorIdentity.brand,
                    model = event.motorIdentity.model,
                    serialNumber = event.motorIdentity.serialNumber,
                    productNumber = event.motorIdentity.productNumber,
                    fabricationCountry = event.motorIdentity.fabricationCountry,
                    standards = event.motorIdentity.standards,
                    fabricationYear = event.motorIdentity.fabricationYear,
                    price = event.motorIdentity.price,
                    acquisitionDate = event.motorIdentity.acquisitionDate,
                    imageUri = event.motorIdentity.imageUri,
                    modifiedAt = Date(System.currentTimeMillis()),
                )
                motorIdentityState.value?.let {
                    viewModelScope.launch {
                        try {
                            selectMotorsUseCase(it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                currentCard.value = NONE
            }

            is MachineDetailsUiEvent.OnSaveMotorSpecificationsCard -> {
                motorSpecsState.value = motorSpecsState.value?.copy(
                    // Note: createdAt should never be updated
                    effClass = event.motorSpecs.effClass,
                    phasesNumber = event.motorSpecs.phasesNumber,
                    nominalPower = event.motorSpecs.nominalPower,
                    frequency = event.motorSpecs.frequency,
                    rpm = event.motorSpecs.rpm,
                    rpmRange = event.motorSpecs.rpmRange,
                    nominalVoltage = event.motorSpecs.nominalVoltage,
                    nominalCurrent = event.motorSpecs.nominalCurrent,
                    serviceFactor = event.motorSpecs.serviceFactor,
                    powerFactor = event.motorSpecs.powerFactor,
                    duty = event.motorSpecs.duty,
                    roomTemp = event.motorSpecs.roomTemp,
                    energyEff = event.motorSpecs.energyEff,
                    maxAltitude = event.motorSpecs.maxAltitude,
                    ingressProtection = event.motorSpecs.ingressProtection,
                    mountingType = event.motorSpecs.mountingType,
                    frameType = event.motorSpecs.frameType,
                    coolingMethod = event.motorSpecs.coolingMethod,
                    driveEnd = event.motorSpecs.driveEnd,
                    nonDriveEnd = event.motorSpecs.nonDriveEnd,
                    insulationClass = event.motorSpecs.insulationClass,
                    insulationClassTemp = event.motorSpecs.insulationClassTemp,
                    weight = event.motorSpecs.weight,
                    modifiedAt = Date(System.currentTimeMillis())
                )
                motorSpecsState.value?.let {
                    viewModelScope.launch {
                        try {
                            updateMotorSpecsUseCase(it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                currentCard.value = NONE
            }

            is MachineDetailsUiEvent.OnSaveMachineName -> {}
            is MachineDetailsUiEvent.OnSaveMachineImageUri -> {}
        }
    }

    private suspend fun getMachineDetails(machineId: Int): MachineDetails =
        selectMachineAndDetailsUseCase(machineId)

    private fun getCounters(machineId: Int) {
        fun collectCount(
            flow: Flow<Int>,
            update: (Int, MachineDetailsCounters) -> MachineDetailsCounters,
        ) {
            flow.onEach { count -> counters.value = update(count, counters.value) }
                .launchIn(viewModelScope)
        }

        collectCount(countDocumentsUseCase(machineId)) { count, counters ->
            counters.copy(documentsCount = count)
        }

        collectCount(countActivitiesUseCase(machineId)) { count, counters ->
            counters.copy(activitiesCount = count)
        }
    }
}

sealed class MachineDetailsUiEvent {
    data class OnEditCard(val card: MachineDetailsCard) : MachineDetailsUiEvent()
    data class OnSaveMachineIdentificationCard(val machine: Machine) : MachineDetailsUiEvent()
    data class OnSaveMachineSpecificationsCard(val machineSpecs: MachineSpecs) :
        MachineDetailsUiEvent()

    data class OnSaveMotorIdentificationCard(val motorIdentity: MotorIdentity) : MachineDetailsUiEvent()
    data class OnSaveMotorSpecificationsCard(val motorSpecs: MotorSpecs) : MachineDetailsUiEvent()

    data class OnSaveMachineName(val name: String) : MachineDetailsUiEvent()
    data class OnSaveMachineImageUri(val imageUri: Uri?) : MachineDetailsUiEvent()
}

sealed interface MachineDetailsCardsUiState {

    data object Loading : MachineDetailsCardsUiState

    /**
     * Data class containing the successfully loaded data of the machine details screen cards from
     * local database.
     *
     * @property machine state of the machine.
     * @property machineSpecs state of the machine specs.
     * @property motorIdentity state of the motor.
     * @property motorSpecs state of the motor specs.
     */
    data class Success(
        val machine: Machine?,
        val machineSpecs: MachineSpecs?,
        val motorIdentity: MotorIdentity?,
        val motorSpecs: MotorSpecs?,
    ) : MachineDetailsCardsUiState
}

data class MachineDetailsCounters(
    val activitiesCount: Int = 0,
    val documentsCount: Int = 0,
    val suppliersCount: Int = 0,
)

/**
 * Data class representing the UI state of the machine details screen.
 *
 * @property card Indicates which card is currently being edited.
 * @property isEditing A boolean flag indicating whether the screen is in editing mode.
 */
data class MachineDetailsUiState(
    val card: MachineDetailsCard = NONE,
    val isEditing: Boolean = false,
    val counters: MachineDetailsCounters = MachineDetailsCounters(),
)

/**
 * Enum class representing the cards available in the machine details screen.
 *
 * @property MACHINE The machine identification card.
 * @property MACHINE_SPECS The machine specifications card.
 * @property MOTOR The motor identification card.
 * @property MOTOR_SPECS The motor specifications card.
 * @property NONE No card is being edited.
 */
enum class MachineDetailsCard {
    MACHINE, MACHINE_SPECS, MOTOR, MOTOR_SPECS, NONE
}