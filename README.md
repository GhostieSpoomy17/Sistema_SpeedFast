# Semana 5 — Sumativa 2

Proyecto SpeedFast

# Estructura
- **Abstracción:** `Pedido` es clase abstracta (idPedido, direccionEntrega, distanciaKm, repartidorAsignado, estado), con `mostrarResumen()` implementado, `toString()` sobrescrito y `calcularTiempoEntrega()` abstracto.
- **Polimorfismo:** cada subclase (`PedidoComida`, `PedidoEncomienda`, `PedidoExpress`) sobrescribe `asignarRepartidor()` (asignación automática) y sobrecarga `asignarRepartidor(String nombre)` (asignación manual), además de implementar `calcularTiempoEntrega()` con la fórmula propia de cada tipo de pedido.
- **Interfaces:**
  - `Despachable` (`despachar()`) y `Cancelable` (`cancelar()`) se implementan directamente en `Pedido`, porque despachar/cancelar es una responsabilidad de cada pedido individual.
  - `Rastreable` (`verHistorial()`) se implementa en la clase adicional `ControladorDeEnvios`, porque el historial es una responsabilidad de varios pedidos, no de un pedido individual.
- **ControladorDeEnvios:** concentra las interacciones funcionales (reservar, despachar, cancelar, ver historial) y mantiene dos `ArrayList<Pedido>`: pedidos activos e historial de entregas. Esto desacopla la lógica de negocio de las clases de pedido, favoreciendo mantenibilidad y bajo acoplamiento.
- **Estado del pedido:** enum `EstadoPedido` (`PENDIENTE`, `EN_REPARTO`, `ENTREGADO`, `CANCELADO`), actualizado mediante `setEstado(String nuevoEstado)`, que valida que el texto recibido corresponda a un valor del enum antes de aplicarlo.
- **Concurrencia:** `Repartidor implements Runnable`. Varios repartidores se ejecutan en paralelo mediante `ExecutorService`, simulando el tiempo de entrega con `Thread.sleep()`.
- **Sincronización:** a diferencia de la semana anterior, los repartidores ya no reciben una lista fija de pedidos: compiten por retirarlos desde `ZonaDeCarga`, un recurso compartido cuyos métodos `agregarPedido()` y `retirarPedido()` son `synchronized`, evitando condiciones de carrera. Se validó ejecutando el programa 15 veces seguidas, retirando siempre los mismos 6 pedidos sin duplicados ni pérdidas.

## Diagrama de clases
```
        <<interface>>         <<interface>>          <<interface>>
        Despachable            Cancelable              Rastreable
        + despachar()          + cancelar()            + verHistorial()
             ▲                      ▲                        ▲
             └──────────┬───────────┘                        │
                         │                                    │
                  Pedido (abstract)                  ControladorDeEnvios
                  # idPedido, direccionEntrega,       - pedidosActivos: ArrayList<Pedido>
                    distanciaKm, repartidorAsignado,  - historialEntregas: ArrayList<Pedido>
                    estado: EstadoPedido              + reservarPedido()
                  + mostrarResumen()                  + despacharPedido()
                  + toString()                        + cancelarPedido()
                  + setEstado(String)                 + verHistorial()
                  + calcularTiempoEntrega() {abstract}
                  + asignarRepartidor() {abstract}
                  + asignarRepartidor(String){abstract}
                         ▲
        ┌────────────────┼────────────────┐
        │                │                │
  PedidoComida     PedidoEncomienda   PedidoExpress


      <<enum>>                     ZonaDeCarga                     Repartidor
    EstadoPedido               (recurso compartido)              implements Runnable
    PENDIENTE                 - pedidosPendientes: List<Pedido>   - nombre: String
    EN_REPARTO                + agregarPedido() {synchronized}    - zonaDeCarga: ZonaDeCarga
    ENTREGADO                 + retirarPedido() {synchronized}    + run()
    CANCELADO                            ▲                              │
         ▲                              │                              │
         └───── usado como estado ──────┘                              │
                de cada Pedido           └──── varios Repartidor compiten
                                               por retirarPedido(), lanzados
                                               en paralelo con ExecutorService
```

# Link del repositorio
https://github.com/GhostieSpoomy17/Sistema_SpeedFast.git
