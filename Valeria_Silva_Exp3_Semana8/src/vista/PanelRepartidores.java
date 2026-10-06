package vista;
import dao.RepartidorDAO;
import modelo.RepartidorRegistro;
import javax.swing.*;
public final class PanelRepartidores extends PanelGestion<RepartidorRegistro> {
    private final JTextField nombre = new JTextField();
    private final RepartidorDAO dao = new RepartidorDAO();
    public PanelRepartidores(VentanaPrincipal p) { super(p, "ID", "Nombre"); campo("Nombre", nombre); }
    protected int id(RepartidorRegistro v) { return v.id(); }
    protected Object[] fila(RepartidorRegistro v) { return new Object[]{v.id(), v.nombre()}; }
    protected RepartidorRegistro formulario(int id) { return new RepartidorRegistro(id, obligatorio(nombre, "Nombre")); }
    protected void cargar(RepartidorRegistro v) { nombre.setText(v.nombre()); }
    protected void limpiarCampos() { nombre.setText(""); }
    protected void crear(RepartidorRegistro v) throws Exception { dao.create(v); }
    protected void actualizar(RepartidorRegistro v) throws Exception { dao.update(v); }
    protected void eliminar(int id) throws Exception { dao.delete(id); }
}
