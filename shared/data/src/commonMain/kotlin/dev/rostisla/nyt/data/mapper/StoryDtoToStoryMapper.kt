package dev.rostisla.nyt.data.mapper

import dev.rostisla.nyt.data.api.NytStoryDto
import dev.rostisla.nyt.domain.model.Story

internal fun NytStoryDto.toStory(): Story {
    val imageUrl = multimedia?.find { it.format == "Large Thumbnail" }?.url 
        ?: multimedia?.firstOrNull()?.url

    return Story(
        url = this.url,
        title = this.title,
        abstract = this.abstract,
        publishedDate = this.publishedDate,
        imageUrl = imageUrl
    )
}
