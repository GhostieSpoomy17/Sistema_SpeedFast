package modelo;
public class PedidoComida extends Pedido {
    public PedidoComida(int idPedido, String direccionEntrega, double distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    @Override
    public int calcularTiempoEntrega() {
        return (int) Math.round(15 + (2 * distanciaKm));
    }

    @Override
    public synchronized void asignarRepartidor() {
        // Nombre predefinido para la demostración; no consulta ubicación ni disponibilidad.
        asignarRepartidor("Luis Díaz");
    }

    @Override
    public synchronized void asignarRepartidor(String nombreRepartidor) {
        if (getEstado() != EstadoPedido.PENDIENTE) {
            System.out.println("El pedido #" + idPedido + " no admite una nueva asignación.");
            return;
        }
        setRepartidorAsignado(nombreRepartidor);
        System.out.println("Simulación de preparación: mochila térmica.");
        System.out.println("Repartidor asignado: " + getRepartidorAsignado());
    }
}
