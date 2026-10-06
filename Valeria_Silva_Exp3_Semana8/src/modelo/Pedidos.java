package modelo;
/** Convierte las filas JDBC al modelo polimórfico existente. */
public final class Pedidos {
    private Pedidos() {}
    public static Pedido crear(int id, String direccion, TipoPedido tipo, EstadoPedido estado) {
        if (estado == EstadoPedido.CANCELADO) throw new IllegalArgumentException("Estado no admitido en la semana 8.");
        Pedido p = switch (tipo) {
            case COMIDA -> new PedidoComida(id, direccion, 0);
            case ENCOMIENDA -> new PedidoEncomienda(id, direccion, 0);
            case EXPRESS -> new PedidoExpress(id, direccion, 0);
        };
        // Hidratar una fila no equivale a ejecutar una transición de reparto.
        p.estado = estado;
        return p;
    }
    public static TipoPedido tipo(Pedido p) {
        if (p instanceof PedidoComida) return TipoPedido.COMIDA;
        if (p instanceof PedidoEncomienda) return TipoPedido.ENCOMIENDA;
        return TipoPedido.EXPRESS;
    }
}
