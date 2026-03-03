package dev.rostisla.nyt.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.domain.repository.StoriesRepository
import dev.rostisla.nyt.presentation.mapper.toUiStory
import dev.rostisla.nyt.presentation.state.StoriesState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal class StoriesViewModel(private val repository: StoriesRepository) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext = viewModelScope.coroutineContext
    private val _state: MutableStateFlow<StoriesState> = MutableStateFlow(StoriesState.Loading)
    val state: StateFlow<StoriesState> = _state.asStateFlow()
    private var isStarted: Boolean = false

    fun onStart() {
        if (!isStarted) {
            fetchStories()
        }
        isStarted = true
    }

    private fun fetchStories() {
        launch {
            updateState { StoriesState.Loading }
            try {
                val stories = repository.fetchStories(StoriesSection.HOME)
                updateState { StoriesState.Success(stories.map { it.toUiStory() }) }
            } catch (error: Exception) {
                ensureActive()
                updateState { StoriesState.Error(error.message.orEmpty()) }
            }
        }
    }

    private fun updateState(update: (StoriesState) -> StoriesState) = _state.update(update)
}
