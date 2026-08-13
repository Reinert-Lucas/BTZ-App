package com.ivanbm.frontend_btz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val rol = intent.getStringExtra("ROL")

        Handler(Looper.getMainLooper()).postDelayed({

            val destino = when (rol?.lowercase()) {
                "admin" -> AdminActivity::class.java
                "operario" -> OperarioActivity::class.java
                else -> null
            }

            destino?.let {
                val intent = Intent(this, it)
                startActivity(intent)

                overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )

                finish()
            }

        }, 2000)
    }
}