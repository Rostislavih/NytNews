package dev.rostisla.nyt.data

import dev.rostisla.nyt.data.api.NytStoriesApi
import dev.rostisla.nyt.data.api.NytStoriesApiImpl
import dev.rostisla.nyt.data.client.nytClient
import dev.rostisla.nyt.data.database.NytDatabase
import dev.rostisla.nyt.data.repository.StoriesRepositoryImpl
import dev.rostisla.nyt.domain.repository.StoriesRepository
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module

val storiesDataModule = module {
    single { nytClient() }
    factory { NytStoriesApiImpl(get()) }.bind<NytStoriesApi>()
    factory { StoriesRepositoryImpl(get()) }.bind<StoriesRepository>()
}

internal expect fun Scope.getDatabase(): NytDatabase
