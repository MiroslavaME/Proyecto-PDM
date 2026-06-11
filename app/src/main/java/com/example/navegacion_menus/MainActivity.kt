package com.example.navegacion_menus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        val btnNavigate = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnNavigate)
        btnNavigate?.setOnClickListener {
            Log.d("Tarea3_Mhaisi", "Navegación: El usuario presionó 'Realizar mi pedido ahora'")
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

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

        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.close()
            cargarEstadoSesion()

            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Inicio: Mhaisi Coffee", Toast.LENGTH_SHORT).show()
                }

                R.id.nav_orders -> {
                    startActivity(Intent(this, MisPedidosActivity::class.java))
                }

                R.id.nav_favorites -> {
                    if (usuarioEstaRegistrado) {
                        startActivity(Intent(this, FavoritosActivity::class.java))
                    } else {
                        Toast.makeText(this, "Crea tu cuenta para guardar favoritos", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, RegistroActivity::class.java))
                    }
                }

                R.id.nav_coupons -> {
                    if (usuarioEstaRegistrado) {
                        startActivity(Intent(this, CuponesActivity::class.java))
                    } else {
                        Toast.makeText(this, "Los cupones son exclusivos para miembros", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, RegistroActivity::class.java))
                    }
                }

                R.id.nav_news -> {
                    val intent = Intent(this, InfoActivity::class.java)
                    intent.putExtra("TIPO_PANTALLA", "NOVEDADES")
                    startActivity(intent)
                }

                R.id.nav_season -> {
                    val intent = Intent(this, InfoActivity::class.java)
                    intent.putExtra("TIPO_PANTALLA", "TEMPORADA")
                    startActivity(intent)
                }

                R.id.nav_merch -> {
                    val intent = Intent(this, InfoActivity::class.java)
                    intent.putExtra("TIPO_PANTALLA", "MERCANCIA")
                    startActivity(intent)
                }

                R.id.nav_locations -> {
                    val intent = Intent(this, InfoActivity::class.java)
                    intent.putExtra("TIPO_PANTALLA", "SUCURSALES")
                    startActivity(intent)
                }

                R.id.nav_about_us -> {
                    val intent = Intent(this, InfoActivity::class.java)
                    intent.putExtra("TIPO_PANTALLA", "ABOUT")
                    startActivity(intent)
                }

                R.id.nav_faq -> {
                    val intent = Intent(this, InfoActivity::class.java)
                    intent.putExtra("TIPO_PANTALLA", "FAQ")
                    startActivity(intent)
                }

                R.id.nav_logout -> {
                    if (usuarioEstaRegistrado) {
                        cerrarSesionUsuario()
                    } else {
                        Toast.makeText(this, "Navegas en modo Invitado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        }
        val prefs = getSharedPreferences("MhaisiPrefs", android.content.Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)

        if (!isLoggedIn) {
            prefs.edit().apply {
                remove("ultimo_pedido_invitado_id")
                apply()
            }
            Log.d("MhaisiSafety", "Identificador de pedido de invitado removido con éxito en el arranque.")
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

        val prefs = getSharedPreferences("MhaisiPrefs", Context.MODE_PRIVATE)
        val currentUserId = prefs.getInt("user_id", 1)

        val dbHelper = DataBaseHelper(this)
        CarritoGlobal.listaFavoritos.clear()
        try {
            CarritoGlobal.listaFavoritos.addAll(dbHelper.obtenerTodosLosFavoritosPorUsuario(currentUserId))
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al cargar favoritos", e)
        }
    }

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