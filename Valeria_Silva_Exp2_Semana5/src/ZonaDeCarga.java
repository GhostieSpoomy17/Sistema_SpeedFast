import java.util.ArrayList;
import java.util.List;

public class ZonaDeCarga {

    private final List<Pedido> pedidosPendientes = new ArrayList<>();

    public synchronized void agregarPedido(Pedido pedido) {
        pedidosPendientes.add(pedido);
        System.out.println("Pedido #" + pedido.getIdPedido() + " agregado. Destino: "
                + pedido.getDireccionEntrega());
    }

    public synchronized Pedido retirarPedido() {
        if (pedidosPendientes.isEmpty()) {
            return null;
        }
        return pedidosPendientes.remove(0);
    }

    public synchronized int cantidadPendientes() {
        return pedidosPendientes.size();
    }
}
