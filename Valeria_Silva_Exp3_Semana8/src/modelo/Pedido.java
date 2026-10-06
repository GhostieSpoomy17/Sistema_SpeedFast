package modelo;
import java.util.Locale;

public abstract class Pedido implements Despachable, Cancelable {
    protected final int idPedido;
    protected String direccionEntrega;
    protected final double distanciaKm;
    protected String repartidorAsignado;
    protected EstadoPedido estado;

    public Pedido(int idPedido, String direccionEntrega, double distanciaKm) {
        if (idPedido <= 0 || direccionEntrega == null || direccionEntrega.isBlank()
                || !Double.isFinite(distanciaKm) || distanciaKm < 0) {
            throw new IllegalArgumentException("Identificación, dirección o distancia no válida.");
        }
        this.idPedido = idPedido;
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
        this.estado = EstadoPedido.PENDIENTE;
    }

    public synchronized void mostrarResumen() {
        System.out.println(getClass().getSimpleName() + " #" + idPedido);
        System.out.println("Dirección: " + direccionEntrega);
        System.out.println("Distancia: " + distanciaKm + " km");
    }

    public abstract int calcularTiempoEntrega();
    public abstract void asignarRepartidor();
    public abstract void asignarRepartidor(String nombreRepartidor);

    @Override
    public synchronized void despachar() {
        if (estado != EstadoPedido.PENDIENTE || repartidorAsignado == null) {
            System.out.println("No se puede despachar el pedido #" + idPedido + ".");
            return;
        }
        estado = EstadoPedido.EN_REPARTO;
        System.out.println("Pedido #" + idPedido + " despachado: EN_REPARTO.");
    }

    public synchronized void entregar() {
        if (estado != EstadoPedido.EN_REPARTO) {
            System.out.println("No se puede entregar el pedido #" + idPedido + ".");
            return;
        }
        estado = EstadoPedido.ENTREGADO;
        System.out.println("Pedido #" + idPedido + " entregado correctamente.");
    }

    @Override
    public synchronized void cancelar() {
        if (estado == EstadoPedido.ENTREGADO || estado == EstadoPedido.CANCELADO) {
            System.out.println("No se puede cancelar el pedido #" + idPedido + " en estado " + estado + ".");
            return;
        }
        estado = EstadoPedido.CANCELADO;
        System.out.println("Pedido #" + idPedido + " cancelado.");
    }

    public synchronized void setEstado(String nuevoEstado) {
        if (nuevoEstado == null) {
            System.out.println("Estado no válido: null. Se mantiene " + estado + ".");
            return;
        }
        final EstadoPedido destino;
        try {
            destino = EstadoPedido.valueOf(nuevoEstado.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            System.out.println("Estado no válido: " + nuevoEstado + ". Se mantiene " + estado + ".");
            return;
        }
        if (destino == estado) return;
        switch (destino) {
            case EN_REPARTO -> despachar();
            case ENTREGADO -> entregar();
            case CANCELADO -> cancelar();
            case PENDIENTE -> System.out.println("Solo una entrega interrumpida puede volver a PENDIENTE.");
        }
    }

    // Recuperación explícita: no permite reabrir pedidos entregados o cancelados.
    public synchronized boolean recuperarEntregaInterrumpida() {
        if (estado != EstadoPedido.EN_REPARTO) return false;
        estado = EstadoPedido.PENDIENTE;
        repartidorAsignado = null;
        return true;
    }

    public int getIdPedido() { return idPedido; }
    public synchronized String getDireccionEntrega() { return direccionEntrega; }
    public double getDistanciaKm() { return distanciaKm; }
    public synchronized String getRepartidorAsignado() { return repartidorAsignado; }
    public synchronized EstadoPedido getEstado() { return estado; }

    public synchronized void setDireccionEntrega(String direccionEntrega) {
        if (direccionEntrega == null || direccionEntrega.isBlank()) {
            throw new IllegalArgumentException("La dirección no puede estar vacía.");
        }
        this.direccionEntrega = direccionEntrega;
    }

    public synchronized void setRepartidorAsignado(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del repartidor no puede estar vacío.");
        }
        if (estado != EstadoPedido.PENDIENTE) {
            System.out.println("No se puede reasignar el pedido #" + idPedido + " en estado " + estado + ".");
            return;
        }
        repartidorAsignado = nombre.trim();
    }

    @Override
    public synchronized String toString() {
        return getClass().getSimpleName() + " #" + idPedido
                + " | Dirección: " + direccionEntrega + " | Distancia: " + distanciaKm + " km"
                + " | Repartidor: " + (repartidorAsignado != null ? repartidorAsignado : "sin asignar")
                + " | Estado: " + estado;
    }
}
