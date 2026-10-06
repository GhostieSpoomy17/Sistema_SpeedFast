package modelo;
/** Repartidor persistido; la clase Repartidor conserva el trabajador de la simulación. */
public record RepartidorRegistro(int id, String nombre) {
    @Override public String toString() { return id + " - " + nombre; }
}
