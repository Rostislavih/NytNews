package dev.rostisla.nyt.domain.model

class Story(
    val title: String,
    val abstract: String,
    val publishedDate: String,
    val imageUrl: String? = null
)
