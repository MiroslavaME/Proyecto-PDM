package com.example.navegacion_menus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class PedidoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_pedido, container, false)

        val btnCheckout = root.findViewById<MaterialButton>(R.id.btn_checkout)
        val btnCancel = root.findViewById<MaterialButton>(R.id.btn_cancel_order)

        btnCheckout?.setOnClickListener {
            Log.d("Tarea3_Mhaisi", "Orden: El usuario presionó 'Realizar pedido'")
            mostrarDialogoConfirmacion()
        }

        btnCancel?.setOnClickListener {
            Log.d("Tarea3_Mhaisi", "Orden: El usuario canceló el pedido")
            CarritoGlobal.limpiarCarrito()
            cargarOrden(root)
            Toast.makeText(context, "Pedido cancelado", Toast.LENGTH_SHORT).show()
        }

        cargarOrden(root)
        return root
    }

    override fun onResume() {
        super.onResume()
        view?.let { cargarOrden(it) }
    }

    private fun cargarOrden(root: View) {
        val container = root.findViewById<LinearLayout>(R.id.container_items_orden)
        val tvEmpty = root.findViewById<TextView>(R.id.tv_empty_cart)
        val tvTotal = root.findViewById<TextView>(R.id.tv_total_pedido)
        val btnCheckout = root.findViewById<View>(R.id.btn_checkout)
        val btnCancel = root.findViewById<View>(R.id.btn_cancel_order)

        container?.removeAllViews()
        var sumaTotal = 0.0

        val productos = CarritoGlobal.obtenerProductosSeleccionados()

        if (productos.isEmpty()) {
            tvEmpty?.visibility = View.VISIBLE
            container?.visibility = View.GONE
            btnCheckout?.visibility = View.GONE
            btnCancel?.visibility = View.GONE
            tvTotal?.text = "$0.00"
        } else {
            tvEmpty?.visibility = View.GONE
            container?.visibility = View.VISIBLE
            btnCheckout?.visibility = View.VISIBLE
            btnCancel?.visibility = View.VISIBLE

            val grupos = productos.groupBy { "${it.nombre}|${it.especificaciones}" }

            for (entry in grupos) {
                val listaDeIguales = entry.value
                val p = listaDeIguales[0]

                val subtotalGrupo = p.precio * listaDeIguales.size
                sumaTotal += subtotalGrupo

                val itemView = layoutInflater.inflate(R.layout.item_orden_card, container, false)

                itemView.findViewById<TextView>(R.id.tv_nombre_item_orden).text = p.nombre
                itemView.findViewById<TextView>(R.id.tv_cantidad_item_orden).text = "Cantidad: ${listaDeIguales.size}"
                itemView.findViewById<TextView>(R.id.tv_precio_item_orden).text = "$${String.format("%.2f", subtotalGrupo)}"

                val tvEspec = itemView.findViewById<TextView>(R.id.tv_especificaciones_orden)
                if (p.especificaciones.isNotEmpty()) {
                    tvEspec.text = p.especificaciones
                } else {
                    tvEspec.text = when {
                        p.nombre == "Espresso" -> "• Tamaño Grande\n• Azúcar"
                        listOf("Latte Clásico", "Capuccino", "Chocolate caliente", "Matcha").contains(p.nombre) ->
                            "• Tamaño Grande\n• Leche Entera\n• Azúcar"

                        listOf("Cheesecake", "Pastel Zanahoria").contains(p.nombre) ->
                            "• Tamaño Estándar\n• Sin toppings extra"

                        p.categoria == "comida" -> "• Preparación Tradicional"
                        p.categoria == "extra" -> "• Producto Cerrado"
                        else -> "• Tamaño Grande\n• Azúcar"
                    }
                }
                val imgProducto = itemView.findViewById<ImageView>(R.id.img_item_orden)
                when (p.nombre) {
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

                val btnPerso = itemView.findViewById<MaterialButton>(R.id.btn_customize_item)
                if (p.categoria == "extra") {
                    btnPerso?.visibility = View.GONE
                } else {
                    btnPerso?.visibility = View.VISIBLE
                    btnPerso?.setOnClickListener {
                        val intent = Intent(requireContext(), CustomizationActivity::class.java)
                        intent.putExtra("EXTRA_NOMBRE", p.nombre)
                        intent.putExtra("EXTRA_CATEGORIA", p.categoria)

                        val imgRes = when (p.nombre) {
                            "Latte Clásico"      -> R.drawable.latte
                            "Espresso"           -> R.drawable.espresso
                            "Chocolate Caliente" -> R.drawable.chocolate
                            "Capuccino"          -> R.drawable.capuccino
                            "Té Frío"            -> R.drawable.tefrio
                            "Limonada de Fresa"  -> R.drawable.limonada
                            "Smoothie Asha"      -> R.drawable.smoothie
                            "Matcha"             -> R.drawable.matcha
                            "Baguette Pizza"     -> R.drawable.baguette
                            "Ensalada César"     -> R.drawable.ensalada
                            "Sandwich Pavo"      -> R.drawable.sandwich
                            "Bagel Guacamole"    -> R.drawable.bagel
                            "Dona Caramelo"      -> R.drawable.dona
                            "Tarta de Moras"     -> R.drawable.tarta
                            "Pastel Zanahoria"   -> R.drawable.pastel
                            "Cheesecake"         -> R.drawable.cheesecake
                            else                 -> R.drawable.img
                        }

                        intent.putExtra("EXTRA_IMAGEN", imgRes)
                        startActivity(intent)
                    }
                }

                container?.addView(itemView)
            }
            tvTotal?.text = "$${String.format("%.2f", sumaTotal)}"
        }
    }

    private fun mostrarDialogoConfirmacion() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirmar_pedido, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val containerResumen = dialogView.findViewById<LinearLayout>(R.id.container_resumen_pedido)
        val tvTotal = dialogView.findViewById<TextView>(R.id.tv_total_confirmacion)
        val btnFinalizar = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_finalizar_pedido)
        val btnCancelar = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_cancelar_pedido)
        val rgMetodoPago = dialogView.findViewById<android.widget.RadioGroup>(R.id.rg_metodo_pago)

        val etCupon = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_codigo_cupon)
        val btnAplicarCupon = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_aplicar_cupon)

        var totalOriginal = 0.0
        containerResumen?.removeAllViews()

        val productos = CarritoGlobal.obtenerProductosSeleccionados()
        val grupos = productos.groupBy { it.nombre }

        for (entry in grupos) {
            val listaDeIguales = entry.value
            val p = listaDeIguales[0]
            val cantidad = listaDeIguales.size
            val subtotalProducto = p.precio * cantidad
            totalOriginal += subtotalProducto

            val tvProducto = TextView(context).apply {
                text = "${cantidad}x  ${p.nombre}  ->  $${String.format("%.2f", subtotalProducto)}"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setPadding(0, 8, 0, 8)
                val typedValue = TypedValue()
                context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
                setTextColor(typedValue.data)
            }
            containerResumen?.addView(tvProducto)
        }

        tvTotal?.text = "Tu pedido"

        var totalFinal = totalOriginal
        var cuponAplicado = ""

        val tvTotalFinal = TextView(context).apply {
            text = "\nTotal a pagar: $${String.format("%.2f", totalFinal)}"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTypeface(null, android.graphics.Typeface.BOLD)
            val typedValue = TypedValue()
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
            setTextColor(typedValue.data)
            setPadding(0, 12, 0, 4)
        }
        containerResumen?.addView(tvTotalFinal)

        btnAplicarCupon?.setOnClickListener {
            val codigoIngresado = etCupon?.text?.toString()?.trim()?.uppercase() ?: ""
            val prefs = requireContext().getSharedPreferences("MhaisiPrefs", Context.MODE_PRIVATE)

            when (codigoIngresado) {
                "MHAISIBIENVENIDA" -> {
                    val yaUsado = prefs.getBoolean("cupon_mhaisibienvenida_usado", false)

                    if (!yaUsado) {
                        val descuento = totalOriginal * 0.10
                        totalFinal = totalOriginal - descuento
                        cuponAplicado = "MHAISIBIENVENIDA"
                        tvTotalFinal.text = "\nTotal a pagar (10% desc. aplicado): $${String.format("%.2f", totalFinal)}"
                        Toast.makeText(context, "¡Cupón de Bienvenida aplicado! ", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Este cupón de bienvenida ya fue utilizado ", Toast.LENGTH_SHORT).show()
                    }
                }
                "MHAISICUMPLE" -> {
                    val calendar = java.util.Calendar.getInstance()
                    val mesActual = calendar.get(java.util.Calendar.MONTH) + 1
                    val mesNacimientoUsuario = prefs.getInt("user_birth_month", 0)
                    val cumpleYaUsado = prefs.getBoolean("cupon_mhaisicump_usado", false)

                    if (mesNacimientoUsuario == mesActual) {
                        if (!cumpleYaUsado) {
                            val bebidaRegalo = productos.filter { it.categoria == "bebida" }.maxByOrNull { it.precio }

                            if (bebidaRegalo != null) {
                                totalFinal = totalOriginal - bebidaRegalo.precio
                                cuponAplicado = "MHAISICUMPLE"
                                tvTotalFinal.text = "\nTotal a pagar (Bebida gratis aplicada): $${String.format("%.2f", totalFinal)}"
                                Toast.makeText(context, "¡Cupón de Cumpleaños aplicado! ", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Requieres al menos una bebida en tu orden ", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Tu cupón de cumpleaños ya fue utilizado ", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Este cupón solo es válido en el mes de tu cumpleaños ", Toast.LENGTH_LONG).show()
                    }
                }
                "" -> {
                    totalFinal = totalOriginal
                    cuponAplicado = ""
                    tvTotalFinal.text = "\nTotal a pagar: $${String.format("%.2f", totalFinal)}"
                    Toast.makeText(context, "Introduce un código", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    totalFinal = totalOriginal
                    cuponAplicado = ""
                    tvTotalFinal.text = "\nTotal a pagar: $${String.format("%.2f", totalFinal)}"
                    Toast.makeText(context, "Código inválido", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnFinalizar?.setOnClickListener {
            val metodoSeleccionado = when (rgMetodoPago?.checkedRadioButtonId) {
                R.id.rb_tarjeta  -> "Tarjeta de Crédito/Débito"
                R.id.rb_efectivo -> "Efectivo en caja"
                R.id.rb_wallet   -> "Apple Wallet"
                else             -> "No seleccionado"
            }

            dialog.dismiss()

            if (cuponAplicado.isNotEmpty()) {
                val prefs = requireContext().getSharedPreferences("MhaisiPrefs", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("cupon_${cuponAplicado.lowercase()}_usado", true).apply()
            }

            Toast.makeText(
                context,
                "Pedido confirmado con $metodoSeleccionado por $${String.format("%.2f", totalFinal)}.",
                Toast.LENGTH_LONG
            ).show()

            CarritoGlobal.limpiarCarrito()
            view?.let { cargarOrden(it) }
        }

        dialog.show()
    }
}