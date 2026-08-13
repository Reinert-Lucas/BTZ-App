package com.ivanbm.frontend_btz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ivanbm.frontend_btz.model.LoginRequest
import com.ivanbm.frontend_btz.model.LoginResponse
import com.ivanbm.frontend_btz.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIngresar: Button
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        etUsuario = findViewById(R.id.etUsuario)
        etPassword = findViewById(R.id.etPassword)
        btnIngresar = findViewById(R.id.btnIngresar)
        tvError = findViewById(R.id.tvError)

        btnIngresar.setOnClickListener {

            val usuario = etUsuario.text.toString().trim()
            val password = etPassword.text.toString()

            // Oculta el error anterior
            tvError.visibility = View.GONE

            if (usuario.isEmpty() || password.isEmpty()) {

                mostrarError()

                Toast.makeText(
                    this,
                    "Completá todos los campos",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            iniciarSesion(usuario, password)
        }
    }

    private fun iniciarSesion(usuario: String, password: String) {

        Toast.makeText(
            this,
            "Iniciando sesión...",
            Toast.LENGTH_SHORT
        ).show()

        limpiarError()

        val request = LoginRequest(
            dni = usuario,
            password = password
        )

        RetrofitClient.api.login(request).enqueue(object : Callback<LoginResponse> {

            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {

                if (response.isSuccessful) {

                    val loginResponse = response.body()

                    if (loginResponse?.status == true) {

                        // Login correcto
                        val usuarioLogueado = loginResponse.user
                        val token = loginResponse.token?.plainTextToken

                        Toast.makeText(
                            this@LoginActivity,
                            "Bienvenido ${usuarioLogueado?.nombre}",
                            Toast.LENGTH_LONG
                        ).show()

                        println("TOKEN: $token")
                        println("ROL: ${usuarioLogueado?.rol}")

                        //Navegación
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.putExtra("ROL", loginResponse.user?.rol)
                        startActivity(intent)
                        finish()

                    } else {

                        mostrarErrorServidor(
                            loginResponse?.message ?: "Credenciales incorrectas"
                        )
                    }

                } else {
                    mostrarErrorServidor(
                        "DNI o contraseña incorrectos"
                    )
                }
            }

            override fun onFailure(
                call: Call<LoginResponse>,
                t: Throwable
            ) {

                mostrarErrorServidor(
                    "No se pudo conectar con el servidor"
                )

                println("ERROR DE CONEXIÓN: ${t.message}")
            }
        })
    }

    private fun mostrarError() {

        tvError.visibility = View.VISIBLE

        etUsuario.setTextColor(Color.RED)
        etPassword.setTextColor(Color.RED)

        etUsuario.setBackgroundResource(R.drawable.box_input_error)
        etPassword.setBackgroundResource(R.drawable.box_input_error)
    }

    private fun limpiarError() {

        tvError.visibility = View.GONE

        etUsuario.setTextColor(Color.BLACK)
        etPassword.setTextColor(Color.BLACK)

        etUsuario.setBackgroundResource(R.drawable.box_input_normal)
        etPassword.setBackgroundResource(R.drawable.box_input_normal)
    }

    private fun mostrarErrorServidor(mensaje: String) {

        tvError.text = mensaje
        tvError.visibility = View.VISIBLE

        etUsuario.setTextColor(Color.RED)
        etPassword.setTextColor(Color.RED)

        etUsuario.setBackgroundResource(R.drawable.box_input_error)
        etPassword.setBackgroundResource(R.drawable.box_input_error)

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }
}