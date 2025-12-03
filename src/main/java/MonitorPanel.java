import com.fazecast.jSerialComm.SerialPort;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class MonitorPanel extends JPanel implements DataListener {
    private MainFrame mainFrame;
    private JComboBox<String> cmbPorts;
    private BotonRedondeado btnStartStop;
    private BotonRedondeado btnBack;

    // Gráficas
    private XYSeries seriesX, seriesY, seriesZ;
    private int timeIndex = 0;

    // Lógica
    private ArduinoManager arduinoManager;
    private SimulationThread simThread;
    private boolean isRunning = false;

    public MonitorPanel(MainFrame frame) {
        this.mainFrame = frame;
        this.arduinoManager = new ArduinoManager(this);

        setLayout(new BorderLayout());
        setBackground(AppStyle.BLANCO);

        initUI();
        initChart();
    }

    private void initChart() {
        seriesX = new XYSeries("X");
        seriesY = new XYSeries("Y");
        seriesZ = new XYSeries("Z");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesX);
        dataset.addSeries(seriesY);
        dataset.addSeries(seriesZ);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Sensores en Tiempo Real", "Tiempo (s)", "Valor",
                dataset, PlotOrientation.VERTICAL, true, true, false
        );

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Personalizar colores de líneas
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);   // X
        renderer.setSeriesPaint(1, Color.GREEN); // Y
        renderer.setSeriesPaint(2, Color.BLUE);  // Z
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
    }

    private void initUI() {
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(AppStyle.BLANCO);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnBack = new BotonRedondeado("Atrás", Color.GRAY);
        btnBack.setPreferredSize(new Dimension(80, 30));
        btnBack.addActionListener(e -> stopAndGoBack());

        // ComboBox de Puertos
        cmbPorts = new JComboBox<>();
        cmbPorts.setBackground(Color.WHITE);
        cmbPorts.addItem("Simulación");

        JButton btnRefresh = new JButton("↻"); // Botón pequeño para refrescar
        btnRefresh.addActionListener(e -> refreshPorts());

        btnStartStop = new BotonRedondeado("Iniciar", AppStyle.AZUL_UNISON);
        btnStartStop.setPreferredSize(new Dimension(100, 30));
        btnStartStop.addActionListener(e -> toggleMonitoring());

        controlPanel.add(btnBack);
        controlPanel.add(new JLabel("Puerto:"));
        controlPanel.add(cmbPorts);
        controlPanel.add(btnRefresh);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(btnStartStop);

        add(controlPanel, BorderLayout.SOUTH);
        refreshPorts();
    }

    private void refreshPorts() {
        cmbPorts.removeAllItems();
        cmbPorts.addItem("Simulación");
        SerialPort[] ports = arduinoManager.getAvailablePorts();
        for (SerialPort p : ports) {
            cmbPorts.addItem(p.getSystemPortName());
        }
    }

    private void toggleMonitoring() {
        if (!isRunning) {
            startMonitoring();
        } else {
            stopMonitoring();
        }
    }

    private void startMonitoring() {
        String selected = (String) cmbPorts.getSelectedItem();
        timeIndex = 0;
        seriesX.clear(); seriesY.clear(); seriesZ.clear();

        if ("Simulación".equals(selected)) {
            simThread = new SimulationThread(this);
            simThread.start();
            setRunningState(true);
        } else {
            if (arduinoManager.connect(selected)) {
                setRunningState(true);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo conectar al puerto.");
            }
        }
    }

    private void stopMonitoring() {
        if (simThread != null && simThread.isAlive()) {
            simThread.stopSimulation();
        }
        arduinoManager.disconnect();
        setRunningState(false);
    }

    private void setRunningState(boolean running) {
        this.isRunning = running;
        btnStartStop.setText(running ? "Detener" : "Iniciar");
        btnStartStop.setColorFondo(running ? Color.RED : AppStyle.AZUL_UNISON);
        cmbPorts.setEnabled(!running);
    }

    private void stopAndGoBack() {
        stopMonitoring();
        mainFrame.showCard("HOME");
    }

    @Override
    public void onDataReceived(int x, int y, int z) {
        // 1. Actualizar la gráfica en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            seriesX.add(timeIndex, x);
            seriesY.add(timeIndex, y);
            seriesZ.add(timeIndex, z);
            timeIndex++;
        });
        SocketClient.enviarDatosSensor(x, y, z);
    }

    @Override
    public void onError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
        stopMonitoring();
    }
}