package modelo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZonaDeCarga {
    private final List<Pedido> pedidosPendientes = new ArrayList<>();
    private final Map<Integer, Pedido> pedidosRegistrados = new HashMap<>();

    public synchronized void agregarPedido(Pedido pedido) {
        if (pedido == null) throw new IllegalArgumentException("El pedido no puede ser null.");
        synchronized (pedido) {
            if (pedido.getEstado() != EstadoPedido.PENDIENTE
                    || pedidosRegistrados.containsKey(pedido.getIdPedido())) {
                System.out.println("Pedido #" + pedido.getIdPedido() + " omitido: estado o ID duplicado.");
                return;
            }
            pedidosRegistrados.put(pedido.getIdPedido(), pedido);
            pedidosPendientes.add(pedido);
            System.out.println("Pedido #" + pedido.getIdPedido() + " agregado. Destino: " + pedido.getDireccionEntrega());
        }
    }

    public synchronized Pedido retirarPedido() {
        while (!pedidosPendientes.isEmpty()) {
            Pedido pedido = pedidosPendientes.remove(0);
            if (pedido.getEstado() == EstadoPedido.PENDIENTE) return pedido;
        }
        return null;
    }

    public synchronized void devolverPedido(Pedido pedido) {
        if (pedido == null) return;
        synchronized (pedido) {
            if (pedidosRegistrados.get(pedido.getIdPedido()) == pedido
                    && pedido.getEstado() == EstadoPedido.PENDIENTE && !pedidosPendientes.contains(pedido)) {
                pedidosPendientes.add(pedido);
                System.out.println("Pedido #" + pedido.getIdPedido() + " devuelto a la zona de carga.");
            }
        }
    }

    public synchronized int cantidadPendientes() {
        int cantidad = 0;
        for (Pedido pedido : pedidosPendientes) {
            if (pedido.getEstado() == EstadoPedido.PENDIENTE) cantidad++;
        }
        return cantidad;
    }
}
