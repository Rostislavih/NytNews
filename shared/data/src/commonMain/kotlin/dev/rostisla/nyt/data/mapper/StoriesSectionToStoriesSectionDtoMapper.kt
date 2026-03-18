package dev.rostisla.nyt.data.mapper

import dev.rostisla.nyt.data.api.StoriesSectionDto
import dev.rostisla.nyt.domain.model.StoriesSection

internal fun StoriesSection.toStoriesSectionDto(): StoriesSectionDto {
    return when (this) {
        StoriesSection.HOME -> StoriesSectionDto.HOME
        StoriesSection.ARTS -> StoriesSectionDto.ARTS
        StoriesSection.AUTOMOBILES -> StoriesSectionDto.AUTOMOBILES
        StoriesSection.BOOKS -> StoriesSectionDto.BOOKS
    }
}
