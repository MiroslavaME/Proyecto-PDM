package com.example.navegacion_menus

import android.content.Context
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

        try {
            setContentView(R.layout.activity_perfil)
        } catch (e: Exception) {
            Log.e("MhaisiPerfil", "Error fatal al inflar el diseño XML", e)
            return
        }

        val prefs = getSharedPreferences("MhaisiPrefs", Context.MODE_PRIVATE)
        val nombreUsuario = prefs.getString("user_name", "Invitado Mhaisi") ?: "Invitado Mhaisi"
        val correoUsuario = prefs.getString("user_email", "invitado@mhaisicoffee.com") ?: "invitado@mhaisicoffee.com"
        val currentUserId = prefs.getInt("user_id", 1)

        val dbHelper = DataBaseHelper(this)

        var cumpleaniosUsuario = prefs.getString("user_birthday", null)
            ?: prefs.getString("birthday", null)
            ?: prefs.getString("mes_nacimiento", null)

        if (cumpleaniosUsuario == null) {
            try {
                val db = dbHelper.readableDatabase
                val cursor = db.query(
                    DataBaseHelper.TABLA_USUARIOS,
                    arrayOf(DataBaseHelper.KEY_USER_BIRTH_MONTH),
                    "${DataBaseHelper.KEY_USER_ID} = ?",
                    arrayOf(currentUserId.toString()),
                    null, null, null
                )

                if (cursor != null && cursor.moveToFirst()) {
                    val mesNum = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.KEY_USER_BIRTH_MONTH))
                    cumpleaniosUsuario = obtenerNombreMes(mesNum)
                }
                cursor?.close()
            } catch (e: Exception) {
                Log.e("MhaisiPerfil", "Error al consultar mes en la BD", e)
            }
        }

        // Si después de ambos intentos sigue siendo nulo, asignamos el valor por defecto
        if (cumpleaniosUsuario == null) {
            cumpleaniosUsuario = "No especificado"
        }

        val comprasRealizadas = try {
            dbHelper.obtenerCantidadPedidosPorUsuario(currentUserId)
        } catch (e: Exception) {
            0
        }

        // Mapeo seguro en la interfaz
        findViewById<TextView>(R.id.tv_profile_name)?.text = nombreUsuario
        findViewById<TextView>(R.id.tv_data_nombre)?.text = nombreUsuario
        findViewById<TextView>(R.id.tv_data_correo)?.text = correoUsuario
        findViewById<TextView>(R.id.tv_data_cumpleanios)?.text = cumpleaniosUsuario

        val tvProfileInitial = findViewById<TextView>(R.id.tv_profile_initial)
        if (tvProfileInitial != null) {
            if (nombreUsuario.trim().isNotEmpty()) {
                tvProfileInitial.text = nombreUsuario.trim().first().toString().uppercase()
            } else {
                tvProfileInitial.text = "M"
            }
        }

        findViewById<TextView>(R.id.tv_profile_level)?.text = when {
            comprasRealizadas <= 2 -> "Cliente Nuevo"
            comprasRealizadas in 3..9 -> "Cliente Coffee Lover"
            else -> "Cliente Destacado"
        }

        findViewById<TextView>(R.id.tv_total_compras_status)?.text = "Compras realizadas: $comprasRealizadas"

        val puntosAcumulados = comprasRealizadas * 10
        findViewById<TextView>(R.id.tv_profile_points)?.text = "$puntosAcumulados pts"

        val progresoCiclo = puntosAcumulados % 100
        findViewById<ProgressBar>(R.id.pb_points_progress)?.progress = progresoCiclo

        val tvPointsSubtitle = findViewById<TextView>(R.id.tv_points_subtitle)
        if (progresoCiclo == 0 && comprasRealizadas > 0) {
            tvPointsSubtitle?.text = "¡Felicidades! Tienes una bebida gratis lista en caja."
            findViewById<ProgressBar>(R.id.pb_points_progress)?.progress = 100
        } else {
            tvPointsSubtitle?.text = "Te faltan ${(100 - progresoCiclo) / 10} compras para tu café gratis."
        }

        findViewById<MaterialButton>(R.id.btnLogout)?.setOnClickListener {
            getSharedPreferences("MhaisiPrefs", Context.MODE_PRIVATE).edit().clear().commit()
            Toast.makeText(this, "Sesión cerrada con éxito", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun obtenerNombreMes(mes: Int): String {
        return when (mes) {
            1 -> "Enero"
            2 -> "Febrero"
            3 -> "Marzo"
            4 -> "Abril"
            5 -> "Mayo"
            6 -> "Junio"
            7 -> "Julio"
            8 -> "Agosto"
            9 -> "Septiembre"
            10 -> "Octubre"
            11 -> "Noviembre"
            12 -> "Diciembre"
            else -> "No especificado"
        }
    }
}