// EXP 1, SEMANA 3 - SUMATIVA 1

public class Main {
    public static void main(String[] args) {

        ControladorDeEnvios controlador = new ControladorDeEnvios();

        PedidoComida pedidoComida = new PedidoComida("a746", "Av. Italia 1439", 3);
        PedidoEncomienda pedidoEncomienda = new PedidoEncomienda("a184", "Av. Independencia 2935", 7);
        PedidoExpress pedidoExpress = new PedidoExpress("h174", "Av. Apoquindo 6410", 6);

        controlador.reservarPedido(pedidoComida);
        controlador.reservarPedido(pedidoEncomienda);
        controlador.reservarPedido(pedidoExpress);

        System.out.println("\n[Pedido Comida]");
        pedidoComida.mostrarResumen();
        pedidoComida.asignarRepartidor();
        System.out.println("Tiempo estimado: " + pedidoComida.calcularTiempoEntrega() + " minutos");
        controlador.despacharPedido(pedidoComida);

        System.out.println("\n[Pedido Encomienda]");
        pedidoEncomienda.mostrarResumen();
        pedidoEncomienda.asignarRepartidor("Daniela Tapia"); // manual
        System.out.println("Tiempo estimado: " + pedidoEncomienda.calcularTiempoEntrega() + " minutos");
        controlador.despacharPedido(pedidoEncomienda);

        System.out.println("\n[Pedido Express]");
        pedidoExpress.mostrarResumen();
        pedidoExpress.asignarRepartidor();
        System.out.println("Tiempo estimado: " + pedidoExpress.calcularTiempoEntrega() + " minutos");
        controlador.cancelarPedido(pedidoExpress);

        controlador.verHistorial();
    }
}
