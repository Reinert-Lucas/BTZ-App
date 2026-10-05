package com.ivanbm.frontend_btz

import android.app.Application
import com.ivanbm.frontend_btz.network.SessionManager

class BtzApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        SessionManager.inicializar(this)
    }
}