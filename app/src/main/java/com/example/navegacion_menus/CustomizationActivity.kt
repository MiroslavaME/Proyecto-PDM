package com.example.navegacion_menus

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CustomizationActivity : AppCompatActivity() {
    private var nombre: String = ""
    private var categoria: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customization)

        nombre = intent.getStringExtra("EXTRA_NOMBRE") ?: ""
        categoria = intent.getStringExtra("EXTRA_CATEGORIA") ?: ""

        val instancias = CarritoGlobal.obtenerProductosSeleccionados().filter { it.nombre == nombre }
        val tamañoInstancias = instancias.size
        val imagenRes = intent.getIntExtra("EXTRA_IMAGEN", R.drawable.img)

        findViewById<TextView>(R.id.tv_custom_name).text = nombre

        val imgHeader = findViewById<ImageView>(R.id.img_custom_header)
        imgHeader?.setImageResource(imagenRes)

        val container = findViewById<LinearLayout>(R.id.container_instancias)
        val btnSave = findViewById<Button>(R.id.btn_save_custom)

        for (i in 1..tamañoInstancias) {
            val block = layoutInflater.inflate(R.layout.item_custom_block, container, false)
            block.findViewById<TextView>(R.id.tv_item_number).text = "Personalización $i de $tamañoInstancias"

            configurarVisibilidadBloque(block, nombre, categoria)
            configurarLogicaColumnas(block)

            if (i <= instancias.size) {
                cargarPreferenciasPrevias(block, instancias[i-1].especificaciones)
            }
            container.addView(block)
        }

        btnSave.setOnClickListener {
            for (indiceView in 0 until container.childCount) {
                val view = container.getChildAt(indiceView)
                var specs = ""
                var precioCalculado = CarritoGlobal.obtenerPrecioBase(nombre)

                val sSizeContainer = view.findViewById<View>(R.id.section_size_container)
                val sLeche = view.findViewById<View>(R.id.section_leche)
                val sEndulzante = view.findViewById<View>(R.id.section_endulzante)
                val sSalados = view.findViewById<View>(R.id.section_salados)
                val sDulces = view.findViewById<View>(R.id.section_dulces)

                if (sSizeContainer != null && sSizeContainer.visibility == View.VISIBLE) {
                    val rgSize = view.findViewById<RadioGroup>(R.id.section_size)
                    val checkedId = rgSize?.checkedRadioButtonId ?: -1
                    val rbSize = if (checkedId != -1) view.findViewById<RadioButton>(checkedId) else null
                    val sizeText = rbSize?.text?.toString() ?: "Grande"

                    if (categoria.equals("bebida", ignoreCase = true)) {
                        when (sizeText) {
                            "Chico" -> precioCalculado -= 20.0
                            "Mediano" -> precioCalculado -= 10.0
                        }
                        specs += "• Tamaño $sizeText\n"
                    } else if (categoria.equals("comida", ignoreCase = true)) {
                        when (sizeText) {
                            "Mediano" -> precioCalculado += 15.0
                            "Familiar" -> precioCalculado += 40.0
                        }
                        if (listOf("Cheesecake", "Tarta de Moras", "Pastel Zanahoria").contains(nombre)) {
                            specs += "• Tamaño $sizeText\n"
                        }
                    }
                }

                if (sLeche != null && sLeche.visibility == View.VISIBLE) {
                    val ids = listOf(R.id.rb_entera, R.id.rb_deslacto, R.id.rb_almendra, R.id.rb_coco)
                    val sel = ids.map { view.findViewById<RadioButton>(it) }.find { it?.isChecked == true }
                    val lecheStr = sel?.text ?: "Entera"
                    precioCalculado += when (lecheStr) {
                        "Almendra", "Coco" -> 15.0
                        "Deslactosada" -> 5.0
                        else -> 0.0
                    }
                    specs += "• Leche $lecheStr\n"
                }

                if (sEndulzante != null && sEndulzante.visibility == View.VISIBLE) {
                    val ids = listOf(R.id.rb_sin_azucar, R.id.rb_azucar, R.id.rb_stevia, R.id.rb_miel)
                    val sel = ids.map { view.findViewById<RadioButton>(it) }.find { it?.isChecked == true }
                    specs += "• ${sel?.text ?: "Azúcar"}\n"
                }

                if (sSalados != null && sSalados.visibility == View.VISIBLE) {
                    val ingredientesBase = listOf(
                        R.id.cb_mayonesa to "Mayo", R.id.cb_mostaza to "Mostaza", R.id.cb_catsup to "Catsup",
                        R.id.cb_chipotle to "Chipotle", R.id.cb_cebolla to "Cebolla", R.id.cb_jitomate to "Jitomate",
                        R.id.cb_lechuga to "Lechuga", R.id.cb_picante to "Chiles"
                    )
                    val extrasConCosto = listOf(
                        R.id.cb_extra_queso to "Extra Queso",
                        R.id.cb_extra_aguacate to "Aguacate",
                        R.id.cb_extra_aderezo to "Extra Aderezo"
                    )

                    val seleccionadosBase = mutableListOf<String>()
                    val seleccionadosExtras = mutableListOf<String>()

                    ingredientesBase.forEach { (id, label) ->
                        if (view.findViewById<CheckBox>(id)?.isChecked == true) seleccionadosBase.add(label)
                    }
                    extrasConCosto.forEach { (id, label) ->
                        if (view.findViewById<CheckBox>(id)?.isChecked == true) {
                            seleccionadosExtras.add(label)
                            precioCalculado += 15.0
                        }
                    }

                    if (seleccionadosBase.size == ingredientesBase.size) {
                        specs += "• Preparación Tradicional\n"
                    } else if (seleccionadosBase.isEmpty()) {
                        specs += "• Sin vegetales ni salsas\n"
                    } else {
                        specs += "• Notas: ${seleccionadosBase.joinToString(", ")}\n"
                    }

                    if (seleccionadosExtras.isNotEmpty()) {
                        specs += "• Extras: ${seleccionadosExtras.joinToString(", ")}\n"
                    }
                }

                if (sDulces != null && sDulces.visibility == View.VISIBLE) {
                    val toppingsDisponibles = listOf(
                        R.id.cb_extra_chocolate to "Extra Chocolate",
                        R.id.cb_extra_caramelo to "Extra Caramelo",
                        R.id.cb_extra_nueces to "Extra Nueces"
                    )

                    val toppingsSeleccionados = mutableListOf<String>()

                    toppingsDisponibles.forEach { (id, label) ->
                        val cbTopping = view.findViewById<CheckBox>(id)
                        if (cbTopping != null && cbTopping.isChecked) {
                            toppingsSeleccionados.add(label)
                            precioCalculado += 15.0
                        }
                    }

                    if (toppingsSeleccionados.isNotEmpty()) {
                        specs += "• Toppings: ${toppingsSeleccionados.joinToString(", ")}\n"
                    } else {
                        specs += "• Sin toppings extra\n"
                    }
                }

                if (indiceView < instancias.size) {
                    instancias[indiceView].especificaciones = specs.trim()
                    instancias[indiceView].precio = precioCalculado
                }
            }
            finish()
        }
    }

    private fun configurarLogicaColumnas(v: View) {
        fun vincular(ids: List<Int>) {
            val botones = ids.map { v.findViewById<RadioButton>(it) }
            botones.forEach { rb ->
                rb?.setOnClickListener {
                    botones.forEach { it?.isChecked = false }
                    rb.isChecked = true
                }
            }
        }
        vincular(listOf(R.id.rb_entera, R.id.rb_deslacto, R.id.rb_almendra, R.id.rb_coco))
        vincular(listOf(R.id.rb_sin_azucar, R.id.rb_azucar, R.id.rb_stevia, R.id.rb_miel))
    }

    private fun configurarVisibilidadBloque(v: View, nombre: String, cat: String) {
        val sSizeContainer = v.findViewById<View>(R.id.section_size_container)
        val sSalados = v.findViewById<View>(R.id.section_salados)
        val sDulces = v.findViewById<View>(R.id.section_dulces)
        val sLeche = v.findViewById<View>(R.id.section_leche)
        val sEndulzante = v.findViewById<View>(R.id.section_endulzante)

        val rowSalsasOpcionales = v.findViewById<View>(R.id.row_salsas_opcionales)
        val cbLechuga = v.findViewById<CheckBox>(R.id.cb_lechuga)
        val cbExtraAguacate = v.findViewById<CheckBox>(R.id.cb_extra_aguacate)

        if (cat.equals("bebida", ignoreCase = true)) {
            sSizeContainer.visibility = View.VISIBLE
            sEndulzante.visibility = View.VISIBLE
            sSalados.visibility = View.GONE
            sDulces.visibility = View.GONE

            val esBebidaConLeche = listOf("Latte Clásico", "Capuccino", "Chocolate caliente", "Matcha").contains(nombre)
            if (nombre == "Espresso") {
                sLeche.visibility = View.GONE
            } else {
                sLeche.visibility = if (esBebidaConLeche) View.VISIBLE else View.GONE
            }

        } else if (cat.equals("comida", ignoreCase = true)) {
            val esPostre = listOf("Dona Caramelo", "Cheesecake", "Tarta de Moras", "Pastel Zanahoria").contains(nombre)
            sLeche.visibility = View.GONE
            sEndulzante.visibility = View.GONE

            if (esPostre) {
                sSalados.visibility = View.GONE
                sDulces.visibility = View.VISIBLE
                val rowDulcesPrimera = v.findViewById<View>(R.id.row_dulces_primera)

                if (nombre == "Dona Caramelo" || nombre == "Tarta de Moras") {
                    sSizeContainer.visibility = View.GONE
                    rowDulcesPrimera?.visibility = View.GONE
                } else {
                    sSizeContainer.visibility = View.VISIBLE
                    rowDulcesPrimera?.visibility = View.VISIBLE

                    val rbChico = v.findViewById<RadioButton>(R.id.rb_chico)
                    val rbMediano = v.findViewById<RadioButton>(R.id.rb_mediano)
                    val rbGrande = v.findViewById<RadioButton>(R.id.rb_grande)

                    rbChico?.text = "Individual"
                    rbMediano?.text = "Mediano"
                    rbGrande?.text = "Familiar"
                }
            } else {
                sSizeContainer.visibility = View.GONE
                sSalados.visibility = View.VISIBLE
                sDulces.visibility = View.GONE

                when (nombre) {
                    "Ensalada César" -> {
                        rowSalsasOpcionales.visibility = View.GONE
                        cbLechuga.isChecked = true
                        cbLechuga.isEnabled = false
                        cbLechuga.text = "Lechuga (Base)"
                        cbExtraAguacate.visibility = View.VISIBLE
                    }
                    "Bagel de Guacamole" -> {
                        rowSalsasOpcionales.visibility = View.VISIBLE
                        cbLechuga.isEnabled = true
                        cbLechuga.text = "Lechuga"
                        cbExtraAguacate.visibility = View.GONE
                    }
                    else -> {
                        rowSalsasOpcionales.visibility = View.VISIBLE
                        cbLechuga.isEnabled = true
                        cbLechuga.text = "Lechuga"
                        cbExtraAguacate.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            sSizeContainer.visibility = View.GONE
            sLeche.visibility = View.GONE
            sEndulzante.visibility = View.GONE
            sSalados.visibility = View.GONE
            sDulces.visibility = View.GONE
        }
    }

    private fun cargarPreferenciasPrevias(v: View, specs: String) {
        val idsSaladosBase = listOf(
            R.id.cb_mayonesa, R.id.cb_mostaza, R.id.cb_catsup, R.id.cb_chipotle,
            R.id.cb_cebolla, R.id.cb_jitomate, R.id.cb_lechuga, R.id.cb_picante
        )

        if (specs.isEmpty()) {
            when {
                // CORREGIDO: Ahora sí lee correctamente la variable global de clase
                categoria.equals("bebida", ignoreCase = true) -> {
                    v.findViewById<RadioButton>(R.id.rb_grande)?.isChecked = true
                }
                listOf("Cheesecake", "Pastel Zanahoria").contains(nombre) -> {
                    v.findViewById<RadioButton>(R.id.rb_mediano)?.isChecked = true
                }
            }

            v.findViewById<RadioButton>(R.id.rb_entera)?.isChecked = true
            v.findViewById<RadioButton>(R.id.rb_azucar)?.isChecked = true
            idsSaladosBase.forEach { (v.findViewById<View>(it) as? CheckBox)?.isChecked = true }
            return
        }

        listOf(R.id.rb_chico, R.id.rb_mediano, R.id.rb_grande).forEach { v.findViewById<RadioButton>(it).isChecked = false }

        if (categoria.equals("bebida", ignoreCase = true) || listOf("Cheesecake", "Pastel Zanahoria").contains(nombre)) {
            when {
                specs.contains("Chico") || specs.contains("Individual") -> v.findViewById<RadioButton>(R.id.rb_chico).isChecked = true
                specs.contains("Mediano") -> v.findViewById<RadioButton>(R.id.rb_mediano).isChecked = true
                specs.contains("Grande") || specs.contains("Familiar") -> v.findViewById<RadioButton>(R.id.rb_grande).isChecked = true
            }
        }

        val rbsLeche = listOf(R.id.rb_entera to "Entera", R.id.rb_deslacto to "Deslactosada", R.id.rb_almendra to "Almendra", R.id.rb_coco to "Coco")
        rbsLeche.forEach { v.findViewById<RadioButton>(it.first).isChecked = false }
        rbsLeche.forEach { (id, nom) -> if (specs.contains(nom)) v.findViewById<RadioButton>(id).isChecked = true }

        val rbsSugar = listOf(R.id.rb_sin_azucar to "Sin Azúcar", R.id.rb_azucar to "Azúcar", R.id.rb_stevia to "Stevia", R.id.rb_miel to "Miel")
        rbsSugar.forEach { v.findViewById<RadioButton>(it.first).isChecked = false }
        when {
            specs.contains("Sin Azúcar") -> v.findViewById<RadioButton>(R.id.rb_sin_azucar).isChecked = true
            specs.contains("Stevia") -> v.findViewById<RadioButton>(R.id.rb_stevia).isChecked = true
            specs.contains("Miel") -> v.findViewById<RadioButton>(R.id.rb_miel).isChecked = true
            specs.contains("Azúcar") -> v.findViewById<RadioButton>(R.id.rb_azucar).isChecked = true
        }

        val esConTodo = specs.contains("Con todo") || specs.contains("Preparación Tradicional")
        val mappingSalados = listOf(
            R.id.cb_mayonesa to "Mayo", R.id.cb_mostaza to "Mostaza", R.id.cb_catsup to "Catsup",
            R.id.cb_chipotle to "Chipotle", R.id.cb_cebolla to "Cebolla", R.id.cb_jitomate to "Jitomate",
            R.id.cb_lechuga to "Lechuga", R.id.cb_picante to "Chiles"
        )
        mappingSalados.forEach { (id, label) ->
            v.findViewById<CheckBox>(id).isChecked = esConTodo || specs.contains(label)
        }

        val cbChoc = v.findViewById<CheckBox>(R.id.cb_extra_chocolate)
        val cbCaramelo = v.findViewById<CheckBox>(R.id.cb_extra_caramelo)
        val cbNueces = v.findViewById<CheckBox>(R.id.cb_extra_nueces)

        cbChoc?.isChecked = specs.contains("Extra Chocolate")
        cbCaramelo?.isChecked = specs.contains("Extra Caramelo")
        cbNueces?.isChecked = specs.contains("Extra Nueces")

        v.findViewById<CheckBox>(R.id.cb_extra_queso).isChecked = specs.contains("Extra Queso")
        v.findViewById<CheckBox>(R.id.cb_extra_aguacate).isChecked = specs.contains("Aguacate")
        v.findViewById<CheckBox>(R.id.cb_extra_aderezo).isChecked = specs.contains("Extra Aderezo")
    }
}