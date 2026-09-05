package dev.rostisla.nyt.presentation.mapper

import dev.rostisla.nyt.domain.model.StoriesError
import news.shared.presentation.generated.resources.Res
import news.shared.presentation.generated.resources.error_api_key_missing
import news.shared.presentation.generated.resources.error_bad_response
import news.shared.presentation.generated.resources.error_no_data
import news.shared.presentation.generated.resources.error_no_internet
import news.shared.presentation.generated.resources.error_not_found
import news.shared.presentation.generated.resources.error_rate_limited
import news.shared.presentation.generated.resources.error_server
import news.shared.presentation.generated.resources.error_storage
import news.shared.presentation.generated.resources.error_timeout
import news.shared.presentation.generated.resources.error_unauthorized
import news.shared.presentation.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

internal fun StoriesError.toMessageRes(): StringResource = when (this) {
    StoriesError.NO_INTERNET -> Res.string.error_no_internet
    StoriesError.TIMEOUT -> Res.string.error_timeout
    StoriesError.API_KEY_MISSING -> Res.string.error_api_key_missing
    StoriesError.UNAUTHORIZED -> Res.string.error_unauthorized
    StoriesError.RATE_LIMITED -> Res.string.error_rate_limited
    StoriesError.NOT_FOUND -> Res.string.error_not_found
    StoriesError.SERVER -> Res.string.error_server
    StoriesError.BAD_RESPONSE -> Res.string.error_bad_response
    StoriesError.STORAGE -> Res.string.error_storage
    StoriesError.NO_DATA -> Res.string.error_no_data
    StoriesError.UNKNOWN -> Res.string.error_unknown
}
