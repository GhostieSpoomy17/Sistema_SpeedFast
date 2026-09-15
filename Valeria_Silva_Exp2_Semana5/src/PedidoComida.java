
public class PedidoComida extends Pedido {

    public PedidoComida(int idPedido, String direccionEntrega, double distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    @Override
    public int calcularTiempoEntrega() {
        return (int) Math.round(15 + (2 * distanciaKm));
    }

    @Override
    public void asignarRepartidor() {
        System.out.println("Asignando repartidor...");
        System.out.println("→ Verificando mochila térmica... OK");
        this.repartidorAsignado = "Luis Díaz";
        System.out.println("Repartidor asignado: " + repartidorAsignado + " (asignación automática)");
    }

    @Override
    public void asignarRepartidor(String nombreRepartidor) {
        System.out.println("Asignando repartidor...");
        System.out.println("→ Verificando mochila térmica... OK");
        this.repartidorAsignado = nombreRepartidor;
        System.out.println("Repartidor asignado: " + repartidorAsignado + " (asignación manual)");
    }
}
