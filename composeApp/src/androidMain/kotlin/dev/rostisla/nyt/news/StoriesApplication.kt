package dev.rostisla.nyt.news

import android.app.Application
import dev.rostisla.nyt.shared.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StoriesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StoriesApplication)
            modules(sharedModule)
        }
    }
}