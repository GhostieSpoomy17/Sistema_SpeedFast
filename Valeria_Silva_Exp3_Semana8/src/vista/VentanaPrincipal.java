package vista;
import dao.*;
import modelo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
public final class VentanaPrincipal extends JFrame {
    private final PanelRepartidores repartidores = new PanelRepartidores(this);
    private final PanelPedidos pedidos = new PanelPedidos(this);
    private final PanelEntregas entregas = new PanelEntregas(this);
    private final JLabel estado = new JLabel("Conecta a MySQL para comenzar.");
    private boolean ocupada;
    public VentanaPrincipal() {
        this(false);
    }
    /** Permite abrir la ventana después de una conexión comprobada. */
    public VentanaPrincipal(boolean conexionComprobada) {
        super("SpeedFast · Gestión de pedidos y entregas");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                if (ocupada) JOptionPane.showMessageDialog(VentanaPrincipal.this, "Espera a que termine la operación.");
                else dispose();
            }
        });
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Repartidores", repartidores); pestanas.addTab("Pedidos", pedidos); pestanas.addTab("Entregas", entregas);
        JPanel navegacion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton registrar = new JButton("Registrar pedido"), listar = new JButton("Listar pedidos"), asignar = new JButton("Asignar repartidor / Iniciar entrega");
        navegacion.add(registrar); navegacion.add(listar); navegacion.add(asignar);
        registrar.addActionListener(e -> { if (!ocupada) new VentanaRegistroPedido(this).setVisible(true); });
        listar.addActionListener(e -> { if (!ocupada) new VentanaListaPedidos(this).setVisible(true); });
        asignar.addActionListener(e -> pestanas.setSelectedComponent(entregas));
        add(navegacion, BorderLayout.NORTH); add(pestanas, BorderLayout.CENTER); add(estado, BorderLayout.SOUTH);
        setSize(1000, 650); setMinimumSize(new Dimension(850, 550)); setLocationRelativeTo(null);
        SwingUtilities.invokeLater(conexionComprobada ? this::refrescar : this::conectar);
    }
    public boolean ocupada() { return ocupada; }
    public <T> void operar(Callable<T> tarea, Consumer<T> resultado) {
        if (ocupada) return;
        ocupada = true; estado.setText("Procesando…"); setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<T, Void>() {
            protected T doInBackground() throws Exception { return tarea.call(); }
            protected void done() {
                ocupada = false; setCursor(Cursor.getDefaultCursor()); estado.setText("Listo.");
                try { resultado.accept(get()); }
                catch (Exception ex) { estado.setText("La operación no pudo completarse."); Mensajes.error(VentanaPrincipal.this, ex); }
            }
        }.execute();
    }
    private record Datos(List<RepartidorRegistro> repartidores, List<Pedido> pedidos, List<Entrega> entregas) {}
    public void refrescar() {
        operar(() -> new Datos(new RepartidorDAO().readAll(), new PedidoDAO().readAll(), new EntregaDAO().readAll()), d -> {
            repartidores.mostrar(d.repartidores()); pedidos.mostrar(d.pedidos()); entregas.opciones(d.pedidos(), d.repartidores()); entregas.mostrar(d.entregas());
            estado.setText("Datos actualizados desde speedfast_db.");
        });
    }
    private void conectar() {
        JTextField host = new JTextField("127.0.0.1"), puerto = new JTextField("3306"), usuario = new JTextField("root");
        JPasswordField clave = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Servidor")); panel.add(host); panel.add(new JLabel("Puerto")); panel.add(puerto);
        panel.add(new JLabel("Usuario")); panel.add(usuario); panel.add(new JLabel("Contraseña")); panel.add(clave);
        if (JOptionPane.showConfirmDialog(this, panel, "Conectar a speedfast_db", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) { dispose(); return; }
        char[] password = clave.getPassword();
        try { ConexionDB.configurar(host.getText().trim(), Integer.parseInt(puerto.getText().trim()), usuario.getText(), password); }
        catch (IllegalArgumentException ex) { Mensajes.error(this, ex); SwingUtilities.invokeLater(this::conectar); return; }
        finally { java.util.Arrays.fill(password, '\0'); clave.setText(""); }
        operar(() -> { ConexionDB.comprobar(); return null; }, x -> refrescar());
        // El usuario puede volver a conectar desde el menú si la prueba falla.
    }
    { JMenuBar barra = new JMenuBar(); JMenu menu = new JMenu("Base de datos"); JMenuItem conectar = new JMenuItem("Conectar / Cambiar conexión");
      conectar.addActionListener(e -> { if (!ocupada) conectar(); }); menu.add(conectar); barra.add(menu); setJMenuBar(barra); }
}
