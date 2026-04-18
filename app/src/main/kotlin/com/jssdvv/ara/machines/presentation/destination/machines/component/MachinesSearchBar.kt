package com.jssdvv.ara.machines.presentation.destination.machines.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.type.OrderType
import com.jssdvv.ara.core.presentation.common.component.ArrowBackIcon
import com.jssdvv.ara.core.presentation.common.component.CloseIcon
import com.jssdvv.ara.core.presentation.common.component.SearchIcon
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.type.MachineOrderKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachinesSearchBar(
    orderKey: MachineOrderKey,
    orderType: OrderType,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: List<Machine>,
    onOrderMachines: (MachineOrderKey, OrderType) -> Unit,
    onNavigateToMachineDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) {
        SearchBar(
            modifier = modifier,
            inputField = {
                SearchBarDefaults.InputField(
                    query = if (expanded) textFieldState.text.toString() else String(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text(stringResource(R.string.search_bar_machines_supporting_text)) },
                    leadingIcon = {
                        if (expanded) {
                            IconButton(
                                onClick = { expanded = false }
                            ) {
                                ArrowBackIcon()
                            }
                        } else {
                            IconButton(
                                onClick = { expanded = true }
                            ) {
                                SearchIcon()
                            }
                        }
                    },
                    trailingIcon = {
                        if (textFieldState.text.isNotEmpty()) {
                            IconButton(
                                onClick = { textFieldState.setTextAndPlaceCursorAtEnd("") }
                            ) {
                                CloseIcon()
                            }
                        }
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            MachinesOrderSection(
                orderType = orderType,
                orderKey = orderKey,
                onOrderMachines = onOrderMachines
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { machine ->
//                    MachineCard(
//                        onNavigateToMachineDetails = onNavigateToMachineDetails,
//                        machine = machine
//                    )
                }
            }
        }
    }
}