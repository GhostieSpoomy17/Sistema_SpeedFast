
public class PedidoEncomienda extends Pedido {

    public PedidoEncomienda(String idPedido, String direccionEntrega, double distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    @Override
    public int calcularTiempoEntrega() {
        double tiempo = 20 + (1.5 * distanciaKm);
        return (int) Math.round(tiempo);
    }

    @Override
    public void asignarRepartidor() {
        System.out.println("Asignando repartidor...");
        System.out.println("→ Validando peso y embalaje... OK");
        this.repartidorAsignado = "Camila Soto";
        System.out.println("Repartidor asignado: " + repartidorAsignado + " (asignación automática)");
    }

    @Override
    public void asignarRepartidor(String nombreRepartidor) {
        System.out.println("Asignando repartidor...");
        System.out.println("→ Validando peso y embalaje... OK");
        this.repartidorAsignado = nombreRepartidor;
        System.out.println("Repartidor asignado: " + repartidorAsignado + " (asignación manual)");
    }
}
