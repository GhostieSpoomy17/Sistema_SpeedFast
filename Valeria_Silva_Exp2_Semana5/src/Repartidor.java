import java.util.Random;

public class Repartidor implements Runnable {

    private final String nombre;
    private final ZonaDeCarga zonaDeCarga;
    private final ControladorDeEnvios controlador;
    private final Random random = new Random();

    public Repartidor(String nombre, ZonaDeCarga zonaDeCarga, ControladorDeEnvios controlador) {
        this.nombre = nombre;
        this.zonaDeCarga = zonaDeCarga;
        this.controlador = controlador;
    }

    @Override
    public void run() {
        while (true) {
            Pedido pedido = zonaDeCarga.retirarPedido();
            if (pedido == null) {
                break;
            }

            pedido.setRepartidorAsignado(nombre);

            System.out.println("[Repartidor - " + nombre + "] Retirando pedido #" + pedido.getIdPedido() + "...");
            pedido.setEstado("EN_REPARTO");
            System.out.println("[Repartidor - " + nombre + "] Estado: " + pedido.getEstado());

            try {
                int pausaMs = 1000 + random.nextInt(2000);
                Thread.sleep(pausaMs);
            } catch (InterruptedException e) {
                System.out.println("[Repartidor - " + nombre + "] Entrega interrumpida para el pedido #"
                        + pedido.getIdPedido() + ".");
                Thread.currentThread().interrupt();
                return;
            }

            System.out.println("[Repartidor - " + nombre + "] Entregando pedido #" + pedido.getIdPedido() + "...");
            controlador.despacharPedido(pedido);
            System.out.println("[Repartidor - " + nombre + "] Estado: " + pedido.getEstado());
            System.out.println();
        }
        System.out.println("[Repartidor - " + nombre + "] No quedan más pedidos. Finaliza su turno.");
    }
}
