package com.example.navegacion_menus

data class Producto(
    val nombre: String,
    var precio: Double,
    val categoria: String,
    var cantidad: Int = 1,
    var especificaciones: String = "",
    var idInstancia: Int = 0
)

object CarritoGlobal {
    private val productosEnCarrito = mutableListOf<Producto>()
    val listaFavoritos = mutableSetOf<String>()

    var latte = 0; var espresso = 0; var chocolate = 0; var capuccino = 0
    var teFrio = 0; var limonada = 0; var smoothie = 0; var matcha = 0
    var baguette = 0; var cesar = 0; var pavo = 0; var bagel = 0
    var dona = 0; var tarta = 0; var zanahoria = 0; var cheesecake = 0
    var oaxaca = 0; var michoacan = 0; var rosa = 0; var aniversario = 0
    var tote = 0; var galletas = 0; var mix = 0

    fun obtenerPrecioBase(nombre: String): Double {
        return when(nombre) {
            "Latte Clásico" -> 65.0
            "Espresso" -> 45.0
            "Chocolate Caliente" -> 50.0
            "Capuccino" -> 70.0
            "Té Frío" -> 55.0
            "Limonada de Fresa" -> 40.0
            "Smoothie Asha" -> 80.0
            "Matcha" -> 75.0
            "Baguette Pizza" -> 95.0
            "Ensalada César" -> 110.0
            "Sandwich Pavo" -> 85.0
            "Bagel Guacamole" -> 75.0
            "Dona Caramelo" -> 35.0
            "Tarta de Moras" -> 60.0
            "Pastel Zanahoria" -> 70.0
            "Cheesecake" -> 65.0
            "Grano Oaxaca" -> 280.0
            "Molido Michoacán" -> 265.0
            "Termo Rosa" -> 450.0
            "Termo Aniversario" -> 520.0
            "Tote Bag Mhaisi" -> 190.0
            "Galletas Avena" -> 38.0
            "Mix Energético" -> 42.0
            else -> 0.0
        }
    }

    fun calcularTotalCarrito(): Double {
        var total = 0.0
        val productos = obtenerProductosSeleccionados()
        for (p in productos) {
            total += p.precio
        }
        return total
    }

    fun obtenerResumenTexto(): String {
        val productos = obtenerProductosSeleccionados()
        if (productos.isEmpty()) return "Pedido Vacío"

        return productos.groupBy { it.nombre }
            .map { entry -> "${entry.value.size}x ${entry.key}" }
            .joinToString(", ")
    }

    fun obtenerProductosSeleccionados(): List<Producto> {
        val nuevaLista = mutableListOf<Producto>()

        fun desglosar(nombre: String, precio: Double, cat: String, cant: Int) {
            for (i in 1..cant) {
                val existente = productosEnCarrito.find { it.nombre == nombre && it.idInstancia == i }
                nuevaLista.add(existente ?: Producto(nombre, precio, cat, 1, "", i))
            }
        }

        if (latte > 0) desglosar("Latte Clásico", obtenerPrecioBase("Latte Clásico"), "bebida", latte)
        if (espresso > 0) desglosar("Espresso", obtenerPrecioBase("Espresso"), "bebida", espresso)
        if (chocolate > 0) desglosar("Chocolate Caliente", obtenerPrecioBase("Chocolate Caliente"), "bebida", chocolate)
        if (capuccino > 0) desglosar("Capuccino", obtenerPrecioBase("Capuccino"), "bebida", capuccino)
        if (teFrio > 0) desglosar("Té Frío", obtenerPrecioBase("Té Frío"), "bebida", teFrio)
        if (limonada > 0) desglosar("Limonada de Fresa", obtenerPrecioBase("Limonada de Fresa"), "bebida", limonada)
        if (smoothie > 0) desglosar("Smoothie Asha", obtenerPrecioBase("Smoothie Asha"), "bebida", smoothie)
        if (matcha > 0) desglosar("Matcha", obtenerPrecioBase("Matcha"), "bebida", matcha)

        if (baguette > 0) desglosar("Baguette Pizza", obtenerPrecioBase("Baguette Pizza"), "comida", baguette)
        if (cesar > 0) desglosar("Ensalada César", obtenerPrecioBase("Ensalada César"), "comida", cesar)
        if (pavo > 0) desglosar("Sandwich Pavo", obtenerPrecioBase("Sandwich Pavo"), "comida", pavo)
        if (bagel > 0) desglosar("Bagel Guacamole", obtenerPrecioBase("Bagel Guacamole"), "comida", bagel)
        if (dona > 0) desglosar("Dona Caramelo", obtenerPrecioBase("Dona Caramelo"), "comida", dona)
        if (tarta > 0) desglosar("Tarta de Moras", obtenerPrecioBase("Tarta de Moras"), "comida", tarta)
        if (zanahoria > 0) desglosar("Pastel Zanahoria", obtenerPrecioBase("Pastel Zanahoria"), "comida", zanahoria)
        if (cheesecake > 0) desglosar("Cheesecake", obtenerPrecioBase("Cheesecake"), "comida", cheesecake)

        if (oaxaca > 0) desglosar("Grano Oaxaca", obtenerPrecioBase("Grano Oaxaca"), "extra", oaxaca)
        if (michoacan > 0) desglosar("Molido Michoacán", obtenerPrecioBase("Molido Michoacán"), "extra", michoacan)
        if (rosa > 0) desglosar("Termo Rosa", obtenerPrecioBase("Termo Rosa"), "extra", rosa)
        if (aniversario > 0) desglosar("Termo Aniversario", obtenerPrecioBase("Termo Aniversario"), "extra", aniversario)
        if (tote > 0) desglosar("Tote Bag Mhaisi", obtenerPrecioBase("Tote Bag Mhaisi"), "extra", tote)
        if (galletas > 0) desglosar("Galletas Avena", obtenerPrecioBase("Galletas Avena"), "extra", galletas)
        if (mix > 0) desglosar("Mix Energético", obtenerPrecioBase("Mix Energético"), "extra", mix)

        productosEnCarrito.clear()
        productosEnCarrito.addAll(nuevaLista)
        return productosEnCarrito
    }

    fun limpiarCarrito() {
        latte = 0; espresso = 0; chocolate = 0; capuccino = 0
        teFrio = 0; limonada = 0; smoothie = 0; matcha = 0
        baguette = 0; cesar = 0; pavo = 0; bagel = 0
        dona = 0; tarta = 0; zanahoria = 0; cheesecake = 0
        oaxaca = 0; michoacan = 0; rosa = 0; aniversario = 0
        tote = 0; galletas = 0; mix = 0
        productosEnCarrito.clear()
    }
}