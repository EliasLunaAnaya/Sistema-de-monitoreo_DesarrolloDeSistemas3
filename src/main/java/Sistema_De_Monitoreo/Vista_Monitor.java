
package Sistema_De_Monitoreo;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.fazecast.jSerialComm.SerialPort;

public class Vista_Monitor {

    //Componentes de la interfaz
    private JPanel JPanel_Monitor;
    private JLabel TextMonitorEnTiempoReal;
    private JComboBox<String> ComboBoxCOM;
    private JPanel JPanelGrafica;
    private JButton IniDetButton;
    private JButton regresarButton;
    private JButton limpiarButton;
    private SerialPort puertoActivo;

    //Variables series para la gráfica
    private XYSeries seriesX = new XYSeries("X");
    private XYSeries seriesY = new XYSeries("Y");
    private XYSeries seriesZ = new XYSeries("Z");

    //Otras variables...
    private AtomicBoolean leyendo = new AtomicBoolean(false);
    private Thread hiloLectura;
    private ExecutorService envioExecutor = Executors.newSingleThreadExecutor();
    private BlockingQueue<int[]> colaEnvio = new LinkedBlockingQueue<>();

    //Constructor
    public Vista_Monitor() {
        JPanel_Monitor = new JPanel(new BorderLayout());

        //Borde azul en la interfaz
        JPanel_Monitor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x00529e), 14), // borde azul Unison
                BorderFactory.createEmptyBorder(20, 20, 20, 20) // espacio interno
        ));

        JPanel_Monitor.setBackground(Color.WHITE); //Fondo blanco en la interfaz

        //Bordes redondeados y con color azul fuerte a los botones
        IniDetButton.setBorder(new javax.swing.border.LineBorder(new Color(1,82,148), 7, true));
        regresarButton.setBorder(new javax.swing.border.LineBorder(new Color(1,82,148), 7, true));
        limpiarButton.setBorder(new javax.swing.border.LineBorder(new Color(1,82,148), 7, true));

        //Ajustar tamaño de los botones
        IniDetButton.setPreferredSize(new Dimension(100, 35));
        regresarButton.setPreferredSize(new Dimension(100, 35));
        limpiarButton.setPreferredSize(new Dimension(100, 35));

        //Fondos blancos en los botones
        IniDetButton.setBackground(Color.WHITE);
        regresarButton.setBackground(Color.WHITE);
        limpiarButton.setBackground(Color.WHITE);

        IniDetButton.setFocusPainted(false);
        regresarButton.setFocusPainted(false);
        limpiarButton.setFocusPainted(false);

        //La parte de arriba de la interfaz
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ComboBoxCOM = new JComboBox<>();

        topPanel.add(TextMonitorEnTiempoReal);
        topPanel.add(ComboBoxCOM);

        //La parte de abajo de la interfaz
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(IniDetButton);
        bottomPanel.add(limpiarButton);
        bottomPanel.add(regresarButton);

        JPanelGrafica = new JPanel(new BorderLayout()); //El panel de la grafica

        XYSeriesCollection dataset = new XYSeriesCollection(); //Dataset de la gráfica

        //Agregar las series x, y, z en el dataset
        dataset.addSeries(seriesX);
        dataset.addSeries(seriesY);
        dataset.addSeries(seriesZ);

        //Creacion de la gráfica
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Datos en tiempo real",
                "Tiempo (s)",
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

        //Añadir la gráfica a la interfaz
        JPanelGrafica.add(chartPanel, BorderLayout.CENTER);

        //Añadir los paneles a la interfaz
        JPanel_Monitor.add(topPanel, BorderLayout.NORTH);
        JPanel_Monitor.add(JPanelGrafica, BorderLayout.CENTER);
        JPanel_Monitor.add(bottomPanel, BorderLayout.SOUTH);

        //ActionListener del boton para iniciar y detener
        IniDetButton.addActionListener(e -> {
            //Si no se están leyendo datos...
            if (!leyendo.get()) {
                String nombrePuerto = (String) ComboBoxCOM.getSelectedItem();
                if (nombrePuerto == null) {
                    JOptionPane.showMessageDialog(JPanel_Monitor, "Seleccione un puerto COM antes de iniciar.");
                    return;
                }
                iniciarLectura(nombrePuerto);
                IniDetButton.setText("Detener");
            } else {
                detenerLectura();
                IniDetButton.setText("Iniciar");
            }
        });

        //ActionListener del botón para regresar a la vista inicio
        regresarButton.addActionListener(e -> {
            Container parent = JPanel_Monitor.getParent();
            CardLayout cl = (CardLayout) parent.getLayout();
            cl.show(parent, "Inicio");
        });

        //ActionListener del botón para borrar lo que se ha hecho en la gráfica
        limpiarButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                seriesX.clear();
                seriesY.clear();
                seriesZ.clear();
            });
        });


        //Este hilo envia datos en segundo plano, evita congelar la interfaz
        envioExecutor.submit(() -> {
            while (true) { //Este bucle se realizará indefinidamente mientras corra el programa
                try {
                    //Bloquea hasta que haya datos en la cola
                    //colaEnvio es una LinkedBlockingQueue donde se guardan los datos del Arduino
                    int[] datos = colaEnvio.take();
                    // Envía los datos al servidor usando el socket
                    // datos[0] = valor X, datos[1] = valor Y, datos[2] = valor Z
                    ClienteSocket.enviarDatos(datos[0], datos[1], datos[2]);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        });
    }

    private void iniciarLectura(String nombrePuerto) {
        puertoActivo = SerialPort.getCommPort(nombrePuerto); //Obtener el objeto SerialPort correspondiente al puerto COM
        puertoActivo.setBaudRate(9600); //Velocidad de transmisión del arduino
        puertoActivo.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0); //Timeout para la lectura, espera minimo 1 segundo para continuar

        if (!puertoActivo.openPort()) {
            JOptionPane.showMessageDialog(JPanel_Monitor, "No se pudo abrir el puerto.");
            return;
        }

        leyendo.set(true);

        //Este hilo lee los datos del arduino
        hiloLectura = new Thread(() -> {
            try {
                Thread.sleep(2000); //Tiempo para estabilizar Arduino
            } catch (InterruptedException ignored) {}

            Scanner scanner = new Scanner(puertoActivo.getInputStream()); //Scanner para leer datos del flujo de entrada del puerto serial
            int tiempo = 0;
            long ultimoDato = System.currentTimeMillis();

            while (leyendo.get()) {
                if (!scanner.hasNextLine()) {
                    if (System.currentTimeMillis() - ultimoDato > 5000) {
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(JPanel_Monitor, "No se reciben datos. Deteniendo lectura."));
                        detenerLectura();
                        break;
                    }
                    continue;
                }

                String linea = scanner.nextLine().trim();
                try {
                    //Validar el formato xyz del arduino
                    if (!linea.startsWith("x:") || !linea.contains(",y:") || !linea.contains(",z:")) {
                        continue;
                    }
                    String[] partes = linea.split(",");
                    int x = Integer.parseInt(partes[0].replace("x:", "").trim());
                    int y = Integer.parseInt(partes[1].replace("y:", "").trim());
                    int z = Integer.parseInt(partes[2].replace("z:", "").trim());

                    final int t = tiempo;
                    SwingUtilities.invokeLater(() -> {
                        if (seriesX.getItemCount() > 100) { //Limitar puntos
                            seriesX.remove(0);
                            seriesY.remove(0);
                            seriesZ.remove(0);
                        }
                        seriesX.add(t, x);
                        seriesY.add(t, y);
                        seriesZ.add(t, z);
                        TextMonitorEnTiempoReal.setText(linea);
                    });

                    colaEnvio.offer(new int[]{x, y, z});
                    tiempo++;
                    ultimoDato = System.currentTimeMillis();
                } catch (Exception ex) {
                    System.out.println("Error procesando línea: " + linea + "  " + ex);
                }
            }
            puertoActivo.closePort();
        });
        hiloLectura.start();
    }

    private void detenerLectura() {
        leyendo.set(false);
        if (puertoActivo != null && puertoActivo.isOpen()) {
            puertoActivo.closePort();
        }
    }

    public void cargarPuertos() {
        SerialPort[] puertos = SerialPort.getCommPorts();
        ComboBoxCOM.removeAllItems();
        for (SerialPort puerto : puertos) {
            ComboBoxCOM.addItem(puerto.getSystemPortName());
        }
    }

    public JPanel getPanelMonitor() {
        cargarPuertos();
        return JPanel_Monitor;
    }


}
