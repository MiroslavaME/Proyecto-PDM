
## Mhaisi Coffee App - Proyecto Final
Continuación de la tarea 4 de PDDM, basada en un sistema de órdenes de forma virtual totalmente funcional

### Funcionalidades Implementadas
1. Control de usuarios y cuentas
Persistencia con SharedPreferences: La app se acuerda perfectamente si ya iniciaste sesión o si estás navegando en Modo Invitado usando un archivo local llamado MhaisiPrefs.

> Bloqueo para colados: Protegimos las pantallas importantes como Mi Orden, Favoritos o Cupones. Si entras como invitado, la app te avisa con un Toast y te manda directo a registrarte para que no puedas hacer trampa.

> Cambios al momento (onResume): El botón de la barra de arriba cambia de inmediato a tu nombre cuando te registras o vuelve a decir "Iniciar Sesión" si cierras tu cuenta.

2. Menús y botones para moverse en la app
Menú Lateral (Navigation Drawer): El panel de la izquierda que se despliega para ver las opciones de Perfil, Descubrir, Info y Soporte.

> Barra de abajo (Bottom Navigation): Diseñada con Fragments para cambiar rápido entre las pestañas de Bebidas, Comidas, Tienda y Mi Orden.

> Barra de arriba (Top App Bar): Cambia su texto dinámicamente usando SpannableString para poner tu nombre y tiene el menú de tres puntitos para abrir las opciones de reportar problemas o ir a los ajustes.

3. El Carrito de compras
Carrito que no se borra (Singleton): Usamos el objeto CarritoGlobal para que la lista de cafés y panes no se borre aunque salgas de la pantalla o te muevas por los menús.

> Cálculo automático: La pantalla de "Mi Orden" acomoda todo con su foto, precio por grupo y saca la cuenta del dinero de forma automática.

4. Cupones de descuento y diseño visual
> Cupones de un solo uso: Agregamos el módulo de Mis Cupones con el código de Bienvenida (10% de descuento) y el de Cumpleaños (bebida gratis). Si ya los usaste en un pedido, desaparecen automáticamente de la lista para que no se puedan repetir.

> Sección de Novedades: Diseñada con MaterialCardView de esquinas redondeadas y ShapeableImageView para cargar los anuncios grandes de los nuevos cafés de Chiapas y los talleres de la barra.

> Modo Oscuro sin fallas: Activamos el soporte para el tema nocturno. Usando atributos dinámicos como ?attr/colorOnPrimary.

5. Avisos y revisión de errores
> Mensajes interactivos (Toasts): Te avisan con pequeñas alertas en la pantalla cada vez que agregas algo, aplicas un cupón o cancelas un pedido.

> Rastreo en consola (Logcat): Dejamos un sistema de monitoreo interno con la etiqueta Tarea3_Mhaisi para revisar que los datos del carrito y el cierre de sesión funcionen bien desde la consola de Android Studio.

### Herramientas y cosas técnicas que usamos
Lenguaje: Kotlin 1.9+

Estructura: Activities para las pantallas completas y Fragments para el menú inferior.

Guardado de datos: SharedPreferences para las preferencias y estados del usuario.

Desarrollado por: Miroslava Mora Espinosa y Gomez Aguilar Jesus
