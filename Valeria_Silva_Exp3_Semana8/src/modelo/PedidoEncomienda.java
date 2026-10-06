package modelo;
public class PedidoEncomienda extends Pedido {
    public PedidoEncomienda(int idPedido, String direccionEntrega, double distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    @Override
    public int calcularTiempoEntrega() {
        return (int) Math.round(20 + (1.5 * distanciaKm));
    }

    @Override
    public synchronized void asignarRepartidor() {
        // Nombre predefinido para la demostración; no consulta ubicación ni disponibilidad.
        asignarRepartidor("Camila Soto");
    }

    @Override
    public synchronized void asignarRepartidor(String nombreRepartidor) {
        if (getEstado() != EstadoPedido.PENDIENTE) {
            System.out.println("El pedido #" + idPedido + " no admite una nueva asignación.");
            return;
        }
        setRepartidorAsignado(nombreRepartidor);
        System.out.println("Simulación de preparación: peso y embalaje.");
        System.out.println("Repartidor asignado: " + getRepartidorAsignado());
    }
}
