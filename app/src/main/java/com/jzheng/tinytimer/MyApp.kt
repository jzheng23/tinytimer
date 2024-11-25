package com.jzheng.tinytimer

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    // You can add application-wide initialization here if needed
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}