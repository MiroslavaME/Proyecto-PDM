package com.example.navegacion_menus

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import java.util.Calendar

class CuponesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cupones)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val cardCumple = findViewById<MaterialCardView>(R.id.card_cupon_cumple)
        val cardBienvenida = findViewById<MaterialCardView>(R.id.card_cupon_bienvenida)

        val calendar = Calendar.getInstance()
        val mesActual = calendar.get(Calendar.MONTH) + 1

        val prefs = getSharedPreferences("MhaisiPrefs", Context.MODE_PRIVATE)

        val mesNacimientoUsuario = prefs.getInt("user_birth_month", 0)
        val cumpleYaUsado = prefs.getBoolean("cupon_mhaisicump_usado", false)

        if (cardCumple != null) {
            if (mesNacimientoUsuario == mesActual && !cumpleYaUsado) {
                cardCumple.visibility = View.VISIBLE
            } else {
                cardCumple.visibility = View.GONE
            }
        }

        val bienvenidaYaUsado = prefs.getBoolean("cupon_mhaisibienvenida_usado", false)

        if (cardBienvenida != null) {
            if (!bienvenidaYaUsado) {
                cardBienvenida.visibility = View.VISIBLE
            } else {
                cardBienvenida.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}