package Sistema_De_Monitoreo;

import javax.swing.*;
import java.awt.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema de Monitoreo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Crear CardLayout y panel contenedor
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            // Inicializa las vistas
            Vista_Inicio vistaInicio = new Vista_Inicio();
            Vista_Monitor vistaMonitor = new Vista_Monitor();
            Vista_Historico vistaHistorico = new Vista_Historico();

            // Agregar las vistas al CardLayout
            mainPanel.add(vistaInicio.getPanelInicio(), "Inicio");
            mainPanel.add(vistaMonitor.getPanelMonitor(), "Monitor");
            mainPanel.add(vistaHistorico.getPanelHistorico(), "Historico");

            // Añadir los listeners para los botones monitor e historico
            vistaInicio.getMonitorButton().addActionListener(e -> cardLayout.show(mainPanel, "Monitor"));
            vistaInicio.gethistoricoButton().addActionListener(e -> cardLayout.show(mainPanel, "Historico"));

            // Mostrar la vista Inicio por defecto
            cardLayout.show(mainPanel, "Inicio");

            // Configurar el frame
            frame.setContentPane(mainPanel);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

