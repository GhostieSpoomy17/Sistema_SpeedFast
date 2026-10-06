package dao;
import java.sql.*;
import java.util.Properties;
/** Configuración de sesión: nunca escribe la contraseña a disco. */
public final class ConexionDB {
    private static String host = "127.0.0.1", usuario = "root", clave = "";
    private static int puerto = 3306;
    private ConexionDB() {}
    public static synchronized void configurar(String h, int p, String u, char[] c) {
        if (!h.matches("[a-zA-Z0-9.:-]+") || p < 1 || p > 65535 || u.isBlank())
            throw new IllegalArgumentException("Servidor, puerto o usuario no válido.");
        host = h; puerto = p; usuario = u.trim(); clave = new String(c);
    }
    public static synchronized Connection getConnection() throws SQLException {
        Properties propiedades = new Properties();
        propiedades.setProperty("user", usuario);
        propiedades.setProperty("password", clave);
        propiedades.setProperty("connectTimeout", "5000");
        propiedades.setProperty("socketTimeout", "10000");
        // Conexión al servidor local instalado; permite autenticar caching_sha2_password.
        propiedades.setProperty("allowPublicKeyRetrieval", "true");
        return DriverManager.getConnection("jdbc:mysql://" + host + ":" + puerto
                + "/speedfast_db", propiedades);
    }
    public static void comprobar() throws SQLException {
        try (Connection c = getConnection(); PreparedStatement s = c.prepareStatement(
                "SELECT id FROM repartidores LIMIT 0"); ResultSet r = s.executeQuery()) {
            // La consulta comprueba conexión y existencia de la tabla.
        }
    }
}
