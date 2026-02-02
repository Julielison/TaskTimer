package com.example.tasktimer

import android.app.Application
import com.example.tasktimer.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class TaskTimerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@TaskTimerApplication)
            modules(appModule)
        }
    }
}
