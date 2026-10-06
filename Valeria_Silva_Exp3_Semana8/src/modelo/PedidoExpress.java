package modelo;
public class PedidoExpress extends Pedido {
    public PedidoExpress(int idPedido, String direccionEntrega, double distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    @Override
    public int calcularTiempoEntrega() {
        return distanciaKm > 5 ? 15 : 10;
    }

    @Override
    public synchronized void asignarRepartidor() {
        // Nombre predefinido para la demostración; no consulta ubicación ni disponibilidad.
        asignarRepartidor("Juanito Pérez");
    }

    @Override
    public synchronized void asignarRepartidor(String nombreRepartidor) {
        if (getEstado() != EstadoPedido.PENDIENTE) {
            System.out.println("El pedido #" + idPedido + " no admite una nueva asignación.");
            return;
        }
        setRepartidorAsignado(nombreRepartidor);
        System.out.println("Simulación de preparación: entrega prioritaria.");
        System.out.println("Repartidor asignado: " + getRepartidorAsignado());
    }
}
