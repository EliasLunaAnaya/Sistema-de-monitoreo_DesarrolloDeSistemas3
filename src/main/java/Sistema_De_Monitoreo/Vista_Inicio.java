package Sistema_De_Monitoreo;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Vista_Inicio {

    //Componentes del GUI
    private JPanel JPanel_Inicio;
    private JButton monitorButton;
    private JButton historicoButton;
    private JLabel JLabelSM;
    private JLabel JLabelName;

    //Constructor
    public Vista_Inicio() {
        //Configuración del GUI (lo visual)

        //Borde azul en la interfaz
        JPanel_Inicio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x00529e), 14), // borde azul Unison
                BorderFactory.createEmptyBorder(20, 20, 20, 20) // espacio interno
        ));

        JPanel_Inicio.setBackground(Color.WHITE); //Fondo blanco en la interfaz


        //Bordes redondeados y con color azul fuerte a los botones
        monitorButton.setBorder(new javax.swing.border.LineBorder(new Color(1,82,148), 7, true));
        historicoButton.setBorder(new javax.swing.border.LineBorder(new Color(1,82,148), 7, true));

        //Ajustar tamaño de los botones
        monitorButton.setPreferredSize(new Dimension(150, 50));
        historicoButton.setPreferredSize(new Dimension(150, 50));

        //Fondo blanco de los botones
        monitorButton.setBackground(Color.WHITE);
        historicoButton.setBackground(Color.WHITE);

        monitorButton.setFocusPainted(false);
        historicoButton.setFocusPainted(false);
    }

    //Getters
    public JPanel getPanelInicio() {
        return JPanel_Inicio;
    }

    public JButton getMonitorButton() {
        return monitorButton;
    }

    public JButton gethistoricoButton() {
        return historicoButton;
    }

}








