package dev.rostisla.nyt.presentation.mapper

import dev.rostisla.nyt.domain.model.StoriesSection
import news.shared.presentation.generated.resources.Res
import news.shared.presentation.generated.resources.section_arts
import news.shared.presentation.generated.resources.section_automobiles
import news.shared.presentation.generated.resources.section_books
import news.shared.presentation.generated.resources.section_home
import org.jetbrains.compose.resources.StringResource

internal fun StoriesSection.toTitleRes(): StringResource = when (this) {
    StoriesSection.HOME -> Res.string.section_home
    StoriesSection.ARTS -> Res.string.section_arts
    StoriesSection.AUTOMOBILES -> Res.string.section_automobiles
    StoriesSection.BOOKS -> Res.string.section_books
}
