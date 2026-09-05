package dev.rostisla.nyt.presentation.state

import dev.rostisla.nyt.domain.model.StoriesError
import dev.rostisla.nyt.presentation.model.UiStory

internal sealed interface StoriesState {

    /** Данных ещё нет, запрос в работе. */
    data object Loading : StoriesState

    /** Есть что показать — из базы и/или из сети. */
    data class Success(val stories: List<UiStory>) : StoriesState

    /** Показывать нечего, и известно почему. */
    data class Error(val error: StoriesError) : StoriesState
}
