package com.example.navegacion_menus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class RegistroActivity : AppCompatActivity() {

    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilCumpleanios: TextInputLayout

    private lateinit var tietNombre: TextInputEditText
    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tietCumpleanios: TextInputEditText

    private var fechaSeleccionadaMs: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        tilNombre = findViewById(R.id.til_registro_nombre)
        tilCorreo = findViewById(R.id.til_registro_correo)
        tilCumpleanios = findViewById(R.id.til_registro_cumpleanios)

        tietNombre = findViewById(R.id.tiet_registro_nombre)
        tietCorreo = findViewById(R.id.tiet_registro_correo)
        tietCumpleanios = findViewById(R.id.tiet_registro_cumpleanios)

        val btnRegistrar = findViewById<MaterialButton>(R.id.btn_registrar_usuario)
        val btnCancelar = findViewById<MaterialButton>(R.id.btn_cancelar_registro)

        tietCumpleanios.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona tu cumpleaños")
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                fechaSeleccionadaMs = selection

                val timeZoneUtc = TimeZone.getDefault()
                val offset = timeZoneUtc.getOffset(Date().time) * -1
                val format = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))
                val dateStr = format.format(Date(selection + offset))

                tietCumpleanios.setText(dateStr)
                tilCumpleanios.error = null
            }

            datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        }

        btnRegistrar.setOnClickListener {
            if (validarFormulario()) {
                val nombreCompleto = tietNombre.text.toString().trim()
                val correo = tietCorreo.text.toString().trim()
                val cumpleanios = tietCumpleanios.text.toString().trim()

                val primerNombre = nombreCompleto.split(" ").firstOrNull() ?: nombreCompleto

                val calNacimiento = Calendar.getInstance()
                calNacimiento.timeInMillis = fechaSeleccionadaMs
                val mesNacimiento = calNacimiento.get(Calendar.MONTH) + 1
                val diaNacimiento = calNacimiento.get(Calendar.DAY_OF_MONTH)

                val dbHelper = DataBaseHelper(this)
                val idUsuarioGenerado = dbHelper.registrarOObtenerUsuario(nombreCompleto, correo, mesNacimiento)

                if (idUsuarioGenerado != -1L) {
                    val prefs = getSharedPreferences("MhaisiPrefs", Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putBoolean("is_logged_in", true)
                        putInt("user_id", idUsuarioGenerado.toInt())
                        putString("user_name", nombreCompleto)
                        putString("user_email", correo)
                        putString("user_birthday", cumpleanios)
                        putInt("user_birth_month", mesNacimiento)
                        putInt("user_birth_day", diaNacimiento)
                        putBoolean("cupon_mhaisibienvenida_usado", false)
                        putBoolean("cupon_mhaisicumple_usado", false)
                        apply()
                    }

                    Toast.makeText(this, "¡Cuenta creada exitosamente para $primerNombre!", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar el usuario en la base de datos", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnCancelar?.setOnClickListener {
            finish()
        }
    }

    private fun validarFormulario(): Boolean {
        var esValido = true

        val nombre = tietNombre.text.toString().trim()
        val correo = tietCorreo.text.toString().trim()
        val cumpleanios = tietCumpleanios.text.toString().trim()

        val palabrasNombre = nombre.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        val patronSoloLetras = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$".toRegex()

        if (nombre.isEmpty()) {
            tilNombre.error = "El nombre completo es obligatorio"
            esValido = false
        } else if (!nombre.matches(patronSoloLetras)) {
            tilNombre.error = "El nombre no puede contener números ni caracteres especiales"
            esValido = false
        } else if (palabrasNombre.size < 3) {
            tilNombre.error = "Por favor introduce tu nombre y ambos apellidos"
            esValido = false
        } else {
            tilNombre.error = null
        }

        if (correo.isEmpty()) {
            tilCorreo.error = "El correo electrónico es requerido"
            esValido = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.error = "Escribe una estructura de correo válida"
            esValido = false
        } else {
            tilCorreo.error = null
        }

        if (cumpleanios.isEmpty()) {
            tilCumpleanios.error = "Por favor selecciona tu fecha de nacimiento"
            esValido = false
        } else {
            val fechaHoy = Calendar.getInstance()
            val fechaLimite = Calendar.getInstance()
            fechaLimite.add(Calendar.YEAR, -12)

            val fechaNacimiento = Calendar.getInstance()
            fechaNacimiento.timeInMillis = fechaSeleccionadaMs

            if (fechaNacimiento.after(fechaHoy)) {
                tilCumpleanios.error = "¡No puedes nacer en el futuro!"
                esValido = false
            } else if (fechaNacimiento.after(fechaLimite)) {
                tilCumpleanios.error = "Debes tener al menos 12 años para usar la app"
                esValido = false
            } else {
                tilCumpleanios.error = null
            }
        }

        return esValido
    }
}