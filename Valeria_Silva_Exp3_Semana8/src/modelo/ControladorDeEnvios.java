package modelo;
import java.util.ArrayList;
import java.util.List;

public class ControladorDeEnvios implements Rastreable {
    private final List<Pedido> pedidosActivos = new ArrayList<>();
    private final List<Pedido> historialEntregas = new ArrayList<>();
    private final List<Integer> idsReservados = new ArrayList<>();

    public synchronized void reservarPedido(Pedido pedido) {
        if (pedido == null) throw new IllegalArgumentException("El pedido no puede ser null.");
        synchronized (pedido) {
            if (pedido.getEstado() != EstadoPedido.PENDIENTE || idsReservados.contains(pedido.getIdPedido())) {
                System.out.println("Reserva rechazada: estado o ID duplicado para #" + pedido.getIdPedido() + ".");
                return;
            }
            idsReservados.add(pedido.getIdPedido());
            pedidosActivos.add(pedido);
            System.out.println("Pedido #" + pedido.getIdPedido() + " reservado correctamente.");
        }
    }

    public synchronized void despacharPedido(Pedido pedido) {
        if (pedido == null || !pedidosActivos.contains(pedido)) return;
        pedido.despachar();
    }

    public synchronized boolean despacharPedido(Pedido pedido, String nombreRepartidor) {
        if (pedido == null || !pedidosActivos.contains(pedido)) return false;
        synchronized (pedido) {
            if (pedido.getEstado() != EstadoPedido.PENDIENTE) return false;
            pedido.asignarRepartidor(nombreRepartidor);
            pedido.despachar();
            return pedido.getEstado() == EstadoPedido.EN_REPARTO;
        }
    }

    public synchronized boolean entregarPedido(Pedido pedido) {
        if (pedido == null || !pedidosActivos.contains(pedido)) return false;
        synchronized (pedido) {
            if (pedido.getEstado() != EstadoPedido.EN_REPARTO) return false;
            pedido.entregar();
            pedidosActivos.remove(pedido);
            historialEntregas.add(pedido);
            return true;
        }
    }

    public synchronized void cancelarPedido(Pedido pedido) {
        if (pedido == null || !pedidosActivos.contains(pedido)) return;
        synchronized (pedido) {
            pedido.cancelar();
            if (pedido.getEstado() == EstadoPedido.CANCELADO) pedidosActivos.remove(pedido);
        }
    }

    public synchronized boolean recuperarPedido(Pedido pedido) {
        return pedidosActivos.contains(pedido) && pedido.recuperarEntregaInterrumpida();
    }

    public synchronized int cantidadEntregados() { return historialEntregas.size(); }

    @Override
    public synchronized void verHistorial() {
        System.out.println("\nHistorial de entregas:");
        if (historialEntregas.isEmpty()) System.out.println("(Sin entregas registradas todavía)");
        for (Pedido pedido : historialEntregas) {
            System.out.println("- " + pedido.getClass().getSimpleName() + " #" + pedido.getIdPedido()
                    + " – entregado por " + pedido.getRepartidorAsignado());
        }
    }
}
