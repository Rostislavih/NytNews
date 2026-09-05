package dev.rostisla.nyt.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.rostisla.nyt.domain.model.StoriesError
import dev.rostisla.nyt.domain.model.StoriesException
import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.domain.repository.StoriesRepository
import dev.rostisla.nyt.presentation.mapper.toUiStory
import dev.rostisla.nyt.presentation.model.UiStory
import dev.rostisla.nyt.presentation.state.StoriesState
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private var stories: List<UiStory> = emptyList()

    private var isFetching: Boolean = false

    /** Не удалось прочитать локальную базу — подписка на этот раздел мертва до переподписки. */
    private var storageFailure: StoriesState.Error? = null

    /** Не удалось сходить в сеть за последним обновлением. */
    private var networkFailure: StoriesState.Error? = null

    fun onStart() {
        if (!isStarted) {
            updateSection(StoriesSection.HOME)
            isStarted = true
        }
    }

    fun updateSection(section: StoriesSection) {
        _currentSection.value = section
        stories = emptyList()
        storageFailure = null
        networkFailure = null
        isFetching = true
        render()

        observeJob?.cancel()
        observeJob = repository.getStories(section)
            .onEach { loaded ->
                stories = loaded.map { it.toUiStory() }
                render()
            }
            .catch { error ->
                storageFailure = error.toErrorState()
                render()
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
            isFetching = true
            try {
                repository.fetchStories(section)
                networkFailure = null
            } catch (error: Exception) {
                ensureActive()
                networkFailure = error.toErrorState()
            } finally {
                isFetching = false
                _isRefreshing.value = false
                render()
            }
        }
    }

    /**
     * Единственное место, где состояние экрана собирается воедино.
     * Загруженные новости важнее ошибки: неудачное обновление не должно стирать то,
     * что уже видит пользователь. Пустой результат — тоже ошибка: показывать нечего,
     * и причина у этого своя.
     *
     * Отказ базы приоритетнее сетевого: экран читает данные только из неё, поэтому
     * удачный запрос в сеть ничего не чинит, пока база недоступна.
     */
    private fun render() {
        _state.value = when {
            stories.isNotEmpty() -> StoriesState.Success(stories)
            isFetching -> StoriesState.Loading
            else -> storageFailure ?: networkFailure ?: StoriesState.Error(StoriesError.NO_DATA)
        }
    }

    private fun Throwable.toErrorState(): StoriesState.Error = when (this) {
        is StoriesException -> StoriesState.Error(error)
        else -> StoriesState.Error(StoriesError.UNKNOWN)
    }
}
