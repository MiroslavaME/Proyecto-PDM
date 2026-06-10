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

        setSupportActionBar(topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        cargarEstadoSesion()

        val btnNavigate = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnNavigate)
        btnNavigate?.setOnClickListener {
            if (usuarioEstaRegistrado) {
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Para realizar pedidos, por favor crea tu cuenta ", Toast.LENGTH_LONG).show()
                val intent = Intent(this, RegistroActivity::class.java)
                startActivity(intent)
            }
        }

        topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.close()

            cargarEstadoSesion()

            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Inicio: Mhaisi Coffee", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_orders -> {
                    if (usuarioEstaRegistrado) {
                        Toast.makeText(this, "Consultando tus pedidos anteriores...", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Regístrate para guardar tu historial de compras", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, RegistroActivity::class.java))
                    }
                }
                R.id.nav_favorites -> {
                    if (usuarioEstaRegistrado) {
                        Toast.makeText(this, "Abriendo tus favoritos ", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, FavoritosActivity::class.java))
                    } else {
                        Toast.makeText(this, "Crea tu cuenta para guardar tus cafés favoritos", Toast.LENGTH_SHORT).show()
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
                R.id.nav_coupons -> {
                    if (usuarioEstaRegistrado) {
                        startActivity(Intent(this, CuponesActivity::class.java))
                    } else {
                        Toast.makeText(this, "¡Los cupones de descuento son exclusivos para miembros!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, RegistroActivity::class.java))
                    }
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
                        Toast.makeText(this, "Actualmente navegas en Modo Invitado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
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

        invalidateOptionsMenu()

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
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)

        if (menu != null && menu.javaClass.simpleName == "MenuBuilder") {
            try {
                val method = menu.javaClass.getDeclaredMethod(
                    "setOptionalIconsVisible",
                    Boolean::class.javaPrimitiveType
                )
                method.isAccessible = true
                method.invoke(menu, true)

                val typedValue = android.util.TypedValue()
                theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)

                val colorIcono = if (typedValue.resourceId != 0) {
                    androidx.core.content.ContextCompat.getColor(this, typedValue.resourceId)
                } else {
                    typedValue.data
                }

                for (i in 0 until menu.size()) {
                    val item = menu.getItem(i)
                    // Tintamos los iconos de manera segura para que no queden negros en modo noche
                    item.icon?.let { drawable ->
                        val wrapped = androidx.core.graphics.drawable.DrawableCompat.wrap(drawable)
                        androidx.core.graphics.drawable.DrawableCompat.setTint(wrapped, colorIcono)
                        item.icon = wrapped
                    }
                }
            } catch (e: Exception) {
                Log.e("Tarea3_Mhaisi", "Error al alinear o tintar iconos de la barra superior", e)
            }
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val perfilItem = menu?.findItem(R.id.edit)

        val textoBase = if (usuarioEstaRegistrado) {
            "$nombreUsuarioLogueado ▾"
        } else {
            "Iniciar Sesión"
        }

        val typedValue = android.util.TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)

        val colorTextoActivo = if (typedValue.resourceId != 0) {
            androidx.core.content.ContextCompat.getColor(this, typedValue.resourceId)
        } else {
            typedValue.data
        }

        perfilItem?.let { item ->
            val spannableString = android.text.SpannableString(textoBase)
            spannableString.setSpan(
                android.text.style.ForegroundColorSpan(colorTextoActivo),
                0,
                spannableString.length,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            item.title = spannableString
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
            else -> super.onOptionsItemSelected(item)
        }
    }
}