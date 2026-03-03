package dev.rostisla.nyt.data.mapper

import dev.rostisla.nyt.data.api.NytStoryDto
import dev.rostisla.nyt.domain.model.Story

internal fun NytStoryDto.toStory(): Story {
    return Story(
        title = this.title,
        abstract = this.abstract,
        publishedDate = this.publishedDate,
    )
}
