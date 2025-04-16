package com.jssdvv.ara.core.presentation.component.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.jssdvv.ara.R

@Composable
fun CheckIcon() = AraIcon(
    painter = painterResource(id = R.drawable.ic_check),
    contentDescription = "Check",
)

@Composable
fun CancelIcon() = AraIcon(
    painter = painterResource(id = R.drawable.ic_cancel),
    contentDescription = "Check",
)

@Composable
fun SaveIcon() = AraIcon(
    painter = painterResource(id = R.drawable.ic_save),
    contentDescription = "Check",
)

@Composable
fun EditIcon() = AraIcon(
    painter = painterResource(id = R.drawable.ic_edit),
    contentDescription = "Check",
)