package com.jssdvv.ara.machinery.presentation.machinery.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.jssdvv.ara.R
import com.jssdvv.ara.core.data.local.entity.MachineEntity
import com.jssdvv.ara.core.domain.model.Machine

@Composable
fun MachineCard(
    modifier: Modifier = Modifier,
    entity: Machine,
    onNavigateToActivitiesList: (Int) -> Unit,
    onNavigateToEditMachine: (Int) -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        onClick = { onNavigateToEditMachine(entity.machineId) }
    ) {
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(entity.imageUri)
                .build()
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = stringResource(R.string.machine_image_content_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Text(
                text = entity.category,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(
                modifier = Modifier.height(4.dp)
            )
            Text(
                text = entity.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = entity.description,
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            Row {
                FilledTonalButton(
                    onClick = {
                        onNavigateToActivitiesList(entity.machineId)
                    },
                ) {
                    Text(text = stringResource(R.string.activity_button_label))
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = {}
                ) {
                    Text(
                        text = stringResource(R.string.technical_sheet_button_label)
                    )
                }

            }
        }
    }
}