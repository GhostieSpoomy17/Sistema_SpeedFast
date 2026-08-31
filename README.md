# Semana 3 — Sumativa 1

Proyecto SpeedFast

# Estructura
- **Abstracción:** `Pedido` es clase abstracta (idPedido, direccionEntrega, distanciaKm, repartidorAsignado, estado), con `mostrarResumen()` implementado y `calcularTiempoEntrega()` abstracto.
- **Polimorfismo:** cada subclase (`PedidoComida`, `PedidoEncomienda`, `PedidoExpress`) sobrescribe `asignarRepartidor()` (asignación automática) y sobrecarga `asignarRepartidor(String nombre)` (asignación manual), además de implementar `calcularTiempoEntrega()` con la fórmula de la semana 2.
- **Interfaces:**
  - `Despachable` (`despachar()`) y `Cancelable` (`cancelar()`) se implementan directamente en `Pedido`, porque despachar/cancelar es una responsabilidad de cada pedido individual.
  - `Rastreable` (`verHistorial()`) se implementa en la clase adicional `ControladorDeEnvios`, porque el historial es una responsabilidad de varios pedidos, no de un pedido individual.
- **ControladorDeEnvios:** concentra las interacciones funcionales (reservar, despachar, cancelar, ver historial) y mantiene dos `ArrayList<Pedido>`: pedidos activos e historial de entregas. Esto desacopla la lógica de negocio de las clases de pedido, favoreciendo mantenibilidad y bajo acoplamiento.

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
                    estado                            + reservarPedido()
                  + mostrarResumen()                  + despacharPedido()
                  + calcularTiempoEntrega() {abstract} + cancelarPedido()
                  + asignarRepartidor() {abstract}     + verHistorial()
                  + asignarRepartidor(String){abstract}
                         ▲
        ┌────────────────┼────────────────┐
        │                │                │
  PedidoComida     PedidoEncomienda   PedidoExpress
```
# Link del repositorio
