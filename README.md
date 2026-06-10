Mhaisi Coffee App - Tarea 03

Continuacion de la tarea 2 de PDDM, basada en un sistema de ordenes de forma virtual.
## Funcionalidades Implementadas

### 1. Navegación Avanzada
* **Navigation Drawer (Menú Lateral):** Organizado por secciones (Perfil, Descubrir, Info, Soporte) con iconos personalizados y manejo de eventos.
* **Bottom Navigation:** Permite el cambio fluido entre categorías (Bebidas, Comidas, Tienda y Mi Orden) mediante el uso de **Fragments**.
* **Top App Bar:** Incluye un menú de desbordamiento (overflow) para reportar problemas y configuración.

### 2. Gestión de Pedidos (Carrito)
* **Persistencia de Datos:** Uso de un objeto `Singleton` (`CarritoGlobal`) para mantener las cantidades de productos seleccionados entre diferentes pantallas.
* **Interfaz Dinámica:** La pantalla de "Mi Orden" lista los productos con su imagen, precio y subtotal, calculando el total automáticamente.

### 3. Feedback y Trazabilidad
* **Toasts:** Avisos visuales para el usuario en cada acción importante.
* **Logcat (Logs):** Se implementó un sistema de registro bajo la etiqueta `Tarea3_Mhaisi` para rastrear la navegación y eventos en la consola de Android Studio.

---
**Desarrollado por:** Miroslava Mora Espinosa y Gomez Aguilar Jesus
