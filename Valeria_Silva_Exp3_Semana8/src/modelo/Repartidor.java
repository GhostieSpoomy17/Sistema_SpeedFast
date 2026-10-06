package modelo;
public class Repartidor implements Runnable {
    // Un minuto estimado equivale a 100 ms, para una demostración breve.
    public static final long MILISEGUNDOS_POR_MINUTO = 100;
    private final String nombre;
    private final ZonaDeCarga zonaDeCarga;
    private final ControladorDeEnvios controlador;

    public Repartidor(String nombre, ZonaDeCarga zonaDeCarga, ControladorDeEnvios controlador) {
        if (nombre == null || nombre.isBlank() || zonaDeCarga == null || controlador == null) {
            throw new IllegalArgumentException("El repartidor requiere nombre, zona y controlador.");
        }
        this.nombre = nombre;
        this.zonaDeCarga = zonaDeCarga;
        this.controlador = controlador;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Pedido pedido = zonaDeCarga.retirarPedido();
            if (pedido == null) break;
            if (!controlador.despacharPedido(pedido, nombre)) continue;

            System.out.println("[Repartidor - " + nombre + "] Retirando pedido #" + pedido.getIdPedido());
            try {
                Thread.sleep(pedido.calcularTiempoEntrega() * MILISEGUNDOS_POR_MINUTO);
            } catch (InterruptedException e) {
                if (controlador.recuperarPedido(pedido)) zonaDeCarga.devolverPedido(pedido);
                System.out.println("[Repartidor - " + nombre + "] Entrega interrumpida para #" + pedido.getIdPedido());
                Thread.currentThread().interrupt();
                return;
            }

            if (controlador.entregarPedido(pedido)) {
                System.out.println("[Repartidor - " + nombre + "] Entregado #" + pedido.getIdPedido());
            } else {
                System.out.println("[Repartidor - " + nombre + "] No se completó #" + pedido.getIdPedido()
                        + ": " + pedido.getEstado());
            }
        }
        System.out.println("[Repartidor - " + nombre + "] Finaliza su turno.");
    }
}
