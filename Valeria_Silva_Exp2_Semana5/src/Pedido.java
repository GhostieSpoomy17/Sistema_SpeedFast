
public abstract class Pedido implements Despachable, Cancelable {

    protected int idPedido;
    protected String direccionEntrega;
    protected double distanciaKm;
    protected String repartidorAsignado;
    protected EstadoPedido estado;

    public Pedido(int idPedido, String direccionEntrega, double distanciaKm) {
        this.idPedido = idPedido;
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
        this.repartidorAsignado = null;
        this.estado = EstadoPedido.PENDIENTE;
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
        this.estado = EstadoPedido.ENTREGADO;
        System.out.println("Pedido despachado correctamente.");
    }

    @Override
    public void cancelar() {
        System.out.println("Cancelando " + this.getClass().getSimpleName() + " #" + idPedido + "...");
        this.estado = EstadoPedido.CANCELADO;
        System.out.println("→ Pedido cancelado exitosamente.");
    }

    public void setEstado(String nuevoEstado) {
        try {
            this.estado = EstadoPedido.valueOf(nuevoEstado.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Estado no válido: \"" + nuevoEstado + "\". Se mantiene el estado actual ("
                    + this.estado + ").");
        }
    }

    // Getters ---
    public int getIdPedido() {
        return idPedido;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public String getRepartidorAsignado() {
        return repartidorAsignado;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    // Setters
    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public void setRepartidorAsignado(String repartidorAsignado) {
        this.repartidorAsignado = repartidorAsignado;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " #" + idPedido
                + " | Dirección: " + direccionEntrega
                + " | Distancia: " + (int) distanciaKm + " km"
                + " | Repartidor: " + (repartidorAsignado != null ? repartidorAsignado : "sin asignar")
                + " | Estado: " + estado;
    }
}
