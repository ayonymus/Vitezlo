package org.szvsszke.vitezlo2018

import android.app.Application
import org.szvsszke.vitezlo2018.di.AppComponent
import org.szvsszke.vitezlo2018.di.AppModule
import org.szvsszke.vitezlo2018.di.DaggerAppComponent
import timber.log.Timber

class App: Application() {

    private lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        component = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {

        private lateinit var INSTANCE: App

        @JvmStatic
        fun getComponent(): AppComponent = INSTANCE.component
    }

}
