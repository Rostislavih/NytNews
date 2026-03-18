package dev.rostisla.nyt.data.api

internal enum class StoriesSectionDto(val value: String) {
    HOME("topstories/v2/home"),
    ARTS("topstories/v2/arts"),
    AUTOMOBILES("topstories/v2/automobiles"),
    BOOKS("books/v3/lists")
}
