package dev.rostisla.nyt.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.domain.repository.StoriesRepository
import dev.rostisla.nyt.presentation.mapper.toUiStory
import dev.rostisla.nyt.presentation.state.StoriesState
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class StoriesViewModel(private val repository: StoriesRepository) : ViewModel() {
    private val _state: MutableStateFlow<StoriesState> = MutableStateFlow(StoriesState.Loading)
    val state: StateFlow<StoriesState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _currentSection = MutableStateFlow(StoriesSection.HOME)
    val currentSection: StateFlow<StoriesSection> = _currentSection.asStateFlow()

    private var observeJob: Job? = null
    private var isStarted: Boolean = false

    fun onStart() {
        if (!isStarted) {
            updateSection(StoriesSection.HOME)
            isStarted = true
        }
    }

    fun updateSection(section: StoriesSection) {
        _currentSection.value = section
        _state.value = StoriesState.Loading
        
        observeJob?.cancel()
        observeJob = repository.getStories(section)
            .onEach { stories ->
                if (stories.isNotEmpty()) {
                    updateState { StoriesState.Success(stories.map { it.toUiStory() }) }
                }
            }
            .catch { error ->
                updateState { StoriesState.Error(error.message.orEmpty()) }
            }
            .launchIn(viewModelScope)

        refreshStories(section)
    }

    fun onRefresh() {
        refreshStories(currentSection.value)
    }

    private fun refreshStories(section: StoriesSection) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.fetchStories(section)
            } catch (error: Exception) {
                ensureActive()
                if (state.value is StoriesState.Loading) {
                    updateState { StoriesState.Error(error.message.orEmpty()) }
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun updateState(update: (StoriesState) -> StoriesState) = _state.update(update)
}
