package vista;
import dao.PedidoDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.*;
import java.awt.*;
public final class VentanaListaPedidos extends JFrame {
    public VentanaListaPedidos(VentanaPrincipal principal) {
        super("SpeedFast · Listado de pedidos");
        DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Dirección", "Tipo", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo); tabla.setAutoCreateRowSorter(true);
        JButton refrescar = new JButton("Refrescar");
        Runnable cargar = () -> principal.operar(() -> new PedidoDAO().readAll(), lista -> {
            modelo.setRowCount(0); for (Pedido p : lista) modelo.addRow(new Object[]{p.getIdPedido(), p.getDireccionEntrega(), Pedidos.tipo(p), p.getEstado()});
        });
        refrescar.addActionListener(e -> cargar.run()); add(refrescar, BorderLayout.NORTH); add(new JScrollPane(tabla));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(750, 450); setLocationRelativeTo(principal); cargar.run();
    }
}
