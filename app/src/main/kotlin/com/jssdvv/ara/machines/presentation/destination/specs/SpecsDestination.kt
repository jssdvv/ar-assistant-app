package com.jssdvv.ara.machines.presentation.destination.specs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jssdvv.ara.R
import com.jssdvv.ara.core.domain.utility.AspectRatio
import com.jssdvv.ara.core.presentation.foundation.component.CounterButton
import com.jssdvv.ara.core.presentation.foundation.component.LoadingWheelScreen
import com.jssdvv.ara.core.presentation.navigation.ActivitiesIcon
import com.jssdvv.ara.core.presentation.navigation.DocumentsIcon
import com.jssdvv.ara.core.presentation.theme.spacing
import com.jssdvv.ara.machines.domain.model.machine.Machine
import com.jssdvv.ara.machines.domain.model.machine.MachineSpecs
import com.jssdvv.ara.machines.domain.model.machine.MotorIdentity
import com.jssdvv.ara.machines.domain.model.machine.MotorSpecs
import com.jssdvv.ara.machines.presentation.destination.specs.component.MachineIdentityCard
import com.jssdvv.ara.machines.presentation.destination.specs.component.MachineSpecsCard
import com.jssdvv.ara.machines.presentation.destination.specs.component.MotorIdentityCard
import com.jssdvv.ara.machines.presentation.destination.specs.component.MotorSpecsCard
import com.jssdvv.ara.machines.presentation.destination.specs.component.SpecsTopBar
import java.text.SimpleDateFormat

@Composable
fun MachineDetailsDestination(
    onNavigateBack: () -> Unit,
    onNavigateToActivities: (Int) -> Unit,
    onNavigateToDocuments: (Int) -> Unit,
    viewModel: MachineDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cardUiState by viewModel.cardsUiState.collectAsStateWithLifecycle()
    MachineDetailsScreen(
        uiState = uiState,
        cardUiState = cardUiState,
        uiEvent = viewModel::uiEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToActivities = onNavigateToActivities,
        onNavigateToDocuments = onNavigateToDocuments,
    )
}

@Composable
internal fun MachineDetailsScreen(
    modifier: Modifier = Modifier,
    uiState: MachineDetailsUiState,
    cardUiState: MachineDetailsCardsUiState,
    uiEvent: (MachineDetailsUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToActivities: (Int) -> Unit,
    onNavigateToDocuments: (Int) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val onShowTitle by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex >= 2 }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SpecsTopBar(
                onNavigateBack = onNavigateBack,
                isTitleShown = onShowTitle,
                title = if (cardUiState is MachineDetailsCardsUiState.Success) {
                    cardUiState.machine?.name ?: ""
                } else ""
            )
        }
    ) { paddingValues ->
        when (cardUiState) {
            MachineDetailsCardsUiState.Loading -> LoadingWheelScreen()

            is MachineDetailsCardsUiState.Success -> {
                MachineDetailsSuccessScreen(
                    modifier = Modifier.padding(paddingValues),
                    machine = cardUiState.machine,
                    machineSpecs = cardUiState.machineSpecs,
                    motorIdentity = cardUiState.motorIdentity,
                    motorSpecs = cardUiState.motorSpecs,
                    counters = uiState.counters,
                    card = uiState.card,
                    isEditing = uiState.isEditing,
                    uiEvent = uiEvent,
                    onNavigateToActivities = onNavigateToActivities,
                    onNavigateToDocuments = onNavigateToDocuments,
                    lazyListState = lazyListState
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MachineDetailsSuccessScreen(
    modifier: Modifier = Modifier,
    machine: Machine?,
    machineSpecs: MachineSpecs?,
    motorIdentity: MotorIdentity?,
    motorSpecs: MotorSpecs?,
    counters: MachineDetailsCounters,
    card: MachineDetailsCard,
    isEditing: Boolean,
    uiEvent: (MachineDetailsUiEvent) -> Unit,
    onNavigateToActivities: (Int) -> Unit,
    onNavigateToDocuments: (Int) -> Unit,
    lazyListState: LazyListState,
    aspectRatio: AspectRatio = AspectRatio(16F, 9F)
) {
    // TODO: Add editing of machine title and image, also add a delete button at the end of the screen
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = lazyListState,
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio.aspectRatio),
                contentAlignment = Alignment.BottomCenter
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val scrollOffset =
                                if (lazyListState.firstVisibleItemIndex == 0)
                                    lazyListState.firstVisibleItemScrollOffset.toFloat()
                                else size.height

                            val height = size.height - scrollOffset
                            val scale = (height / size.height).coerceIn(0F, 1F)
                            val alpha = (scale * 2 - 1F).coerceIn(0F, 1F)

                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha

                            translationY = (size.height - height) / 2F
                        },
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(machine?.imageUri)
                        .build(),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = stringResource(R.string.machine_image_content_desc)
                )
            }
        }

        item {
            // Titles
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.small,
                    Alignment.CenterVertically
                )
            ) {
                Text(
                    text = machine?.name.orEmpty(),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Created on ${machine?.createdAt?.let { dateFormat.format(it) }}",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Navigation Buttons
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CounterButton(
                    onClick = { machine?.id?.let { onNavigateToActivities(it) } },
                    title = stringResource(R.string.counter_button_activities_label),
                    count = counters.activitiesCount,
                    icon = { ActivitiesIcon() }
                )
                CounterButton(
                    onClick = { machine?.id?.let { onNavigateToDocuments(it) } },
                    title = stringResource(R.string.counter_button_documents_label),
                    count = counters.documentsCount,
                    icon = { DocumentsIcon() }
                )
            }
        }

        // Info Cards
        item {
            MachineIdentityCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                machine = machine,
                editingCard = card,
                onClickEditCard = { uiEvent(MachineDetailsUiEvent.OnEditCard(it)) },
                onClickSaveCard = { machine ->
                    uiEvent(MachineDetailsUiEvent.OnSaveMachineIdentificationCard(machine))
                }
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
        }
        item {
            MachineSpecsCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                machineSpecs = machineSpecs,
                editingCard = card,
                onClickEditCard = { uiEvent(MachineDetailsUiEvent.OnEditCard(it)) },
                onClickSaveCard = { machineSpecs ->
                    uiEvent(MachineDetailsUiEvent.OnSaveMachineSpecificationsCard(machineSpecs))
                }
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
        }
        item {
            MotorIdentityCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                motorIdentity = motorIdentity,
                editingCard = card,
                onClickEditCard = { uiEvent(MachineDetailsUiEvent.OnEditCard(it)) },
                onClickSaveCard = { motor ->
                    uiEvent(MachineDetailsUiEvent.OnSaveMotorIdentificationCard(motor))
                }
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
        }
        item {
            MotorSpecsCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                motorSpecs = motorSpecs,
                editingCard = card,
                onClickEditCard = { uiEvent(MachineDetailsUiEvent.OnEditCard(it)) },
                onClickSaveCard = { motorSpecs ->
                    uiEvent(MachineDetailsUiEvent.OnSaveMotorSpecificationsCard(motorSpecs))
                }
            )
        }
    }
}