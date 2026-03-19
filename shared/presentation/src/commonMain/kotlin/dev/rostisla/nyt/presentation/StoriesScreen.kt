package dev.rostisla.nyt.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import coil3.compose.AsyncImage
import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.presentation.model.UiStory
import dev.rostisla.nyt.presentation.state.StoriesState
import news.shared.presentation.generated.resources.Res
import news.shared.presentation.generated.resources.screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StoriesScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<StoriesViewModel>()
    LifecycleStartEffect(viewModel) {
        viewModel.onStart()
        onStopOrDispose { }
    }
    val state = viewModel.state.collectAsState()
    val currentSection by viewModel.currentSection.collectAsState()

    StoriesScreenContent(
        modifier = modifier,
        screenState = state,
        currentSection = currentSection,
        onSectionSelected = viewModel::updateSection
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoriesScreenContent(
    screenState: State<StoriesState>,
    currentSection: StoriesSection,
    onSectionSelected: (StoriesSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                TopAppBar(title = { Text(text = stringResource(Res.string.screen_title)) })
                SectionSelector(
                    selectedSection = currentSection,
                    onSectionSelected = onSectionSelected,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        val state by screenState
        when (val currentState = state) {
            is StoriesState.Error -> ErrorContent(
                state = currentState,
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            )

            StoriesState.Loading -> LoadingContent(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            )

            is StoriesState.Success -> SuccessContent(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                state = currentState,
            )
        }
    }
}

@Composable
private fun SectionSelector(
    selectedSection: StoriesSection,
    onSectionSelected: (StoriesSection) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(StoriesSection.entries) { section ->
            val isSelected = section == selectedSection
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable { onSectionSelected(section) }
            ) {
                Text(
                    text = section.name,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(state: StoriesState.Error, modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = "Error\n\r${state.message}",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun LoadingContent(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = "Loading")
    }
}

@Composable
private fun SuccessContent(state: StoriesState.Success, modifier: Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(state.stories) {
            StoryCard(story = it, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun StoryCard(story: UiStory, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            story.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = story.abstract,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = story.publishedDate,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Preview
@Composable
private fun StoriesScreenPreview() {
    MaterialTheme {
        StoriesScreenContent(
            currentSection = StoriesSection.HOME,
            onSectionSelected = {},
            screenState = mutableStateOf(
                StoriesState.Success(
                    stories = listOf(
                        UiStory(
                            title = "Title 1",
                            abstract = "Abstract 1",
                            publishedDate = "12.12.2023"
                        ),
                        UiStory(
                            title = "Title 2",
                            abstract = "Abstract 2",
                            publishedDate = "12.12.2023"
                        )
                    )
                )
            )
        )
    }
}
