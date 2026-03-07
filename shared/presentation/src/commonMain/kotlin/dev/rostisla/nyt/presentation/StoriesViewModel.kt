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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
            observeStories()
            refreshStories()
        }
        isStarted = true
    }

    private fun observeStories() {
        // Подписываемся на поток данных из БД. 
        // Любое изменение в БД (например, после fetchStories) автоматически обновит UI.
        repository.getStories(StoriesSection.HOME)
            .onEach { stories ->
                if (stories.isNotEmpty()) {
                    updateState { StoriesState.Success(stories.map { it.toUiStory() }) }
                }
            }
            .catch { error ->
                updateState { StoriesState.Error(error.message.orEmpty()) }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshStories() {
        launch {
            try {
                // Загружаем данные из сети. Они будут сохранены в БД, 
                // и observeStories() увидит эти изменения.
                repository.fetchStories(StoriesSection.HOME)
            } catch (error: Exception) {
                ensureActive()
                // Если база пуста и произошла ошибка сети, показываем экран ошибки.
                if (state.value is StoriesState.Loading) {
                    updateState { StoriesState.Error(error.message.orEmpty()) }
                }
            }
        }
    }

    private fun updateState(update: (StoriesState) -> StoriesState) = _state.update(update)
}
