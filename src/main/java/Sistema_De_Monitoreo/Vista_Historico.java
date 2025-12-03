
package Sistema_De_Monitoreo;

import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.PlotOrientation;

public class Vista_Historico {

    //Componentes de la interfaz
    private JPanel JPanelHistorico;
    private JTextField textFieldFechaInicial;
    private JButton buscarButton;
    private JPanel JPanelGrafica;
    private JButton mostrartodoButton;
    private JLabel CargandoLabel;
    private JButton regresarButton;
    private JTextField textFieldHoraInicial;
    private JLabel FechaInicialLabel;
    private JLabel HoraInicialLabel;
    private JButton limpiarButton;
    private JTextField textFieldFechaFinal;
    private JTextField textFieldHoraFinal;
    private JLabel fechaFinalLabel;
    private JLabel horaFinalLabel;

    //Variables series de la gráfica
    private XYSeries seriesX = new XYSeries("X");
    private XYSeries seriesY = new XYSeries("Y");
    private XYSeries seriesZ = new XYSeries("Z");

    //Constructor
    public Vista_Historico() {
        //Configuración de la interfaz

        JPanelHistorico = new JPanel(new BorderLayout());
        JPanelHistorico.setBackground(Color.WHITE);

        //Borde azul en la interfaz
        JPanelHistorico.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x00529e), 14), // borde azul Unison
                BorderFactory.createEmptyBorder(20, 20, 20, 20) // espacio interno
        ));



        //Panel superior con filtros y el boton Buscar
        JPanel panelSuperior = new JPanel(new GridLayout(2, 5, 10, 10)); // 2 filas, 5 columnas


        // En la primera fila estarán fechainicial y horainicial
        panelSuperior.add(FechaInicialLabel);
        panelSuperior.add(textFieldFechaInicial);
        panelSuperior.add(HoraInicialLabel);
        panelSuperior.add(textFieldHoraInicial);
        panelSuperior.add(new JLabel()); //Se necesitara un espacio vacio

        //En la segunda fila estarán fechafinal, horafinal y el boton para buscar los datos
        panelSuperior.add(fechaFinalLabel);
        panelSuperior.add(textFieldFechaFinal);
        panelSuperior.add(horaFinalLabel);
        panelSuperior.add(textFieldHoraFinal);
        panelSuperior.add(buscarButton);

        //Panel de abajo
        JPanel panelInferior = new JPanel(new FlowLayout());
        panelInferior.add(mostrartodoButton);
        panelInferior.add(limpiarButton);
        panelInferior.add(regresarButton);

        // Agregar subpaneles
        JPanelHistorico.add(panelSuperior, BorderLayout.NORTH);
        JPanelHistorico.add(JPanelGrafica, BorderLayout.CENTER);
        JPanelHistorico.add(panelInferior, BorderLayout.SOUTH);

        //Tamaños de los textfield
        textFieldFechaInicial.setPreferredSize(new Dimension(120, 20));
        textFieldHoraInicial.setPreferredSize(new Dimension(120, 20));

        //Borde azul en la interfaz
        JPanelHistorico.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x00529e), 14),
                BorderFactory.createEmptyBorder(20, 20, 20, 20) //Espacio interno
        ));

        JPanelHistorico.setBackground(Color.WHITE); //Fondo blanco en la interfaz

        //Bordes redondeados y con color azul fuerte a los botones
        buscarButton.setBorder(new javax.swing.border.LineBorder(new Color(1,82,148), 7, true));
        regresarButton.setBorder(new javax.swing.border.LineBorder(new Color(1,82,148), 7, true));
        limpiarButton.setBorder(new javax.swing.border.LineBorder(new Color(1,82,148), 7, true));
        mostrartodoButton.setBorder(new javax.swing.border.LineBorder(new Color(1,82,148), 7, true));

        //Ajustar tamaño de los botones
        buscarButton.setPreferredSize(new Dimension(100, 35));
        regresarButton.setPreferredSize(new Dimension(100, 35));
        limpiarButton.setPreferredSize(new Dimension(100, 35));
        mostrartodoButton.setPreferredSize(new Dimension(100, 35));

        //Fondos blancos en los botones
        buscarButton.setBackground(Color.WHITE);
        regresarButton.setBackground(Color.WHITE);
        limpiarButton.setBackground(Color.WHITE);
        mostrartodoButton.setBackground(Color.WHITE);

        buscarButton.setFocusPainted(false);
        regresarButton.setFocusPainted(false);
        limpiarButton.setFocusPainted(false);
        mostrartodoButton.setFocusPainted(false);

        //Dataset de la gráfica
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesX);
        dataset.addSeries(seriesY);
        dataset.addSeries(seriesZ);

        //Creación de la gráfica
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Histórico de datos",
                "Tiempo",
                "Valor",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        //Grosor de las lineas
        chart.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
        chart.getXYPlot().getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
        chart.getXYPlot().getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));

        //Configuración de las fuentes y estilos de texto de la gráfica
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
        chart.getXYPlot().getDomainAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 14));
        chart.getXYPlot().getRangeAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 14));

        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        JPanelGrafica.setLayout(new BorderLayout());
        JPanelGrafica.add(chartPanel, BorderLayout.CENTER);

        //ActionListener del botón buscar
        buscarButton.addActionListener(e -> cargarDatosFiltrados());

        //ActionListener del botón para mostrar todos los datos
        mostrartodoButton.addActionListener(e -> cargarTodosLosDatos());

        //ActionListener del boton para regresar a la vista inicio
        regresarButton.addActionListener(e -> {
            Container parent = JPanelHistorico.getParent();
            CardLayout cl = (CardLayout) parent.getLayout();
            cl.show(parent, "Inicio");
        });

        //ActionListener del boton para limpiar la tabla
        limpiarButton.addActionListener(e -> limpiarSeries());
    }


    //Este metodo se utilizará en el boton "Buscar"
    private void cargarDatosFiltrados() {
        String fechaInicial = textFieldFechaInicial.getText().trim();
        String horaInicial = textFieldHoraInicial.getText().trim();
        String fechaFinal = textFieldFechaFinal.getText().trim();
        String horaFinal = textFieldHoraFinal.getText().trim();

        // Validaciones
        if (fechaInicial.isEmpty() && !fechaFinal.isEmpty()) {
            JOptionPane.showMessageDialog(JPanelHistorico, "Debe introducir una fecha inicial.");
            return;
        }
        if (!horaFinal.isEmpty() && fechaInicial.isEmpty() && fechaFinal.isEmpty()) {
            JOptionPane.showMessageDialog(JPanelHistorico, "Debe introducir fechas para usar hora final.");
            return;
        }
        if (!fechaFinal.isEmpty() && !horaFinal.isEmpty() && fechaInicial.isEmpty()) {
            JOptionPane.showMessageDialog(JPanelHistorico, "Debe introducir una fecha inicial.");
            return;
        }
        if (!horaInicial.isEmpty() && fechaInicial.isEmpty()) {
            JOptionPane.showMessageDialog(JPanelHistorico, "Debe introducir una fecha inicial para usar hora inicial.");
            return;
        }

        String filtro = "";


//Validación: Solo fecha inicial
        if (!fechaInicial.isEmpty() && fechaFinal.isEmpty() && horaInicial.isEmpty()) {
            filtro = "FROM " + fechaInicial;

//Validación: Fecha inicial y hora inicial
        } else if (!fechaInicial.isEmpty() && !horaInicial.isEmpty() && fechaFinal.isEmpty()) {
            filtro = "FROM " + fechaInicial + " " + normalizarHora(horaInicial);

//Validación: Fecha inicial y fecha final
        } else if (!fechaInicial.isEmpty() && !fechaFinal.isEmpty() && horaInicial.isEmpty() && horaFinal.isEmpty()) {
            filtro = "BETWEEN " + fechaInicial + " AND " + fechaFinal;

//Validación: fecha inicial, hora inicial y fecha final
        } else if (!fechaInicial.isEmpty() && !horaInicial.isEmpty() && !fechaFinal.isEmpty() && horaFinal.isEmpty()) {
            filtro = "BETWEEN " + fechaInicial + " " + normalizarHora(horaInicial) + " AND " + fechaFinal;

//Validación: fecha inicial, hora inicial, fecha final y hora final (Caso completo)
        } else if (!fechaInicial.isEmpty() && !horaInicial.isEmpty() && !fechaFinal.isEmpty() && !horaFinal.isEmpty()) {
            filtro = "BETWEEN " + fechaInicial + " " + normalizarHora(horaInicial) + " AND " + fechaFinal + " " + normalizarHora(horaFinal);
        }


        if (filtro.isEmpty()) {
            JOptionPane.showMessageDialog(JPanelHistorico, "Debe introducir al menos una fecha inicial.");
            return;
        }

        mostrarMensajeCarga(true);
        final String filtroFinal = filtro;

        //Este hilo se utiliza para consultar los datos al servidor
        new Thread(() -> {
            limpiarSeries();
            try {
                String datos = ClienteSocket.consultarDatos(filtroFinal);
                if (datos == null || datos.isEmpty()) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(JPanelHistorico, "No se encontraron datos para el filtro."));
                    return;
                }
                String[] registros = datos.split(";");
                int tiempo = 0;
                for (String reg : registros) {
                    if (!reg.isEmpty()) {
                        String[] valores = reg.split(",");
                        int x = Integer.parseInt(valores[0]);
                        int y = Integer.parseInt(valores[1]);
                        int z = Integer.parseInt(valores[2]);
                        final int t = tiempo;
                        SwingUtilities.invokeLater(() -> {
                            seriesX.add(t, x);
                            seriesY.add(t, y);
                            seriesZ.add(t, z);
                        });
                        tiempo++;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SwingUtilities.invokeLater(() -> mostrarMensajeCarga(false));
        }).start();
    }

    //En caso de que en una consulta con filtros no esten los segundos, este metodo los añade
    private String normalizarHora(String hora) {
        return hora.length() == 5 ? hora + ":00" : hora; // Si es HH:MM, añade segundos
    }

    //Este metodo se utilizara en el boton Mostrar todos los datos
    private void cargarTodosLosDatos() {
        mostrarMensajeCarga(true);

        //Este hilo tambien es para consultar los datos al servidor
        new Thread(() -> {
            limpiarSeries();
            try {
                String datos = ClienteSocket.consultarDatos(); //Este string obtiene todos los datos
                System.out.println("Datos recibidos: " + datos);

                //Verificar que haya datos
                if (datos == null || datos.isEmpty()) return;
                String[] registros = datos.split(";");
                int tiempo = 0;
                for (String reg : registros) {
                    //Revisar las iteraciones
                    if (!reg.isEmpty()) {
                        String[] valores = reg.split(",");
                        int x = Integer.parseInt(valores[0]);
                        int y = Integer.parseInt(valores[1]);
                        int z = Integer.parseInt(valores[2]);
                        final int t = tiempo;
                        SwingUtilities.invokeLater(() -> {
                            seriesX.add(t, x);
                            seriesY.add(t, y);
                            seriesZ.add(t, z);
                        });
                        tiempo++;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SwingUtilities.invokeLater(() -> mostrarMensajeCarga(false));
        }).start();
    }

    //Metodo para el boton limpiar
    private void limpiarSeries() {
        SwingUtilities.invokeLater(() -> {
            seriesX.clear();
            seriesY.clear();
            seriesZ.clear();
        });
    }

    private void mostrarMensajeCarga(boolean cargando) {
        CargandoLabel.setText(cargando ? "Cargando datos..." : "");
    }

    public JPanel getPanelHistorico() {
        return JPanelHistorico;
    }
}
