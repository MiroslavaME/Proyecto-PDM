package com.example.navegacion_menus

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class SecondActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            loadFragment(BebidasFragment())
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Log.d("Tarea3_Mhaisi", "Acción: Navegando a Bebidas")
                    loadFragment(BebidasFragment())
                    true
                }
                R.id.nav_search -> {
                    Log.d("Tarea3_Mhaisi", "Acción: Navegando a Comidas")
                    loadFragment(ComidasFragment())
                    true
                }
                R.id.nav_notifications -> {
                    Log.d("Tarea3_Mhaisi", "Acción: Navegando a Extras")
                    loadFragment(ExtrasFragment())
                    true
                }
                R.id.nav_profile -> {
                    Log.d("Tarea3_Mhaisi", "Acción: Revisando Mi Orden")
                    loadFragment(PedidoFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}