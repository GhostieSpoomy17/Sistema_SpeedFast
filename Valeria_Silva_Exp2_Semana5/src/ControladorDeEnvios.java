import java.util.ArrayList;
import java.util.List;

public class ControladorDeEnvios implements Rastreable {

    private final List<Pedido> pedidosActivos = new ArrayList<>();
    private final List<Pedido> historialEntregas = new ArrayList<>();

    public synchronized void reservarPedido(Pedido pedido) {
        pedidosActivos.add(pedido);
        System.out.println("Pedido " + pedido.getClass().getSimpleName() + " #" + pedido.getIdPedido()
                + " reservado correctamente.");
    }

    public synchronized void despacharPedido(Pedido pedido) {
        pedido.despachar();
        if (pedidosActivos.contains(pedido)) {
            pedidosActivos.remove(pedido);
            historialEntregas.add(pedido);
        }
    }

    public synchronized void cancelarPedido(Pedido pedido) {
        pedido.cancelar();
        pedidosActivos.remove(pedido);
    }

    @Override
    public synchronized void verHistorial() {
        System.out.println("\nHistorial de entregas:");
        if (historialEntregas.isEmpty()) {
            System.out.println("(Sin entregas registradas todavía)");
            return;
        }
        for (Pedido pedido : historialEntregas) {
            System.out.println("- " + pedido.getClass().getSimpleName() + " #" + pedido.getIdPedido()
                    + " – entregado por " + pedido.getRepartidorAsignado());
        }
    }
}
