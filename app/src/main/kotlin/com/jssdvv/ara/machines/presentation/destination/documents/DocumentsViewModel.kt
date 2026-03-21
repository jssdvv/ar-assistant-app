package com.jssdvv.ara.machines.presentation.destination.documents

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val documents = mutableListOf<String?>()
    private var machineId: Int? = null

    init {
        savedStateHandle.get<Int>("machineId")?.let { id ->
            if(id > 0) machineId = id
        }
    }

    private fun getDocumentsByMachineId(machineId: Int) {

    }
}