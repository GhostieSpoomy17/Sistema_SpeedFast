package vista;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
/** Tabla y formulario comunes: las entidades definen sus operaciones específicas. */
public abstract class PanelGestion<T> extends JPanel {
    protected final VentanaPrincipal principal;
    protected final JPanel campos = new JPanel(new GridLayout(0, 2, 10, 8));
    protected final JTable tabla;
    protected final DefaultTableModel modeloTabla;
    protected List<T> registros = List.of();
    protected int seleccionado = -1;
    public PanelGestion(VentanaPrincipal p, String... columnas) {
        super(new BorderLayout(10, 10)); principal = p;
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int col) { return col == 0 ? Integer.class : Object.class; }
        };
        tabla = new JTable(modeloTabla); tabla.setAutoCreateRowSorter(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                seleccionado = fila < 0 ? -1 : tabla.convertRowIndexToModel(fila);
                if (seleccionado >= 0 && seleccionado < registros.size()) cargar(registros.get(seleccionado));
            }
        });
        JPanel formulario = new JPanel(new BorderLayout(8, 8)); formulario.add(campos, BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        boton(botones, "Nuevo / Limpiar", this::limpiar);
        boton(botones, "Registrar", () -> guardar(false));
        boton(botones, "Actualizar", () -> guardar(true));
        boton(botones, "Eliminar", this::eliminarSeleccion);
        boton(botones, "Refrescar", principal::refrescar);
        formulario.add(botones, BorderLayout.SOUTH); add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
    protected void campo(String etiqueta, JComponent control) { campos.add(new JLabel(etiqueta)); campos.add(control); }
    protected void boton(JPanel panel, String nombre, Runnable accion) {
        JButton b = new JButton(nombre); panel.add(b); b.addActionListener(e -> { if (!principal.ocupada()) accion.run(); });
    }
    protected String obligatorio(JTextField campo, String nombre) {
        String texto = campo.getText().trim();
        if (texto.isEmpty() || texto.length() > 100) throw new IllegalArgumentException(nombre + ": ingresa entre 1 y 100 caracteres.");
        return texto;
    }
    protected T seleccionado() {
        if (seleccionado < 0) throw new IllegalArgumentException("Selecciona una fila en la tabla.");
        return registros.get(seleccionado);
    }
    private void guardar(boolean editar) {
        try {
            int id = editar ? id(seleccionado()) : 0;
            T valor = formulario(id);
            principal.operar(() -> { if (editar) actualizar(valor); else crear(valor); return null; },
                    v -> { limpiar(); principal.refrescar(); JOptionPane.showMessageDialog(this, "Datos guardados correctamente."); });
        } catch (IllegalArgumentException e) { Mensajes.error(this, e); }
    }
    private void eliminarSeleccion() {
        try {
            int id = id(seleccionado());
            if (JOptionPane.showConfirmDialog(this, "¿Eliminar el registro #" + id + "?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            principal.operar(() -> { eliminar(id); return null; }, v -> {
                limpiar(); principal.refrescar(); JOptionPane.showMessageDialog(this, "Registro eliminado.");
            });
        } catch (IllegalArgumentException e) { Mensajes.error(this, e); }
    }
    public void mostrar(List<T> nuevos) {
        tabla.clearSelection(); registros = nuevos; modeloTabla.setRowCount(0);
        for (T v : nuevos) modeloTabla.addRow(fila(v));
    }
    public void limpiar() { tabla.clearSelection(); seleccionado = -1; limpiarCampos(); }
    protected abstract int id(T v);
    protected abstract Object[] fila(T v);
    protected abstract T formulario(int id);
    protected abstract void cargar(T v);
    protected abstract void limpiarCampos();
    protected abstract void crear(T v) throws Exception;
    protected abstract void actualizar(T v) throws Exception;
    protected abstract void eliminar(int id) throws Exception;
}
