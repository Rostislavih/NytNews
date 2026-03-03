package dev.rostisla.nyt.presentation

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val storiesPresentationModule = module {
    viewModel { StoriesViewModel(get()) }
}