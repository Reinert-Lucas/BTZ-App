package com.ivanbm.frontend_btz

import android.app.Application
import com.ivanbm.frontend_btz.network.SessionManager

class BtzApplication : Application() {

    companion object {
        lateinit var instance: BtzApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        SessionManager.inicializar(this)
    }
}