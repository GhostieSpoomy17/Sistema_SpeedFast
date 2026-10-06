package vista;
import javax.swing.*;
import java.awt.Component;
import java.sql.SQLException;
public final class Mensajes {
    private Mensajes() {}
    public static void error(Component padre, Throwable error) {
        Throwable causa = error;
        while (causa.getCause() != null) causa = causa.getCause();
        String texto = causa.getMessage();
        if (causa instanceof SQLException sql) {
            texto = switch (sql.getErrorCode()) {
                case 1045 -> "MySQL rechazó el usuario o la contraseña. Cierra y vuelve a conectar.";
                case 1049, 1146 -> "Falta la base de datos o sus tablas. Ejecuta sql/speedfast_db.sql.";
                case 1451 -> "No puedes eliminar este registro porque tiene entregas asociadas. Gestiona esas entregas primero.";
                case 1452 -> "El pedido o repartidor seleccionado ya no existe. Refresca las listas.";
                default -> sql.getSQLState() != null && sql.getSQLState().startsWith("08")
                        ? "No se pudo conectar a MySQL. Comprueba el servicio, servidor y puerto."
                        : "No se pudo completar la operación: " + sql.getMessage();
            };
        }
        JOptionPane.showMessageDialog(padre, texto, "SpeedFast", JOptionPane.ERROR_MESSAGE);
    }
}
