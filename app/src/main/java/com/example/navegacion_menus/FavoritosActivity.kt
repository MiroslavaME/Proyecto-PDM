package com.example.navegacion_menus

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FavoritosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_favoritos)

        val rootView = window.decorView.rootView
        refrescarListaFavoritos(rootView)
    }

    override fun onStart() {
        super.onStart()
        val rootView = window.decorView.rootView
        refrescarListaFavoritos(rootView)
    }

    private fun refrescarListaFavoritos(rootView: View) {
        val container = rootView.findViewById<LinearLayout>(R.id.container_items_favoritos)
        val tvEmpty = rootView.findViewById<TextView>(R.id.tv_empty_favoritos)

        container?.removeAllViews()
        val favoritosActuales = CarritoGlobal.listaFavoritos.toList()

        if (favoritosActuales.isEmpty()) {
            tvEmpty?.visibility = View.VISIBLE
            container?.visibility = View.GONE
        } else {
            tvEmpty?.visibility = View.GONE
            container?.visibility = View.VISIBLE

            for (nombreProducto in favoritosActuales) {
                val itemView = layoutInflater.inflate(R.layout.item_favorito_card, container, false)

                itemView.findViewById<TextView>(R.id.tv_nombre_item_favorito).text = nombreProducto

                val precioBase = CarritoGlobal.obtenerPrecioBase(nombreProducto)
                itemView.findViewById<TextView>(R.id.tv_precio_item_favorito).text = "$${String.format("%.2f", precioBase)}"

                val imgProducto = itemView.findViewById<ImageView>(R.id.img_item_favorito)

                when (nombreProducto) {
                    "Latte Clásico"      -> imgProducto.setImageResource(R.drawable.latte)
                    "Espresso"           -> imgProducto.setImageResource(R.drawable.espresso)
                    "Chocolate Caliente" -> imgProducto.setImageResource(R.drawable.chocolate)
                    "Capuccino"          -> imgProducto.setImageResource(R.drawable.capuccino)
                    "Té Frío"            -> imgProducto.setImageResource(R.drawable.tefrio)
                    "Limonada de Fresa"  -> imgProducto.setImageResource(R.drawable.limonada)
                    "Smoothie Asha"      -> imgProducto.setImageResource(R.drawable.smoothie)
                    "Matcha"             -> imgProducto.setImageResource(R.drawable.matcha)
                    "Baguette Pizza"     -> imgProducto.setImageResource(R.drawable.baguette)
                    "Ensalada César"     -> imgProducto.setImageResource(R.drawable.ensalada)
                    "Sandwich Pavo"      -> imgProducto.setImageResource(R.drawable.sandwich)
                    "Bagel Guacamole"    -> imgProducto.setImageResource(R.drawable.bagel)
                    "Dona Caramelo"      -> imgProducto.setImageResource(R.drawable.dona)
                    "Tarta de Moras"     -> imgProducto.setImageResource(R.drawable.tarta)
                    "Pastel Zanahoria"   -> imgProducto.setImageResource(R.drawable.pastel)
                    "Cheesecake"         -> imgProducto.setImageResource(R.drawable.cheesecake)
                    "Grano Oaxaca"       -> imgProducto.setImageResource(R.drawable.grano)
                    "Molido Michoacán"   -> imgProducto.setImageResource(R.drawable.molido)
                    "Termo Rosa"         -> imgProducto.setImageResource(R.drawable.termo_rosa)
                    "Termo Aniversario"  -> imgProducto.setImageResource(R.drawable.aniversario)
                    "Tote Bag Mhaisi"    -> imgProducto.setImageResource(R.drawable.tote)
                    "Galletas Avena"     -> imgProducto.setImageResource(R.drawable.galletas)
                    "Mix Energético"     -> imgProducto.setImageResource(R.drawable.mix)

                    else                 -> imgProducto.setImageResource(R.drawable.img)
                }

                val btnRemove = itemView.findViewById<ImageButton>(R.id.btn_remove_favorito)
                btnRemove.setOnClickListener {
                    CarritoGlobal.listaFavoritos.remove(nombreProducto)
                    Toast.makeText(this, "$nombreProducto eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    refrescarListaFavoritos(rootView)
                }

                container?.addView(itemView)
            }
        }
    }
}