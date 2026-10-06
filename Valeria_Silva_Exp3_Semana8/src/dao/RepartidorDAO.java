package dao;
import modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/** CRUD parametrizado para repartidores. Cada operación cierra sus recursos. */
public final class RepartidorDAO {
    private void validar(RepartidorRegistro v) { if (v.nombre() == null || v.nombre().isBlank() || v.nombre().length() > 100) throw new IllegalArgumentException("El nombre es obligatorio y admite hasta 100 caracteres."); }
    private void parametros(PreparedStatement s, RepartidorRegistro v) throws SQLException { s.setString(1, v.nombre()); }
    public int create(RepartidorRegistro v) throws SQLException {
        validar(v);
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "INSERT INTO repartidores(nombre) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
            parametros(s, v); s.executeUpdate();
            try (ResultSet r = s.getGeneratedKeys()) {
                if (r.next()) return r.getInt(1);
                throw new SQLException("No se obtuvo el ID generado.");
            }
        }
    }
    public List<RepartidorRegistro> readAll() throws SQLException {
        List<RepartidorRegistro> lista = new ArrayList<>();
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "SELECT * FROM repartidores ORDER BY id"); ResultSet r = s.executeQuery()) {
            while (r.next()) lista.add(new RepartidorRegistro(r.getInt("id"), r.getString("nombre")));
        }
        return lista;
    }
    public void update(RepartidorRegistro v) throws SQLException {
        validar(v);
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "UPDATE repartidores SET nombre=? WHERE id=?")) {
            parametros(s, v); s.setInt(2, v.id());
            if (s.executeUpdate() != 1) throw new SQLException("El registro ya no existe. Refresca la tabla.");
        }
    }
    public void delete(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Selecciona un registro.");
        try (Connection c = ConexionDB.getConnection(); PreparedStatement s = c.prepareStatement(
                "DELETE FROM repartidores WHERE id=?")) {
            s.setInt(1, id);
            if (s.executeUpdate() != 1) throw new SQLException("El registro ya no existe. Refresca la tabla.");
        }
    }

}
