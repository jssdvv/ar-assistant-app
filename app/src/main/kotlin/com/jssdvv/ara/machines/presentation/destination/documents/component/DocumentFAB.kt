package com.jssdvv.ara.machines.presentation.destination.documents.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jssdvv.ara.R
import com.jssdvv.ara.core.presentation.common.component.AddIcon

@Composable
fun DocumentFAB(
    onPdfSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onPdfSelected(it) }
    }

    ExtendedFloatingActionButton(
        onClick = { launcher.launch("application/pdf") },
        modifier = modifier,
        icon = { AddIcon() },
        text = { Text(stringResource(R.string.fab_documents_create_action)) }
    )
}