# Semana 8 — Sumativa 3

Proyecto SpeedFast

# Estructura
- **Abstracción:** `Pedido` es clase abstracta (idPedido, direccionEntrega, distanciaKm, repartidorAsignado, estado), con `mostrarResumen()` implementado, `toString()` sobrescrito y `calcularTiempoEntrega()` abstracto.
- **Polimorfismo:** las subclases (`PedidoComida`, `PedidoEncomienda`, `PedidoExpress`) implementan las dos variantes de `asignarRepartidor()` y calculan tiempos de entrega según el tipo de pedido. La clase auxiliar `Pedidos` reconstruye estas subclases al consultar la base de datos.
- **Interfaces:**
  - `Despachable` (`despachar()`) y `Cancelable` (`cancelar()`) se implementan en `Pedido`, porque despachar y cancelar corresponden a cada pedido individual.
  - `Rastreable` (`verHistorial()`) se implementa en `ControladorDeEnvios`, que administra el historial de varios pedidos.
- **ControladorDeEnvios:** conserva las operaciones de reserva, despacho, cancelación e historial para la simulación concurrente de las semanas anteriores.
- **Estado del pedido:** enum `EstadoPedido` (`PENDIENTE`, `EN_REPARTO`, `ENTREGADO`, `CANCELADO`). La simulación anterior conserva los cuatro estados; la gestión con MySQL utiliza los tres estados solicitados en la pauta de la semana 8: `PENDIENTE`, `EN_REPARTO` y `ENTREGADO`.
- **Concurrencia:** `Repartidor implements Runnable`. La simulación de la semana 5 mantiene tres trabajadores mediante `ExecutorService` y utiliza `Thread.sleep()` para representar los tiempos de entrega.
- **Sincronización:** los trabajadores retiran pedidos de `ZonaDeCarga`, un recurso compartido con métodos sincronizados, para evitar retiradas duplicadas. Las tareas y su finalización se comprueban antes de mostrar el resumen.
- **Interfaz gráfica:** `VentanaPrincipal` hereda de `JFrame` y ofrece pestañas para repartidores, pedidos y entregas. Incluye navegación hacia `VentanaRegistroPedido` y `VentanaListaPedidos`, tablas `JTable` con `DefaultTableModel`, formularios y botones para las operaciones CRUD.
- **Persistencia:** `ConexionDB` abre conexiones a MySQL mediante JDBC. `RepartidorDAO`, `PedidoDAO` y `EntregaDAO` implementan `create()`, `readAll()`, `update()` y `delete()`, con `PreparedStatement`, `ResultSet` y cierre de recursos mediante `try-with-resources`.
- **Entidades relacionadas:** `RepartidorRegistro` representa los repartidores almacenados en MySQL; `Repartidor` conserva su responsabilidad como trabajador de la simulación. `Entrega` almacena los ID de pedido y repartidor, la fecha y la hora. Los `JComboBox` muestran textos legibles y conservan internamente los objetos e identificadores.
- **Validaciones y errores:** se comprueban campos obligatorios, longitudes, fecha, hora y selección de registros. Los errores de conexión y las restricciones de claves foráneas se informan mediante `JOptionPane`. No se permite eliminar un pedido o repartidor que tenga entregas asociadas.
- **Entrega desde la interfaz:** asignar un repartidor a un pedido `PENDIENTE` inicia la simulación, espera 1,5 segundos y registra la entrega junto con el estado `ENTREGADO` en una transacción. Si falla, se revierte la transacción y se intenta recuperar el pedido a `PENDIENTE`.
- **Trabajo en segundo plano:** las consultas y la simulación gráfica se ejecutan con `SwingWorker` para mantener disponible la interfaz. Después de una operación se actualizan las tablas y los combos de la ventana principal.
- **Organización:** el proyecto se divide en los paquetes `modelo`, `dao`, `vista` y `main`; las carpetas `sql` y `lib` contienen el esquema de la base de datos y MySQL Connector/J.

## Diagrama de clases
```text
     <<interface>>       <<interface>>         <<interface>>
      Despachable         Cancelable             Rastreable
      + despachar()       + cancelar()           + verHistorial()
            ▲                  ▲                       ▲
            └────────┬─────────┘                       │
                     │                        ControladorDeEnvios
              Pedido (abstract)               + reservarPedido()
              # idPedido                      + despacharPedido()
              # direccionEntrega              + cancelarPedido()
              # distanciaKm                   + verHistorial()
              # repartidorAsignado
              # estado: EstadoPedido
              + calcularTiempoEntrega()
              + asignarRepartidor()
              + asignarRepartidor(String)
                     ▲
       ┌─────────────┼─────────────┐
       │             │             │
 PedidoComida  PedidoEncomienda  PedidoExpress

   <<enum>>              ZonaDeCarga                Repartidor
 EstadoPedido          (recurso compartido)        implements Runnable
 PENDIENTE             + agregarPedido()           + run()
 EN_REPARTO            + retirarPedido()                 │
 ENTREGADO             {synchronized}                   │
 CANCELADO                     ▲                       │
                               └──── retira pedidos ───┘

                   main.Main
                       │
                       ▼
               VentanaPrincipal (JFrame)
                       │
       ┌───────────────┼─────────────────┐
       ▼               ▼                 ▼
 PanelRepartidores  PanelPedidos     PanelEntregas
       │               │                 │
       ▼               ▼                 ▼
 RepartidorDAO     PedidoDAO         EntregaDAO
       │               │                 │
       └───────────────┼─────────────────┘
                       ▼
                  ConexionDB
                       │ JDBC
                       ▼
                  speedfast_db
            repartidores / pedidos / entregas

 VentanaRegistroPedido ──► PanelPedidos
 VentanaListaPedidos   ──► PedidoDAO ──► JTable
```

# Ejecución
1. Utilizar Java 21 y abrir el proyecto en IntelliJ IDEA.
2. Tener MySQL instalado y su servicio iniciado. En el equipo de desarrollo se utiliza `MySQL97`, en el puerto `3306`; otro equipo puede usar su propia instalación.
3. Ejecutar `sql/speedfast_db.sql` en MySQL Workbench para crear `speedfast_db` y las tablas `repartidores`, `pedidos` y `entregas`.
4. Comprobar que `lib/mysql-connector-j-26.7.0.jar` esté agregado como biblioteca del módulo. El proyecto incluye su configuración de IntelliJ.
5. Ejecutar `main.Main`. La clase `Main` del paquete predeterminado también redirige al inicio gráfico para conservar compatibilidad con la configuración anterior.
6. Ingresar servidor, puerto, usuario y contraseña de la instalación local de MySQL. Cada persona utiliza sus propias credenciales; la contraseña no se guarda en archivos ni se incluye en el repositorio.
7. Registrar repartidores y pedidos antes de crear entregas. Seleccionar una fila permite cargar sus datos para actualizarla o eliminarla. `Nuevo / Limpiar` prepara el formulario para un registro nuevo; `Refrescar` consulta nuevamente la base.

La simulación concurrente anterior se ejecuta con `main.SimulacionMain`, o mediante el argumento `--simulacion` al ejecutar `Main`.

# Consideraciones
- **ID:** MySQL asigna los identificadores automáticamente, conforme al esquema de la semana 8.
- **Fecha y hora:** los formularios utilizan `aaaa-mm-dd` y `hh:mm:ss`; también se admite `hh:mm`.
- **Distancia:** el esquema solicitado no almacena kilómetros. Al reconstruir pedidos desde JDBC se usa `0` como valor de distancia; el CRUD no lo utiliza para calcular tiempos. La simulación anterior conserva las distancias de sus pedidos de ejemplo.
- **Gestión manual de entregas:** registrar, editar o eliminar una entrega manualmente no recalcula el estado del pedido. La opción de simulación sí actualiza su estado hasta `ENTREGADO`.
- **Pauta:** se implementa `RepartidorDAO` conforme a las entidades y tablas requeridas. La mención a `ClienteDAO` en un paso de la guía no corresponde al esquema presentado.
- **Verificación:** el proyecto compila con Java 21 y pasó 19 comprobaciones JDBC de CRUD, relaciones, estados, reversión de transacciones y limpieza de datos de prueba. La simulación concurrente terminó con seis pedidos entregados, uno cancelado y ninguno pendiente. También se comprobó visualmente la interfaz y la validación de dirección obligatoria.
- **Entrega:** el ZIP incluye código, configuración del proyecto, conector, script SQL y README. Los registros y las contraseñas de la instalación local no se incluyen. El repositorio nuevo en GitHub y su enlace deben completarse para la entrega.

# Link del repositorio
