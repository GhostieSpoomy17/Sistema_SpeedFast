package vista;
import dao.PedidoDAO;
import modelo.*;
import javax.swing.*;
public final class PanelPedidos extends PanelGestion<Pedido> {
    private final JTextField direccion = new JTextField();
    private final JComboBox<TipoPedido> tipo = new JComboBox<>(TipoPedido.values());
    private final JComboBox<EstadoPedido> estado = new JComboBox<>(new EstadoPedido[]{
            EstadoPedido.PENDIENTE, EstadoPedido.EN_REPARTO, EstadoPedido.ENTREGADO});
    private final PedidoDAO dao = new PedidoDAO();
    public PanelPedidos(VentanaPrincipal p) {
        super(p, "ID", "Dirección", "Tipo", "Estado");
        campo("ID asignado automáticamente por MySQL", new JLabel("Selecciona una fila para editar"));
        campo("Dirección", direccion); campo("Tipo", tipo); campo("Estado", estado);
    }
    protected int id(Pedido v) { return v.getIdPedido(); }
    protected Object[] fila(Pedido v) { return new Object[]{v.getIdPedido(), v.getDireccionEntrega(), Pedidos.tipo(v), v.getEstado()}; }
    protected Pedido formulario(int id) {
        return Pedidos.crear(Math.max(1, id), obligatorio(direccion, "Dirección"),
                (TipoPedido) tipo.getSelectedItem(), (EstadoPedido) estado.getSelectedItem());
    }
    protected void cargar(Pedido v) { direccion.setText(v.getDireccionEntrega()); tipo.setSelectedItem(Pedidos.tipo(v)); estado.setSelectedItem(v.getEstado()); }
    protected void limpiarCampos() { direccion.setText(""); tipo.setSelectedIndex(0); estado.setSelectedIndex(0); }
    protected void crear(Pedido v) throws Exception { dao.create(v); }
    protected void actualizar(Pedido v) throws Exception { dao.update(v); }
    protected void eliminar(int id) throws Exception { dao.delete(id); }
    public JTable getTabla() { return tabla; }
}
