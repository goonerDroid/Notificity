package com.darshan.notificity

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NotificityApp: Application() {

    override fun onCreate() {
        super.onCreate()
    }

}

val Context.app get() = applicationContext as NotificityApp