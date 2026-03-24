package dev.rostisla.nyt.domain.repository

import dev.rostisla.nyt.domain.model.Book
import dev.rostisla.nyt.domain.model.StoriesSection
import dev.rostisla.nyt.domain.model.Story
import kotlinx.coroutines.flow.Flow

interface StoriesRepository {

    fun getStories(section: StoriesSection): Flow<List<Story>>


    suspend fun fetchStories(section: StoriesSection)


    suspend fun getBookList(listName: String): List<Book>
}
