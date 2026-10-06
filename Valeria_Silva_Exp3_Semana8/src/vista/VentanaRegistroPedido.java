package vista;
import javax.swing.*;
/** Ventana de registro y edición que utiliza la misma base de datos que la principal. */
public final class VentanaRegistroPedido extends JFrame {
    public VentanaRegistroPedido(VentanaPrincipal principal) {
        super("SpeedFast · Registrar pedido");
        PanelPedidos panel = new PanelPedidos(principal); add(panel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(750, 450); setLocationRelativeTo(principal);
    }
}
