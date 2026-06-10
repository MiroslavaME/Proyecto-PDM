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

class ExtrasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_extras, container, false)

        setupCounter(root, R.id.btn_plus_oaxaca, R.id.btn_minus_oaxaca, R.id.tv_count_oaxaca, "Grano Oaxaca")
        setupCounter(root, R.id.btn_plus_michoacan, R.id.btn_minus_michoacan, R.id.tv_count_michoacan, "Molido Michoacán")

        setupCounter(root, R.id.btn_plus_rosa, R.id.btn_minus_rosa, R.id.tv_count_rosa, "Termo Rosa")
        setupCounter(root, R.id.btn_plus_aniversario, R.id.btn_minus_aniversario, R.id.tv_count_aniversario, "Termo Aniversario")
        setupCounter(root, R.id.btn_plus_tote, R.id.btn_minus_tote, R.id.tv_count_tote, "Tote Bag Mhaisi")

        setupCounter(root, R.id.btn_plus_galletas, R.id.btn_minus_galletas, R.id.tv_count_galletas, "Galletas Avena")
        setupCounter(root, R.id.btn_plus_mix, R.id.btn_minus_mix, R.id.tv_count_mix, "Mix Energético")

        configurarFavorito(root, R.id.fab_fav_oaxaca, "Grano Oaxaca")
        configurarFavorito(root, R.id.fab_fav_michoacan, "Molido Michoacán")
        configurarFavorito(root, R.id.fab_fav_rosa, "Termo Rosa")
        configurarFavorito(root, R.id.fab_fav_aniversario, "Termo Aniversario")
        configurarFavorito(root, R.id.fab_fav_tote, "Tote Bag Mhaisi")
        configurarFavorito(root, R.id.fab_fav_galletas, "Galletas Avena")
        configurarFavorito(root, R.id.fab_fav_mix, "Mix Energético")

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
            Log.d("Tarea3_Mhaisi", "Tienda: Se añadió '$productName'. Cantidad actual: $nuevoValor")
        }

        btnMinus?.setOnClickListener {
            val valorActual = obtenerValorGlobal(productName)
            if (valorActual > 0) {
                modificarValorGlobal(productName, false)
                val nuevoValor = obtenerValorGlobal(productName)
                tvCount.text = nuevoValor.toString()
                Log.d("Tarea3_Mhaisi", "Tienda: Se quitó '$productName'. Cantidad actual: $nuevoValor")
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
            "Grano Oaxaca" -> CarritoGlobal.oaxaca
            "Molido Michoacán" -> CarritoGlobal.michoacan
            "Termo Rosa" -> CarritoGlobal.rosa
            "Termo Aniversario" -> CarritoGlobal.aniversario
            "Tote Bag Mhaisi" -> CarritoGlobal.tote
            "Galletas Avena" -> CarritoGlobal.galletas
            "Mix Energético" -> CarritoGlobal.mix
            else -> 0
        }
    }

    private fun modificarValorGlobal(nombre: String, aumentar: Boolean) {
        val cambio = if (aumentar) 1 else -1
        when(nombre) {
            "Grano Oaxaca" -> CarritoGlobal.oaxaca += cambio
            "Molido Michoacán" -> CarritoGlobal.michoacan += cambio
            "Termo Rosa" -> CarritoGlobal.rosa += cambio
            "Termo Aniversario" -> CarritoGlobal.aniversario += cambio
            "Tote Bag Mhaisi" -> CarritoGlobal.tote += cambio
            "Galletas Avena" -> CarritoGlobal.galletas += cambio
            "Mix Energético" -> CarritoGlobal.mix += cambio
        }
    }
}