package com.ivanbm.frontend_btz

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.ivanbm.frontend_btz.network.SessionManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class BtzApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        SessionManager.inicializar(this)

        val firebaseApp = FirebaseApp.initializeApp(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                "avisos_asignados",
                "Avisos asignados",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }

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