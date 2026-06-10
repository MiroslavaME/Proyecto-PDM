package com.example.navegacion_menus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tipoPantalla = intent.getStringExtra("TIPO_PANTALLA") ?: "ABOUT"

        when (tipoPantalla) {
            "NOVEDADES" -> setContentView(R.layout.activity_novedades)
            "TEMPORADA" -> setContentView(R.layout.activity_temporada)
            "MERCANCIA" -> setContentView(R.layout.activity_mercancia)
            "SUCURSALES" -> setContentView(R.layout.activity_sucursales)
            "ABOUT" -> setContentView(R.layout.activity_about)
            "FAQ" -> setContentView(R.layout.activity_faq)
            "REPORTAR" -> setContentView(R.layout.activity_reportar)
            "MAS_INFO" -> setContentView(R.layout.activity_more_info)
            else -> setContentView(R.layout.activity_about)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}