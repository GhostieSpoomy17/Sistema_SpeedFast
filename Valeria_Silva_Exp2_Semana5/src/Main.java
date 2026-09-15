import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        System.out.println("-------------------------");
        System.out.println("Polimorfismo por sobrecarga y sobrescritura");
        System.out.println("-------------------------\n");

        Pedido demoComida = new PedidoComida(901, "Av. Italia 1439", 3);
        Pedido demoExpress = new PedidoExpress(902, "Av. Apoquindo 6410", 6);

        demoComida.asignarRepartidor();
        System.out.println();
        demoExpress.asignarRepartidor("Juanito Pérez");

        System.out.println("\n-------------------------");
        System.out.println("Clase abstracta y cálculo de tiempos");
        System.out.println("-------------------------\n");

        demoComida.mostrarResumen();
        System.out.println("Tiempo estimado: " + demoComida.calcularTiempoEntrega() + " minutos\n");
        demoExpress.mostrarResumen();
        System.out.println("Tiempo estimado: " + demoExpress.calcularTiempoEntrega() + " minutos");

        System.out.println("\n-------------------------");
        System.out.println("Interfaces");
        System.out.println("-------------------------\n");

        ControladorDeEnvios controlador = new ControladorDeEnvios();

        Pedido p101 = new PedidoComida(101, "Av. Italia 1439", 3);
        Pedido p102 = new PedidoEncomienda(102, "Av. Independencia 2935", 7);
        Pedido p103 = new PedidoExpress(103, "Av. Apoquindo 6410", 6);
        Pedido p104 = new PedidoComida(104, "Av. Italia 1650", 5);
        Pedido p105 = new PedidoEncomienda(105, "Av. Independencia 3050", 4);
        Pedido p106 = new PedidoExpress(106, "Av. Apoquindo 6800", 3);
        Pedido p107 = new PedidoComida(107, "Av. Providencia 1200", 2);

        List<Pedido> todosLosPedidos = Arrays.asList(p101, p102, p103, p104, p105, p106, p107);

        for (Pedido pedido : todosLosPedidos) {
            controlador.reservarPedido(pedido);
            pedido.mostrarResumen();
            System.out.println("Tiempo estimado: " + pedido.calcularTiempoEntrega() + " minutos\n");
        }

        // Se cancela un pedido antes de repartirlo, para demostrar el Cancelable
        controlador.cancelarPedido(p103);

        System.out.println("\n-------------------------");
        System.out.println("Concurrencia con hilos + sincronización");
        System.out.println("-------------------------\n");

        ZonaDeCarga zonaDeCarga = new ZonaDeCarga();
        System.out.println("[Zona de carga inicializada]\n");

        // p103 fue cancelado, así que no se agrega a la zona de carga
        List<Pedido> pedidosARepartir = new ArrayList<>(Arrays.asList(p101, p102, p104, p105, p106, p107));
        for (Pedido pedido : pedidosARepartir) {
            zonaDeCarga.agregarPedido(pedido);
        }
        System.out.println();

        // 3 repartidores compitiendo por la zona de carga compartida
        Repartidor luis = new Repartidor("Luis Díaz", zonaDeCarga, controlador);
        Repartidor camila = new Repartidor("Camila Soto", zonaDeCarga, controlador);
        Repartidor juanito = new Repartidor("Juanito Pérez", zonaDeCarga, controlador);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(luis);
        executor.submit(camila);
        executor.submit(juanito);

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println("Algunos repartidores no terminaron a tiempo. Forzando el cierre.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("La espera de finalización fue interrumpida.");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("\nTodos los pedidos han sido entregados correctamente.");

        controlador.verHistorial();
    }
}
