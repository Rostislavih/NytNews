package dev.rostisla.nyt.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.rostisla.nyt.presentation.StoriesScreen

@Composable
fun RootScreen(modifier: Modifier = Modifier) {
    MaterialTheme {
        StoriesScreen(modifier = modifier)
    }
}