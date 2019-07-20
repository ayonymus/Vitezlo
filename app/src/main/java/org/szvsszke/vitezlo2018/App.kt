package org.szvsszke.vitezlo2018

import android.app.Application
import org.szvsszke.vitezlo2018.BuildConfig
import timber.log.Timber

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
