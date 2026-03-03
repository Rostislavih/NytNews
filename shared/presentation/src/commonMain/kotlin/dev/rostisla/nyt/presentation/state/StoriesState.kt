package dev.rostisla.nyt.presentation.state

import dev.rostisla.nyt.presentation.model.UiStory

internal sealed interface StoriesState {
    data class Success(val stories: List<UiStory>) : StoriesState
    data class Error(val message: String) : StoriesState
    object Loading : StoriesState
}
