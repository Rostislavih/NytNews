package dev.rostisla.nyt.shared

import dev.rostisla.nyt.data.storiesDataModule
import dev.rostisla.nyt.presentation.storiesPresentationModule
import org.koin.dsl.module

val sharedModule = module {
    includes(listOf(storiesPresentationModule, storiesDataModule))
}