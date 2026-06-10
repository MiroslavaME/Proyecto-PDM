package com.example.navegacion_menus

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ComidasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_comidas, container, false)

        setupCounter(root, R.id.btn_plus_baguette, R.id.btn_minus_baguette, R.id.tv_count_baguette, "Baguette Pizza")
        setupCounter(root, R.id.btn_plus_cesar, R.id.btn_minus_cesar, R.id.tv_count_cesar, "Ensalada César")
        setupCounter(root, R.id.btn_plus_pavo, R.id.btn_minus_pavo, R.id.tv_count_pavo, "Sandwich Pavo")
        setupCounter(root, R.id.btn_plus_bagel, R.id.btn_minus_bagel, R.id.tv_count_bagel, "Bagel Guacamole")

        setupCounter(root, R.id.btn_plus_dona, R.id.btn_minus_dona, R.id.tv_count_dona, "Dona Caramelo")
        setupCounter(root, R.id.btn_plus_tarta, R.id.btn_minus_tarta, R.id.tv_count_tarta, "Tarta de Moras")
        setupCounter(root, R.id.btn_plus_zanahoria, R.id.btn_minus_zanahoria, R.id.tv_count_zanahoria, "Pastel Zanahoria")
        setupCounter(root, R.id.btn_plus_cheesecake, R.id.btn_minus_cheesecake, R.id.tv_count_cheesecake, "Cheesecake")

        configurarFavorito(root, R.id.fab_fav_baguette, "Baguette Pizza")
        configurarFavorito(root, R.id.fab_fav_cesar, "Ensalada César")
        configurarFavorito(root, R.id.fab_fav_pavo, "Sandwich Pavo")
        configurarFavorito(root, R.id.fab_fav_bagel, "Bagel Guacamole")
        configurarFavorito(root, R.id.fab_fav_dona, "Dona Caramelo")
        configurarFavorito(root, R.id.fab_fav_zanahoria, "Pastel Zanahoria")
        configurarFavorito(root, R.id.fab_fav_tarta, "Tarta de Moras")
        configurarFavorito(root, R.id.fab_fav_cheesecake, "Cheesecake")

        return root
    }

    private fun setupCounter(view: View, plusId: Int, minusId: Int, countId: Int, productName: String) {
        val btnPlus = view.findViewById<View>(plusId)
        val btnMinus = view.findViewById<View>(minusId)
        val tvCount = view.findViewById<TextView>(countId)

        tvCount.text = obtenerValorGlobal(productName).toString()

        btnPlus?.setOnClickListener {
            modificarValorGlobal(productName, true)
            val nuevoValor = obtenerValorGlobal(productName)
            tvCount.text = nuevoValor.toString()
            Log.d("Tarea3_Mhaisi", "Comidas: Se añadió '$productName'. Total en carrito: $nuevoValor")
        }

        btnMinus?.setOnClickListener {
            val valorActual = obtenerValorGlobal(productName)
            if (valorActual > 0) {
                modificarValorGlobal(productName, false)
                val nuevoValor = obtenerValorGlobal(productName)
                tvCount.text = nuevoValor.toString()
                Log.d("Tarea3_Mhaisi", "Comidas: Se quitó '$productName'. Total en carrito: $nuevoValor")
            }
        }
    }

    private fun configurarFavorito(rootView: View, fabId: Int, nombreProducto: String) {
        val fab = rootView.findViewById<ImageButton>(fabId)

        if (CarritoGlobal.listaFavoritos.contains(nombreProducto)) {
            fab?.setImageResource(R.drawable.ic_broken_24dp)
        } else {
            fab?.setImageResource(R.drawable.ic_favorite_24dp)
        }

        fab?.setOnClickListener {
            if (CarritoGlobal.listaFavoritos.contains(nombreProducto)) {
                CarritoGlobal.listaFavoritos.remove(nombreProducto)
                fab.setImageResource(R.drawable.ic_favorite_24dp)
                Toast.makeText(context, "$nombreProducto eliminado de favoritos", Toast.LENGTH_SHORT).show()
            } else {
                CarritoGlobal.listaFavoritos.add(nombreProducto)
                fab.setImageResource(R.drawable.ic_broken_24dp)
                Toast.makeText(context, "$nombreProducto ¡añadido a tus favoritos!", Toast.LENGTH_SHORT).show()
            }
            Log.d("Tarea3_Mhaisi", "Favoritos actuales: ${CarritoGlobal.listaFavoritos}")
        }
    }

    private fun obtenerValorGlobal(nombre: String): Int {
        return when(nombre) {
            "Baguette Pizza" -> CarritoGlobal.baguette
            "Ensalada César" -> CarritoGlobal.cesar
            "Sandwich Pavo" -> CarritoGlobal.pavo
            "Bagel Guacamole" -> CarritoGlobal.bagel
            "Dona Caramelo" -> CarritoGlobal.dona
            "Tarta de Moras" -> CarritoGlobal.tarta
            "Pastel Zanahoria" -> CarritoGlobal.zanahoria
            "Cheesecake" -> CarritoGlobal.cheesecake
            else -> 0
        }
    }

    private fun modificarValorGlobal(nombre: String, aumentar: Boolean) {
        val cambio = if (aumentar) 1 else -1
        when(nombre) {
            "Baguette Pizza" -> CarritoGlobal.baguette += cambio
            "Ensalada César" -> CarritoGlobal.cesar += cambio
            "Sandwich Pavo" -> CarritoGlobal.pavo += cambio
            "Bagel Guacamole" -> CarritoGlobal.bagel += cambio
            "Dona Caramelo" -> CarritoGlobal.dona += cambio
            "Tarta de Moras" -> CarritoGlobal.tarta += cambio
            "Pastel Zanahoria" -> CarritoGlobal.zanahoria += cambio
            "Cheesecake" -> CarritoGlobal.cheesecake += cambio
        }
    }
}