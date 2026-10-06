package vista;
import dao.*;
import modelo.*;
import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.util.List;
public final class PanelEntregas extends PanelGestion<Entrega> {
    private final JComboBox<Pedido> pedido = new JComboBox<>();
    private final JComboBox<RepartidorRegistro> repartidor = new JComboBox<>();
    private final JTextField fecha = new JTextField(LocalDate.now().toString());
    private final JTextField hora = new JTextField(LocalTime.now().withNano(0).toString());
    private final EntregaDAO dao = new EntregaDAO();
    private final PedidoDAO pedidos = new PedidoDAO();
    public PanelEntregas(VentanaPrincipal p) {
        super(p, "ID", "Pedido", "Repartidor", "Fecha", "Hora");
        pedido.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                if (v instanceof Pedido x) v = x.getIdPedido() + " - " + x.getDireccionEntrega();
                return super.getListCellRendererComponent(l, v, i, s, f);
            }
        });
        campo("Pedido", pedido); campo("Repartidor", repartidor); campo("Fecha (aaaa-mm-dd)", fecha); campo("Hora (hh:mm:ss)", hora);
        JButton simular = new JButton("Asignar repartidor / Simular entrega");
        campo("Simulación de un pedido PENDIENTE", simular); simular.addActionListener(e -> simular());
    }
    public void opciones(List<Pedido> ps, List<RepartidorRegistro> rs) {
        Pedido anterior = (Pedido) pedido.getSelectedItem(); RepartidorRegistro previo = (RepartidorRegistro) repartidor.getSelectedItem();
        pedido.removeAllItems(); for (Pedido p : ps) pedido.addItem(p);
        repartidor.removeAllItems(); for (RepartidorRegistro r : rs) repartidor.addItem(r);
        if (anterior != null) elegirPedido(anterior.getIdPedido());
        if (previo != null) elegirRepartidor(previo.id());
    }
    private void elegirPedido(int id) { for (int i=0;i<pedido.getItemCount();i++) if(pedido.getItemAt(i).getIdPedido()==id) { pedido.setSelectedIndex(i); return; } }
    private void elegirRepartidor(int id) { for (int i=0;i<repartidor.getItemCount();i++) if(repartidor.getItemAt(i).id()==id) { repartidor.setSelectedIndex(i); return; } }
    protected int id(Entrega v) { return v.id(); }
    protected Object[] fila(Entrega v) { return new Object[]{v.id(), v.idPedido(), v.idRepartidor(), v.fecha(), v.hora()}; }
    protected Entrega formulario(int id) {
        Pedido p = (Pedido) pedido.getSelectedItem(); RepartidorRegistro r = (RepartidorRegistro) repartidor.getSelectedItem();
        if (p == null || r == null) throw new IllegalArgumentException("Primero registra un pedido y un repartidor.");
        try {
            LocalDate d = LocalDate.parse(fecha.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalTime t = LocalTime.parse(hora.getText().trim(), DateTimeFormatter.ISO_LOCAL_TIME);
            if (t.getNano() != 0) throw new DateTimeParseException("Sin fracciones", hora.getText(), 0);
            return new Entrega(id, p.getIdPedido(), r.id(), d, t);
        } catch (DateTimeParseException e) { throw new IllegalArgumentException("Fecha u hora no válida. Usa aaaa-mm-dd y hh:mm:ss."); }
    }
    protected void cargar(Entrega v) { elegirPedido(v.idPedido()); elegirRepartidor(v.idRepartidor()); fecha.setText(v.fecha().toString()); hora.setText(v.hora().toString()); }
    protected void limpiarCampos() { fecha.setText(LocalDate.now().toString()); hora.setText(LocalTime.now().withNano(0).toString()); }
    protected void crear(Entrega v) throws Exception { dao.create(v); }
    protected void actualizar(Entrega v) throws Exception { dao.update(v); }
    protected void eliminar(int id) throws Exception { dao.delete(id); }
    private void simular() {
        if (principal.ocupada()) return;
        try {
            Entrega v = formulario(0);
            principal.operar(() -> {
                pedidos.iniciarEntrega(v.idPedido());
                try {
                    Thread.sleep(1500);
                    dao.finalizarEntrega(new Entrega(0, v.idPedido(), v.idRepartidor(), LocalDate.now(), LocalTime.now().withNano(0)));
                } catch (Exception ex) {
                    try { pedidos.recuperarEntrega(v.idPedido()); } catch (Exception recuperacion) { ex.addSuppressed(recuperacion); }
                    if (ex instanceof InterruptedException) Thread.currentThread().interrupt();
                    throw ex;
                }
                return null;
            }, x -> { principal.refrescar(); JOptionPane.showMessageDialog(this, "Entrega finalizada y guardada. Pedido ENTREGADO."); });
        } catch (IllegalArgumentException ex) { Mensajes.error(this, ex); }
    }
}
