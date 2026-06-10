package com.example.navegacion_menus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private var usuarioEstaRegistrado = false
    private var nombreUsuarioLogueado = "Invitado"

    override fun onCreate(savedInstanceState: Bundle?) {
        val isDark = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean("tema_oscuro", false)

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        topAppBar = findViewById(R.id.topAppBar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        topAppBar.inflateMenu(R.menu.top_app_bar)
        topAppBar.setNavigationIcon(R.drawable.ic_menu_24dp)

        topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        val typedValue = android.util.TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)
        val colorContraste = if (typedValue.resourceId != 0) {
            androidx.core.content.ContextCompat.getColor(this, typedValue.resourceId)
        } else {
            typedValue.data
        }

        topAppBar.navigationIcon?.let { drawable ->
            val wrapped = androidx.core.graphics.drawable.DrawableCompat.wrap(drawable)
            androidx.core.graphics.drawable.DrawableCompat.setTint(wrapped, colorContraste)
            topAppBar.navigationIcon = wrapped
        }

        topAppBar.overflowIcon?.let { drawable ->
            val wrapped = androidx.core.graphics.drawable.DrawableCompat.wrap(drawable)
            androidx.core.graphics.drawable.DrawableCompat.setTint(wrapped, colorContraste)
            topAppBar.overflowIcon = wrapped
        }

        cargarEstadoSesion()
        actualizarMenuToolbar()

        topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    if (usuarioEstaRegistrado) {
                        startActivity(Intent(this, PerfilActivity::class.java))
                    } else {
                        startActivity(Intent(this, RegistroActivity::class.java))
                    }
                    true
                }

                R.id.menu_ajustes -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                R.id.more01 -> {
                    val intent = Intent(this, InfoActivity::class.java)
                    intent.putExtra("TIPO_PANTALLA", "REPORTAR")
                    startActivity(intent)
                    true
                }

                R.id.more02 -> {
                    val intent = Intent(this, InfoActivity::class.java)
                    intent.putExtra("TIPO_PANTALLA", "MAS_INFO")
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    private fun cargarEstadoSesion() {
        val prefs = getSharedPreferences("MhaisiPrefs", Context.MODE_PRIVATE)
        usuarioEstaRegistrado = prefs.getBoolean("is_logged_in", false)
        nombreUsuarioLogueado = prefs.getString("user_name", "Invitado") ?: "Invitado"
    }

    private fun cerrarSesionUsuario() {
        Log.d("Tarea3_Mhaisi", "Cerrando sesión: Limpiando SharedPreferences")
        val prefs = getSharedPreferences("MhaisiPrefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()

        usuarioEstaRegistrado = false
        nombreUsuarioLogueado = "Invitado"

        actualizarMenuToolbar()

        for (i in 0 until navigationView.menu.size()) {
            navigationView.menu.getItem(i).isChecked = false
        }

        Toast.makeText(this, "Sesión cerrada. Volviendo a modo Invitado.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        cargarEstadoSesion()
        actualizarMenuToolbar()
    }

    // CORRECCIÓN 2: Método personalizado para actualizar dinámicamente el texto del perfil manteniendo el color del XML
    private fun actualizarMenuToolbar() {
        val perfilItem = topAppBar.menu.findItem(R.id.edit)

        val textoBase = if (usuarioEstaRegistrado) {
            "$nombreUsuarioLogueado ▾"
        } else {
            "Iniciar Sesión"
        }

        perfilItem?.let { item ->
            val typedValue = android.util.TypedValue()
            theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)
            val colorTexto = if (typedValue.resourceId != 0) {
                androidx.core.content.ContextCompat.getColor(this, typedValue.resourceId)
            } else {
                typedValue.data
            }

            val spannableString = android.text.SpannableString(textoBase)
            spannableString.setSpan(
                android.text.style.ForegroundColorSpan(colorTexto),
                0,
                spannableString.length,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            item.title = spannableString
        }
    }
}