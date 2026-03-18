package dev.rostisla.nyt.data.mapper

import dev.rostisla.nyt.data.api.NytBookDto
import dev.rostisla.nyt.domain.model.Book

internal fun NytBookDto.toBook(): Book {
    return Book(
        rank = rank,
        title = title,
        description = description ?: "",
        author = author ?: "",
        publisher = publisher ?: "",
        bookImage = bookImage ?: "",
        amazonProductUrl = amazonProductUrl ?: "",
        isbn13 = primaryIsbn13 ?: ""
    )
}
