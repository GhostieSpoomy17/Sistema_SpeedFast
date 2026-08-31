
public abstract class Pedido implements Despachable, Cancelable {

    protected String idPedido;
    protected String direccionEntrega;
    protected double distanciaKm;
    protected String repartidorAsignado;
    protected String estado;

    public Pedido(String idPedido, String direccionEntrega, double distanciaKm) {
        this.idPedido = idPedido;
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
        this.repartidorAsignado = null;
        this.estado = "Pendiente";
    }

    public void mostrarResumen() {
        System.out.println(this.getClass().getSimpleName() + " #" + idPedido);
        System.out.println("Dirección: " + direccionEntrega);
        System.out.println("Distancia: " + (int) distanciaKm + " km");
    }

    public abstract int calcularTiempoEntrega();

    public abstract void asignarRepartidor();

    public abstract void asignarRepartidor(String nombreRepartidor);

    @Override
    public void despachar() {
        this.estado = "Despachado";
        System.out.println("Pedido despachado correctamente.");
    }

    @Override
    public void cancelar() {
        System.out.println("Cancelando " + this.getClass().getSimpleName() + " #" + idPedido + "...");
        this.estado = "Cancelado";
        System.out.println("→ Pedido cancelado exitosamente.");
    }

    public String getIdPedido() {
        return idPedido;
    }

    public String getRepartidorAsignado() {
        return repartidorAsignado;
    }

    public String getEstado() {
        return estado;
    }
}
