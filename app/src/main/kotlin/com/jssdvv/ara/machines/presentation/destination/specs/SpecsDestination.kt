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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
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
import com.jssdvv.ara.core.domain.utility.formatMedium
import com.jssdvv.ara.core.presentation.common.component.NavigationUpIconButton
import com.jssdvv.ara.core.presentation.foundation.component.CounterButton
import com.jssdvv.ara.core.presentation.foundation.component.LazyColumnScaffold
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

@Composable
fun SpecsDestination(
    onNavigateUp: () -> Unit,
    onNavigateToActivities: (Int) -> Unit,
    onNavigateToDocuments: (Int) -> Unit,
    viewModel: SpecsViewModel = hiltViewModel(),
) {
    SpecsScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        cardUiState = viewModel.cardsUiState.collectAsStateWithLifecycle().value,
        onEvent = viewModel::uiEvent,
        onNavigateUp = onNavigateUp,
        onNavigateToActivities = onNavigateToActivities,
        onNavigateToDocuments = onNavigateToDocuments,
    )
}

@Composable
internal fun SpecsScreen(
    uiState: SpecsUiState,
    cardUiState: SpecsCardsUiState,
    onEvent: (SpecsEvent) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToActivities: (Int) -> Unit,
    onNavigateToDocuments: (Int) -> Unit,
) {
    when (cardUiState) {
        SpecsCardsUiState.Loading -> LoadingWheelScreen()
        is SpecsCardsUiState.Success -> {
            SpecsContent(
                machine = cardUiState.machine,
                machineSpecs = cardUiState.machineSpecs,
                motorIdentity = cardUiState.motorIdentity,
                motorSpecs = cardUiState.motorSpecs,
                counters = uiState.counters,
                card = uiState.card,
                isEditing = uiState.isEditing,
                onEvent = onEvent,
                onNavigateUp = onNavigateUp,
                onNavigateToActivities = onNavigateToActivities,
                onNavigateToDocuments = onNavigateToDocuments,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpecsContent(
    machine: Machine?,
    machineSpecs: MachineSpecs?,
    motorIdentity: MotorIdentity?,
    motorSpecs: MotorSpecs?,
    counters: MachineDetailsCounters,
    card: MachineDetailsCard,
    isEditing: Boolean,
    onEvent: (SpecsEvent) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToActivities: (Int) -> Unit,
    onNavigateToDocuments: (Int) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val titleVisible by remember { derivedStateOf { lazyListState.firstVisibleItemIndex >= 2 } }
    // TODO: Add editing of machine title and image, also add a delete button at the end of the screen
    LazyColumnScaffold(
        topBarTitle = if (titleVisible) machine?.name.orEmpty() else "",
        navigationIcon = { NavigationUpIconButton(onNavigateUp) },
        lazyListState = lazyListState
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9F),
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
                    contentDescription = stringResource(R.string.card_machine_image_content_desc)
                )
            }
        }

        item {
            // Titles
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
                    text = "Created on ${machine?.createdAt?.formatMedium()}",
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
                onClickEditCard = { onEvent(SpecsEvent.OnEditCard(it)) },
                onClickSaveCard = { machine ->
                    onEvent(SpecsEvent.OnSaveMachineIdentificationCard(machine))
                }
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
        }
        item {
            MachineSpecsCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                machineSpecs = machineSpecs,
                editingCard = card,
                onClickEditCard = { onEvent(SpecsEvent.OnEditCard(it)) },
                onClickSaveCard = { machineSpecs ->
                    onEvent(SpecsEvent.OnSaveMachineSpecificationsCard(machineSpecs))
                }
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
        }
        item {
            MotorIdentityCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                motorIdentity = motorIdentity,
                editingCard = card,
                onClickEditCard = { onEvent(SpecsEvent.OnEditCard(it)) },
                onClickSaveCard = { motor ->
                    onEvent(SpecsEvent.OnSaveMotorIdentificationCard(motor))
                }
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
        }
        item {
            MotorSpecsCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                motorSpecs = motorSpecs,
                editingCard = card,
                onClickEditCard = { onEvent(SpecsEvent.OnEditCard(it)) },
                onClickSaveCard = { motorSpecs ->
                    onEvent(SpecsEvent.OnSaveMotorSpecificationsCard(motorSpecs))
                }
            )
        }
    }
}