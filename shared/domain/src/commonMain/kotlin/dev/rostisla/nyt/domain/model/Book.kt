package dev.rostisla.nyt.domain.model

class Book(
    val rank: Int,
    val title: String,
    val description: String,
    val author: String,
    val publisher: String,
    val bookImage: String,
    val amazonProductUrl: String,
    val isbn13: String,
)
