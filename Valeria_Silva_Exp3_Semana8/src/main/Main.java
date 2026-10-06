package main;
import javax.swing.*;
import vista.VentanaPrincipal;
public final class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--simulacion")) { SimulacionMain.main(args); return; }
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { /* El aspecto Swing predeterminado también es válido. */ }
            new VentanaPrincipal().setVisible(true);
        });
    }
}
