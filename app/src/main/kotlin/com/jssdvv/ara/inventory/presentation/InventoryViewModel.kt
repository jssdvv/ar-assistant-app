package com.jssdvv.ara.inventory.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(

) : ViewModel() {

}

sealed interface InventoryUiState {
    data object Loading
    data object Success
}