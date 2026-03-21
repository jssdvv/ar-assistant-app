import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jssdvv.ara.core.presentation.common.ArrowDownIcon
import com.jssdvv.ara.core.presentation.common.ArrowPreviousItemIcon
import com.jssdvv.ara.core.presentation.common.CheckIcon
import com.jssdvv.ara.core.presentation.foundation.component.FocusableCard
import com.jssdvv.ara.machines.presentation.destination.steps.component.DraggableBottomSheet
import com.jssdvv.ara.machines.presentation.destination.steps.component.DraggableBottomSheetState
import com.jssdvv.ara.schedule.presentation.destination.events.EventsViewModel
import com.jssdvv.ara.schedule.presentation.destination.events.component.Calendar
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@Composable
fun EventsDestination(
    modifier: Modifier = Modifier,
    viewModel: EventsViewModel = hiltViewModel()
) {

    //InteractiveSheetScreen()
//    EventsScreen(
//        modifier = modifier,
//        paths = viewModel.paths.collectAsStateWithLifecycle().value,
//        changePath = { viewModel.changePath() }
//    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EventsScreen(
    modifier: Modifier = Modifier,
    paths: List<Path>,
    changePath: () -> Unit
) {
    val zone = ZoneId.systemDefault()
    val now = ZonedDateTime.now(zone)

    var selectedDate by remember { mutableStateOf(now.toLocalDate()) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now(zone)) }

    val systemLanguage = Locale.getDefault().language
    val locale = if (systemLanguage == "es") Locale.of("es") else Locale.of("en")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = selectedMonth.month.getDisplayName(TextStyle.FULL, locale)
                            .uppercase() +
                                " " +
                                selectedMonth.year,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { selectedMonth = selectedMonth.minusMonths(1) },
                        content = { ArrowPreviousItemIcon() }
                    )
                },
                actions = {
                    IconButton(
                        onClick = { selectedMonth = selectedMonth.plusMonths(1) },
                        content = { ArrowPreviousItemIcon(modifier = Modifier.rotate(180f)) }
                    )
                }
            )
        }
    ) { paddingValues ->



//        Calendar(
//            modifier = modifier.padding(paddingValues),
//            selectedMonth = selectedMonth,
//            selectedDate = selectedDate,
//            onDateChanged = { selectedDate = it },
//            onMonthChanged = { selectedMonth = it },
//            zone = zone
//        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveSheetScreen() {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 120.dp, // Altura cuando está "cerrado"
        sheetContent = {
            Column(
                Modifier.fillMaxWidth().height(400.dp).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Contenido del Sheet", style = MaterialTheme.typography.titleLarge)
                // Tu contenido aquí
            }
        }
    ) { innerPadding ->
        val details = listOf("Motor: V8", "Presión: 120psi", "Voltaje: 220V")
        var selectedCardIndex by remember { mutableIntStateOf(-1) }

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(5) { index ->
                ExpandableTechnicalCard(
                    title = "Máquina de Prueba ${index + 1}",
                    subtitle = "ID: CSO-00${index + 1}",
                    technicalDetails = details,
                    isSelected = selectedCardIndex == index,
                    onSelect = { selectedCardIndex = index }
                )
            }
        }
    }
}

@Composable
fun ExpandableTechnicalCard(
    title: String,
    subtitle: String,
    technicalDetails: List<String>,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    // Estado local para la expansión
    var isExpanded by remember { mutableStateOf(false) }

    FocusableCard(
        isFocused = isSelected,
        onClick = {
            onSelect() // Primero seleccionamos (activamos oleaje)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        // Este es el contenedor que cambiará de tamaño
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                // Aquí ocurre la magia del cambio de tamaño
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Botón para expandir/contraer
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    ArrowDownIcon()
                }
            }

            // Contenido que aparece y desaparece
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

                technicalDetails.forEach { detail ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CheckIcon()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Text(
                    text = "Nota: Configuración exclusiva para entorno local (CSO).",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}