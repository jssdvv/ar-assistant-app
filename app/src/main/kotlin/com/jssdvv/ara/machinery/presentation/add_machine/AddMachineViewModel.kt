package com.jssdvv.ara.machinery.presentation.add_machine

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jssdvv.ara.core.data.local.entity.InvalidMachineException
import com.jssdvv.ara.core.domain.model.Machine
import com.jssdvv.ara.machinery.domain.usecase.InsertMachine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMachineViewModel @Inject constructor(
    private val insertMachineUseCase: InsertMachine,
) : ViewModel() {

    private val _name = MutableStateFlow("")
    private val _category = MutableStateFlow("")
    private val _description = MutableStateFlow("")
    private val _imageUri = MutableStateFlow<Uri?>(null)
    private val _isSavingButtonEnabled = MutableStateFlow(true)

    val uiState: StateFlow<AddMachineUiState> = combine(
        _name,
        _category,
        _description,
        _imageUri,
        _isSavingButtonEnabled
    ) { name, category, description, imageUri, isSavingButtonEnabled ->
        AddMachineUiState(
            machine = Machine(
                name = name,
                category = category,
                description = description,
                imageUri = imageUri,
                timestamp = 0L
            ),
            isButtonEnabled = isSavingButtonEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = AddMachineUiState()
    )

    fun onUiEvent(event: AddMachineUiEvent) {
        when (event) {
            is AddMachineUiEvent.OnNameChange -> onNameChange(event.name)
            is AddMachineUiEvent.OnCategoryChange -> onCategoryChange(event.category)
            is AddMachineUiEvent.OnDescriptionChange -> onDescriptionChange(event.description)
            is AddMachineUiEvent.OnImageUriChange -> onImageUriChange(event.imageUri)
            is AddMachineUiEvent.OnClickSavingChanges -> onClickSavingChanges()
        }
    }

    private fun onNameChange(name: String) {
        _name.value = name
    }

    private fun onCategoryChange(category: String) {
        _category.value = category
    }

    private fun onDescriptionChange(description: String) {
        _description.value = description
    }

    private fun onImageUriChange(imageUri: Uri) {
        _imageUri.value = imageUri
    }

    fun onClickSavingChanges() {
        viewModelScope.launch {
            try {
                insertMachineUseCase.invoke(
                    model = Machine(
                        name = uiState.value.machine.name,
                        category = uiState.value.machine.category,
                        description = uiState.value.machine.description,
                        imageUri = uiState.value.machine.imageUri,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: InvalidMachineException) {
                e.printStackTrace()
            }
        }
    }
}

sealed class AddMachineUiEvent {
    data class OnNameChange(val name: String) : AddMachineUiEvent()
    data class OnCategoryChange(val category: String) : AddMachineUiEvent()
    data class OnDescriptionChange(val description: String) : AddMachineUiEvent()
    data class OnImageUriChange(val imageUri: Uri) : AddMachineUiEvent()
    data object OnClickSavingChanges : AddMachineUiEvent()
}

data class AddMachineUiState(
    val machine: Machine = Machine(
        name = "",
        category = "",
        description = "",
        imageUri = null,
        timestamp = 0L
    ),
    val isButtonEnabled: Boolean = true,
)