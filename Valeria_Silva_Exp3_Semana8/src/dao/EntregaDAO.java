package dao;
import modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/** CRUD parametrizado para entregas. Cada operación cierra sus recursos. */
public final class EntregaDAO {
    private void validar(Entrega v) { if (v.idPedido() <= 0 || v.idRepartidor() <= 0 || v.fecha() == null || v.hora() == null) throw new IllegalArgumentException("Selecciona pedido, repartidor, fecha y hora."); }
    private void parametros(PreparedStatement s, Entrega v) throws SQLException { s.setInt(1, v.idPedido()); s.setInt(2, v.idRepartidor()); s.setDate(3, Date.valueOf(v.fecha())); s.setTime(4, Time.valueOf(v.hora())); }
    public int create(Entrega v) throws SQLException {
        validar(v);
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "INSERT INTO entregas(id_pedido,id_repartidor,fecha,hora) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            parametros(s, v); s.executeUpdate();
            try (ResultSet r = s.getGeneratedKeys()) {
                if (r.next()) return r.getInt(1);
                throw new SQLException("No se obtuvo el ID generado.");
            }
        }
    }
    public List<Entrega> readAll() throws SQLException {
        List<Entrega> lista = new ArrayList<>();
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "SELECT * FROM entregas ORDER BY id"); ResultSet r = s.executeQuery()) {
            while (r.next()) lista.add(new Entrega(r.getInt("id"), r.getInt("id_pedido"), r.getInt("id_repartidor"), r.getDate("fecha").toLocalDate(), r.getTime("hora").toLocalTime()));
        }
        return lista;
    }
    public void update(Entrega v) throws SQLException {
        validar(v);
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "UPDATE entregas SET id_pedido=?,id_repartidor=?,fecha=?,hora=? WHERE id=?")) {
            parametros(s, v); s.setInt(5, v.id());
            if (s.executeUpdate() != 1) throw new SQLException("El registro ya no existe. Refresca la tabla.");
        }
    }
    public void delete(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Selecciona un registro.");
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "DELETE FROM entregas WHERE id=?")) {
            s.setInt(1, id);
            if (s.executeUpdate() != 1) throw new SQLException("El registro ya no existe. Refresca la tabla.");
        }
    }

    /** La simulación guarda la entrega y el estado final en una misma transacción. */
    public void finalizarEntrega(Entrega v) throws SQLException {
        validar(v);
        try (Connection c = ConexionDB.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement p = c.prepareStatement(
                    "UPDATE pedidos SET estado='ENTREGADO' WHERE id=? AND estado='EN_REPARTO'");
                 PreparedStatement e = c.prepareStatement(
                    "INSERT INTO entregas(id_pedido,id_repartidor,fecha,hora) VALUES(?,?,?,?)")) {
                p.setInt(1, v.idPedido());
                if (p.executeUpdate() != 1) throw new SQLException("El pedido ya no está EN_REPARTO.");
                parametros(e, v); e.executeUpdate(); c.commit();
            } catch (SQLException ex) {
                try { c.rollback(); } catch (SQLException rollback) { ex.addSuppressed(rollback); }
                throw ex;
            }
        }
    }

}
