package com.example.navegacion_menus

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class PerfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        Log.d("Tarea3_Mhaisi", "Pantalla: Abriendo perfil dinámico")

        val prefs = getSharedPreferences("MhaisiPrefs", android.content.Context.MODE_PRIVATE)

        val nombreUsuario = prefs.getString("full_name", "Invitado Mhaisi") ?: "Invitado Mhaisi"
        val correoUsuario =
            prefs.getString("email", "invitado@mhaisicoffee.com") ?: "invitado@mhaisicoffee.com"
        val cumpleaniosUsuario = prefs.getString("birthday", "No especificado") ?: "No especificado"

        val comprasRealizadas = 7

        val tvProfileName = findViewById<TextView>(R.id.tv_profile_name)
        val tvProfileInitial = findViewById<TextView>(R.id.tv_profile_initial)
        val tvDataNombre = findViewById<TextView>(R.id.tv_data_nombre)
        val tvDataCorreo = findViewById<TextView>(R.id.tv_data_correo)
        val tvDataCumpleanios = findViewById<TextView>(R.id.tv_data_cumpleanios)

        tvProfileName?.text = nombreUsuario
        tvDataNombre?.text = nombreUsuario
        tvDataCorreo?.text = correoUsuario
        tvDataCumpleanios?.text = cumpleaniosUsuario

        if (nombreUsuario.isNotEmpty()) {
            tvProfileInitial?.text = nombreUsuario.first().uppercase()
        }

        actualizarNivelYPuntos(comprasRealizadas)

        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)
        btnLogout?.setOnClickListener {
            val prefsEditor =
                getSharedPreferences("MhaisiPrefs", android.content.Context.MODE_PRIVATE).edit()
            prefsEditor.clear().commit()

            Toast.makeText(this, "Sesión cerrada con éxito", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    private fun actualizarNivelYPuntos(compras: Int) {
        val tvProfileLevel = findViewById<TextView>(R.id.tv_profile_level)
        val tvTotalComprasStatus = findViewById<TextView>(R.id.tv_total_compras_status)
        val tvProfilePoints = findViewById<TextView>(R.id.tv_profile_points)
        val pbPointsProgress = findViewById<ProgressBar>(R.id.pb_points_progress)
        val tvPointsSubtitle = findViewById<TextView>(R.id.tv_points_subtitle)

        // Cada compra otorga exactamente 10 puntos estrella
        val puntosAcumulados = compras * 10
        tvProfilePoints?.text = "$puntosAcumulados pts"
        tvTotalComprasStatus?.text = "Compras realizadas: $compras"

        // Lógica de Niveles y metas hacia bebidas gratis (Cada 100 puntos = 1 Bebida Gratis)
        val progresoCiclo = puntosAcumulados % 100
        val puntosFaltantes = 100 - progresoCiclo

        pbPointsProgress?.progress = progresoCiclo
        tvPointsSubtitle?.text = "Te faltan ${puntosFaltantes / 10} compras ($puntosFaltantes pts) para tu café gratis."

        // Asignación dinámica de la categoría según rango de compras
        when {
            compras <= 2 -> {
                tvProfileLevel?.text = "Cliente Nuevo "
            }
            compras in 3..9 -> {
                tvProfileLevel?.text = "Cliente Coffee Lover "
            }
            else -> {
                tvProfileLevel?.text = "Cliente Destacado"
            }
        }

        Log.d("Tarea3_Mhaisi", "Lógica: Cliente procesado con nivel: ${tvProfileLevel?.text}")
    }
}