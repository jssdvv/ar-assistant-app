package com.jssdvv.ara.machinery.presentation.camera.activities_list.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.jssdvv.ara.R
import com.jssdvv.ara.core.data.local.entity.ActivityEntity
import com.jssdvv.ara.core.domain.model.Activity

@Composable
fun ActivityCard(
    modifier: Modifier = Modifier,
    entity: Activity,
    onNavigateToArCamera: () -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        onClick = {}
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
                contentDescription = "Esta es la imagen representativa de la actividad de mantenimiento",
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
                modifier = Modifier.height(8.dp)
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
                    onClick = { onNavigateToArCamera() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ar_outlined),
                        contentDescription = "Icono representativo del botón de inciar sesión de realidad aumentada de la carta de la actividad, que navega a la pantalla de camara de realidad aumentada"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Sesión de Realidad Aumentada")
                }
            }
            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}