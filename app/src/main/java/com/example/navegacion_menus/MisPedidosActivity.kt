package com.example.navegacion_menus

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class MisPedidosActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var dbHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_pedidos)
        } catch (e: Exception) {
            Log.e("MhaisiError", "No se pudo cargar el layout activity_pedidos", e)
            Toast.makeText(this, "Error de diseño XML", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        container = findViewById(R.id.container_lista_pedidos) ?: return
        dbHelper = DataBaseHelper(this)
    }

    override fun onResume() {
        super.onResume()
        cargarHistorialPedidos()
    }

    private fun cargarHistorialPedidos() {
        runOnUiThread {
            container.removeAllViews()

            val prefs = getSharedPreferences("MhaisiPrefs", android.content.Context.MODE_PRIVATE)
            val isLoggedIn = prefs.getBoolean("is_logged_in", false)

            val tv = TypedValue()
            theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, tv, true)
            val colorTextoDinamico = tv.data

            theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, tv, true)
            val colorCardDinamico = tv.data

            // --- FLUJO PARA USUARIO NO REGISTRADO (INVITADO) ---
            if (!isLoggedIn) {
                val idPedidoInvitado = prefs.getLong("ultimo_pedido_invitado_id", -1L)
                val pedidoUnico = if (idPedidoInvitado != -1L) dbHelper.obtenerPedidoPorId(idPedidoInvitado) else null

                if (pedidoUnico == null) {
                    val tvVacio = TextView(this).apply {
                        text = "No tienes ningún pedido activo en este momento.\nRegístrate para mantener un historial de tus compras."
                        textSize = 16f
                        setTextColor(colorTextoDinamico)
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                        setPadding(40, 60, 40, 0)
                    }
                    container.addView(tvVacio)
                    return@runOnUiThread
                }

                // Mostrar exclusivamente la sección del pedido en curso sin historial
                agregarSeparadorSeccion("Tu pedido en curso (Modo Invitado)")

                val cardInvitado = crearTarjetaPedido(pedidoUnico, colorCardDinamico, colorTextoDinamico)
                val btnVerQr = MaterialButton(this, null, com.google.android.material.R.attr.materialButtonStyle).apply {
                    text = "Ver Ticket QR"
                    textSize = 12f
                            setBackgroundColor(android.graphics.Color.parseColor("#006241"))
                    setTextColor(android.graphics.Color.WHITE)
                    cornerRadius = 12
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 16
                        gravity = Gravity.END
                    }
                }
                btnVerQr.setOnClickListener {
                    mostrarQrPedido(pedidoUnico)
                }

                (cardInvitado.getChildAt(0) as LinearLayout).addView(btnVerQr)
                container.addView(cardInvitado)
                return@runOnUiThread
            }

            // --- FLUJO PARA USUARIO REGISTRADO (CÓDIGO EXISTENTE) ---
            val currentUserId = prefs.getInt("user_id", 1)
            val historial = dbHelper.obtenerPedidosPorUsuario(currentUserId)

            if (historial.isEmpty()) {
                val tvVacio = TextView(this).apply {
                    text = "Aún no tienes pedidos registrados."
                    textSize = 16f
                    setTextColor(colorTextoDinamico)
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setPadding(0, 40, 0, 0)
                }
                container.addView(tvVacio)
                return@runOnUiThread
            }

            agregarSeparadorSeccion("Pedido en curso / Última orden")
            val ultimoPedido = historial[0]
            val cardUltimo = crearTarjetaPedido(ultimoPedido, colorCardDinamico, colorTextoDinamico)

            val btnVerQr = MaterialButton(this, null, com.google.android.material.R.attr.materialButtonStyle).apply {
                text = "Ver Ticket QR"
                textSize = 12f
                        setBackgroundColor(android.graphics.Color.parseColor("#006241"))
                setTextColor(android.graphics.Color.WHITE)
                cornerRadius = 12
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 16
                    gravity = Gravity.END
                }
            }
            btnVerQr.setOnClickListener {
                mostrarQrPedido(ultimoPedido)
            }
            (cardUltimo.getChildAt(0) as LinearLayout).addView(btnVerQr)
            container.addView(cardUltimo)

            // Sección 2: Pedidos antiguos
            if (historial.size > 1) {
                agregarSeparadorSeccion("Pedidos anteriores")
                for (i in 1 until historial.size) {
                    val pedidoAntiguo = historial[i]
                    val cardAntiguo = crearTarjetaPedido(pedidoAntiguo, colorCardDinamico, colorTextoDinamico)

                    val btnReordenar = MaterialButton(this, null, com.google.android.material.R.attr.materialButtonStyle).apply {
                        text = "Volver a pedir"
                        textSize = 12f
                                setBackgroundColor(android.graphics.Color.parseColor("#1A1A1A"))
                        setTextColor(android.graphics.Color.WHITE)
                        cornerRadius = 12
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = 16
                            gravity = Gravity.END
                        }
                    }
                    btnReordenar.setOnClickListener {
                        procesarReordenar(pedidoAntiguo)
                    }
                    (cardAntiguo.getChildAt(0) as LinearLayout).addView(btnReordenar)
                    container.addView(cardAntiguo)
                }
            }
        }
    }

    private fun crearTarjetaPedido(contenido: String, colorCard: Int, colorTexto: Int): MaterialCardView {
        val card = MaterialCardView(this).apply {
            radius = 24f
            cardElevation = 4f
            setStrokeWidth(0)
            setCardBackgroundColor(colorCard)
            preventCornerOverlap = true
            useCompatPadding = true
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 8, 0, 8)
        }
        card.layoutParams = params

        val layoutInterno = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val texto = TextView(this).apply {
            text = contenido
            textSize = 15f
            setLineSpacing(0f, 1.3f)
            setTextColor(colorTexto)
        }

        layoutInterno.addView(texto)
        card.addView(layoutInterno)
        return card
    }

    private fun agregarSeparadorSeccion(titulo: String) {
        val contenedorSeccion = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 32, 0, 16)
        }

        val textoTitulo = TextView(this).apply {
            text = titulo
            textSize = 14f
            // CORREGIDO: Asignación válida de fuente Sans Serif Medium mediante recursos
            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            setTextColor(android.graphics.Color.GRAY)
        }

        val lineaDivisoria = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
            ).apply {
                topMargin = 8
            }
            setBackgroundColor(android.graphics.Color.LTGRAY)
        }

        contenedorSeccion.addView(textoTitulo)
        contenedorSeccion.addView(lineaDivisoria)
        container.addView(contenedorSeccion)
    }

    private fun procesarReordenar(textoPedido: String) {
        try {
            val lineas = textoPedido.split("\n")
            val resumenProductos = if (lineas.size > 1) lineas[1] else ""

            if (resumenProductos.isEmpty() || resumenProductos == "Pedido Mhaisi") {
                Toast.makeText(this, "No se pueden identificar los productos de este registro", Toast.LENGTH_SHORT).show()
                return
            }

            CarritoGlobal.limpiarCarrito()

            val items = resumenProductos.split(", ")
            for (item in items) {
                val partes = item.split("x ")
                if (partes.size == 2) {
                    val cantidad = partes[0].trim().toIntOrNull() ?: 1
                    val nombreProducto = partes[1].trim()

                    when (nombreProducto) {
                        "Latte Clásico"      -> CarritoGlobal.latte = cantidad
                        "Espresso"           -> CarritoGlobal.espresso = cantidad
                        // CORREGIDO: 'quantity' cambiado a 'cantidad'
                        "Chocolate Caliente" -> CarritoGlobal.chocolate = cantidad
                        "Capuccino"          -> CarritoGlobal.capuccino = cantidad
                        "Té Frío"            -> CarritoGlobal.teFrio = cantidad
                        "Limonada de Fresa"  -> CarritoGlobal.limonada = cantidad
                        "Smoothie Asha"      -> CarritoGlobal.smoothie = cantidad
                        "Matcha"             -> CarritoGlobal.matcha = cantidad
                        "Baguette Pizza"     -> CarritoGlobal.baguette = cantidad
                        "Ensalada César"     -> CarritoGlobal.cesar = cantidad
                        "Sandwich Pavo"      -> CarritoGlobal.pavo = cantidad
                        "Bagel Guacamole"    -> CarritoGlobal.bagel = cantidad
                        "Dona Caramelo"      -> CarritoGlobal.dona = cantidad
                        "Tarta de Moras"     -> CarritoGlobal.tarta = cantidad
                        "Pastel Zanahoria"   -> CarritoGlobal.zanahoria = cantidad
                        "Cheesecake"         -> CarritoGlobal.cheesecake = cantidad
                        "Grano Oaxaca"       -> CarritoGlobal.oaxaca = cantidad
                        "Molido Michoacán"   -> CarritoGlobal.michoacan = cantidad
                        "Termo Rosa"         -> CarritoGlobal.rosa = cantidad
                        "Termo Aniversario"  -> CarritoGlobal.aniversario = cantidad
                        "Tote Bag Mhaisi"    -> CarritoGlobal.tote = cantidad
                        "Galletas Avena"     -> CarritoGlobal.galletas = cantidad
                        "Mix Energético"     -> CarritoGlobal.mix = cantidad
                    }
                }
            }

            Toast.makeText(this, "Productos agregados al carrito de nuevo", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Exception) {
            Log.e("MhaisiReorder", "Error al parsear el texto de reordenar", e)
            Toast.makeText(this, "Error al replicar la orden", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarQrPedido(datosPedido: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirmar_pedido, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val containerResumen = dialogView.findViewById<LinearLayout>(R.id.container_resumen_pedido)
        val tvTotal = dialogView.findViewById<TextView>(R.id.tv_total_confirmacion)
        val btnFinalizar = dialogView.findViewById<MaterialButton>(R.id.btn_finalizar_pedido)
        val containerQr = dialogView.findViewById<LinearLayout>(R.id.container_qr_final)

        dialogView.findViewById<LinearLayout>(R.id.layout_cupon_seccion)?.visibility = View.GONE
        dialogView.findViewById<LinearLayout>(R.id.layout_pago_seccion)?.visibility = View.GONE
        dialogView.findViewById<View>(R.id.divisor_linea)?.visibility = View.GONE
        dialogView.findViewById<View>(R.id.btn_cancelar_pedido)?.visibility = View.GONE

        containerResumen?.removeAllViews()

        val tvDatos = TextView(this).apply {
            text = datosPedido
            textSize = 16f
            setLineSpacing(0f, 1.2f)
            val typedValue = TypedValue()
            theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
            setTextColor(typedValue.data)
            setPadding(0, 16, 0, 16)
        }
        containerResumen?.addView(tvDatos)

        tvTotal?.text = "Ticket de Compra"
        btnFinalizar?.text = "Cerrar"
        containerQr?.visibility = View.VISIBLE

        btnFinalizar?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}