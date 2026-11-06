package com.ivanbm.frontend_btz

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.ivanbm.frontend_btz.network.SessionManager

class BtzApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        SessionManager.inicializar(this)

        val firebaseApp = FirebaseApp.initializeApp(this)

        if (firebaseApp == null) {
            Log.e(
                "BtzApplication",
                "No se pudo inicializar Firebase. Revisar google-services.json y Gradle."
            )
        } else {
            Log.d(
                "BtzApplication",
                "Firebase inicializado correctamente."
            )
        }
    }
}