package com.jssdvv.ara.machines.presentation.destination.documents.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.FloatingActionButton
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

    FloatingActionButton(
        onClick = { launcher.launch("application/pdf") },
        modifier = modifier
    ) {
        AddIcon(
            contentDescription = stringResource(R.string.fab_upload_document_label)
        )
    }
}