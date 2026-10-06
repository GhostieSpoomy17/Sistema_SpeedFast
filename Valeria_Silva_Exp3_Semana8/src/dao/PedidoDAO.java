package dao;
import modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/** CRUD parametrizado para pedidos. Cada operación cierra sus recursos. */
public final class PedidoDAO {
    private void validar(Pedido v) { if (v.getDireccionEntrega().isBlank() || v.getDireccionEntrega().length() > 100 || v.getEstado() == EstadoPedido.CANCELADO) throw new IllegalArgumentException("Dirección o estado no válido."); }
    private void parametros(PreparedStatement s, Pedido v) throws SQLException { s.setString(1, v.getDireccionEntrega()); s.setString(2, Pedidos.tipo(v).name()); s.setString(3, v.getEstado().name()); }
    public int create(Pedido v) throws SQLException {
        validar(v);
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "INSERT INTO pedidos(direccion,tipo,estado) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            parametros(s, v); s.executeUpdate();
            try (ResultSet r = s.getGeneratedKeys()) {
                if (r.next()) return r.getInt(1);
                throw new SQLException("No se obtuvo el ID generado.");
            }
        }
    }
    public List<Pedido> readAll() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "SELECT * FROM pedidos ORDER BY id"); ResultSet r = s.executeQuery()) {
            while (r.next()) lista.add(Pedidos.crear(r.getInt("id"), r.getString("direccion"), TipoPedido.valueOf(r.getString("tipo")), EstadoPedido.valueOf(r.getString("estado"))));
        }
        return lista;
    }
    public void update(Pedido v) throws SQLException {
        validar(v);
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "UPDATE pedidos SET direccion=?,tipo=?,estado=? WHERE id=?")) {
            parametros(s, v); s.setInt(4, v.getIdPedido());
            if (s.executeUpdate() != 1) throw new SQLException("El registro ya no existe. Refresca la tabla.");
        }
    }
    public void delete(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Selecciona un registro.");
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "DELETE FROM pedidos WHERE id=?")) {
            s.setInt(1, id);
            if (s.executeUpdate() != 1) throw new SQLException("El registro ya no existe. Refresca la tabla.");
        }
    }

    public void iniciarEntrega(int id) throws SQLException {
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "UPDATE pedidos SET estado='EN_REPARTO' WHERE id=? AND estado='PENDIENTE'")) {
            s.setInt(1, id);
            if (s.executeUpdate() != 1) throw new SQLException("El pedido debe estar PENDIENTE para iniciar una entrega.");
        }
    }
    public void recuperarEntrega(int id) throws SQLException {
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "UPDATE pedidos SET estado='PENDIENTE' WHERE id=? AND estado='EN_REPARTO'")) {
            s.setInt(1, id); s.executeUpdate();
        }
    }

}
