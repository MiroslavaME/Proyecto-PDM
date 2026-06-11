package com.example.navegacion_menus

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MhaisiCoffee.db"
        private const val DATABASE_VERSION = 2

        const val TABLA_USUARIOS = "usuarios"
        const val KEY_USER_ID = "id"
        const val KEY_USER_NAME = "nombre"
        const val KEY_USER_EMAIL = "correo"
        const val KEY_USER_BIRTH_MONTH = "mes_nacimiento"

        const val TABLA_PEDIDOS = "pedidos"
        const val KEY_PEDIDO_ID = "id_pedido"
        const val KEY_PEDIDO_USER_ID = "usuario_id"
        const val KEY_PEDIDO_RESUMEN = "resumen"
        const val KEY_PEDIDO_TOTAL = "total"
        const val KEY_PEDIDO_FECHA = "fecha"

        const val TABLA_FAVORITOS = "favoritos"
        const val KEY_FAV_ID = "id_favorito"
        const val KEY_FAV_USER_ID = "usuario_id"
        const val KEY_FAV_PRODUCTO_NOMBRE = "producto_nombre"
        const val KEY_FAV_CATEGORIA = "categoria"
        const val KEY_FAV_PRECIO = "precio"
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        db?.disableWriteAheadLogging()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val crearTablaUsuarios = ("CREATE TABLE $TABLA_USUARIOS ("
                + "$KEY_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$KEY_USER_NAME TEXT, "
                + "$KEY_USER_EMAIL TEXT UNIQUE, "
                + "$KEY_USER_BIRTH_MONTH INTEGER)")

        val crearTablaPedidos = ("CREATE TABLE $TABLA_PEDIDOS ("
                + "$KEY_PEDIDO_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$KEY_PEDIDO_USER_ID INTEGER, "
                + "$KEY_PEDIDO_RESUMEN TEXT, "
                + "$KEY_PEDIDO_TOTAL REAL, "
                + "$KEY_PEDIDO_FECHA DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY($KEY_PEDIDO_USER_ID) REFERENCES $TABLA_USUARIOS($KEY_USER_ID) ON DELETE CASCADE)")

        val crearTablaFavoritos = ("CREATE TABLE $TABLA_FAVORITOS ("
                + "$KEY_FAV_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$KEY_FAV_USER_ID INTEGER, "
                + "$KEY_FAV_PRODUCTO_NOMBRE TEXT, "
                + "$KEY_FAV_CATEGORIA TEXT, "
                + "$KEY_FAV_PRECIO REAL, "
                + "FOREIGN KEY($KEY_FAV_USER_ID) REFERENCES $TABLA_USUARIOS($KEY_USER_ID) ON DELETE CASCADE, "
                + "UNIQUE($KEY_FAV_USER_ID, $KEY_FAV_PRODUCTO_NOMBRE))")

        db?.execSQL(crearTablaUsuarios)
        db?.execSQL(crearTablaPedidos)
        db?.execSQL(crearTablaFavoritos)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLA_FAVORITOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLA_PEDIDOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIOS")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.execSQL("PRAGMA foreign_keys=ON;")
    }

    fun registrarOObtenerUsuario(nombre: String, correo: String, mesNacimiento: Int): Long {
        val db = this.writableDatabase
        val cursor = db.query(TABLA_USUARIOS, arrayOf(KEY_USER_ID), "$KEY_USER_EMAIL = ?", arrayOf(correo), null, null, null)
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_USER_ID))
            cursor.close()
            return id
        }
        cursor.close()

        val values = ContentValues().apply {
            put(KEY_USER_NAME, nombre)
            put(KEY_USER_EMAIL, correo)
            put(KEY_USER_BIRTH_MONTH, mesNacimiento)
        }
        return db.insert(TABLA_USUARIOS, null, values)
    }

    fun insertarPedido(usuarioId: Int, resumen: String, total: Double): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_PEDIDO_USER_ID, usuarioId)
            put(KEY_PEDIDO_RESUMEN, resumen)
            put(KEY_PEDIDO_TOTAL, total)
        }
        return db.insert(TABLA_PEDIDOS, null, values)
    }

    fun obtenerPedidosPorUsuario(usuarioId: Int): ArrayList<String> {
        val lista = ArrayList<String>()
        val db = this.readableDatabase

        try {
            val query = "SELECT * FROM $TABLA_PEDIDOS WHERE $KEY_PEDIDO_USER_ID = ? ORDER BY $KEY_PEDIDO_ID DESC"
            val cursor = db.rawQuery(query, arrayOf(usuarioId.toString()))

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        val resumen = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PEDIDO_RESUMEN))
                        val total = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PEDIDO_TOTAL))

                        val indexFecha = cursor.getColumnIndex(KEY_PEDIDO_FECHA)
                        val fecha = if (indexFecha != -1) cursor.getString(indexFecha) else "Reciente"

                        lista.add("[$fecha]\n$resumen\nTotal: $$total")
                    } catch (e: Exception) {
                        Log.e("MhaisiBD", "Error al procesar una fila de pedido", e)
                    }
                } while (cursor.moveToNext())
            }
            cursor?.close()
        } catch (e: Exception) {
            Log.e("MhaisiBD", "Error crítico en la consulta de pedidos", e)
        }

        return lista
    }

    fun obtenerCantidadPedidosPorUsuario(usuarioId: Int): Int {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLA_PEDIDOS WHERE $KEY_PEDIDO_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(usuarioId.toString()))

        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    fun obtenerTodosLosFavoritosPorUsuario(usuarioId: Int): ArrayList<String> {
        val lista = ArrayList<String>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLA_FAVORITOS WHERE $KEY_FAV_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(usuarioId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val nombreProducto = cursor.getString(cursor.getColumnIndexOrThrow(KEY_FAV_PRODUCTO_NOMBRE))
                lista.add(nombreProducto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun agregarFavorito(usuarioId: Int, productoNombre: String, categoria: String, precio: Double): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_FAV_USER_ID, usuarioId)
            put(KEY_FAV_PRODUCTO_NOMBRE, productoNombre)
            put(KEY_FAV_CATEGORIA, categoria)
            put(KEY_FAV_PRECIO, precio)
        }
        return db.insertWithOnConflict(TABLA_FAVORITOS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun eliminarFavorito(usuarioId: Int, productoNombre: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLA_FAVORITOS, "$KEY_FAV_USER_ID = ? AND $KEY_FAV_PRODUCTO_NOMBRE = ?", arrayOf(usuarioId.toString(), productoNombre))
    }

    fun obtenerPedidoPorId(idPedido: Long): String? {
        val db = this.readableDatabase
        var resultado: String? = null

        try {
            val query = "SELECT * FROM $TABLA_PEDIDOS WHERE $KEY_PEDIDO_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(idPedido.toString()))

            if (cursor != null && cursor.moveToFirst()) {
                val resumen = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PEDIDO_RESUMEN))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PEDIDO_TOTAL))

                val indexFecha = cursor.getColumnIndex(KEY_PEDIDO_FECHA)
                val fecha = if (indexFecha != -1) cursor.getString(indexFecha) else "Reciente"

                resultado = "[$fecha]\n$resumen\nTotal: $$total"
            }
            cursor?.close()
        } catch (e: Exception) {
            Log.e("MhaisiBD", "Error crítico al obtener el pedido por ID", e)
        }

        return resultado
    }
}