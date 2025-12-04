import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryPanel extends JPanel {
    private MainFrame mainFrame;
    private JSpinner dateSpinner;
    private JComboBox<String> timeComboBox;
    private BotonRedondeado btnConsultar;
    private JLabel lblStatus;


    private XYSeries seriesX, seriesY, seriesZ;
    private ChartPanel chartPanel;

    public HistoryPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(AppStyle.BLANCO);

        initTopPanel();
        initChart();
        initBottomPanel();
    }

    private void initTopPanel() {
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filtersPanel.setBackground(AppStyle.BLANCO);
        filtersPanel.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));

        // 1. Date Picker
        JLabel lblFecha = new JLabel("Fecha:");
        SpinnerDateModel model = new SpinnerDateModel();
        dateSpinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        dateSpinner.setPreferredSize(new Dimension(120, 30));

        // 2. Combo de Hora
        JLabel lblHora = new JLabel("Hora:");
        timeComboBox = new JComboBox<>();
        generarIntervalosTiempo();
        timeComboBox.setBackground(Color.WHITE);
        timeComboBox.setPreferredSize(new Dimension(100, 30));

        // 3. Botón Consultar
        btnConsultar = new BotonRedondeado("Consultar", AppStyle.AMARILLO_UNISOL);
        btnConsultar.setPreferredSize(new Dimension(100, 30));
        btnConsultar.addActionListener(e -> cargarDatos());

        // 4. Status
        lblStatus = new JLabel("");
        lblStatus.setForeground(Color.GRAY);

        filtersPanel.add(lblFecha);
        filtersPanel.add(dateSpinner);
        filtersPanel.add(lblHora);
        filtersPanel.add(timeComboBox);
        filtersPanel.add(btnConsultar);
        filtersPanel.add(lblStatus);

        add(filtersPanel, BorderLayout.NORTH);
    }

    // --- NUEVO MÉTODO PARA EL BOTÓN DE ATRÁS ---
    private void initBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        bottomPanel.setBackground(AppStyle.BLANCO);

        BotonRedondeado btnAtras = new BotonRedondeado("Atrás", Color.GRAY);
        btnAtras.setPreferredSize(new Dimension(80, 30));

        // Acción para volver al HOME
        btnAtras.addActionListener(e -> mainFrame.showCard("HOME"));

        bottomPanel.add(btnAtras);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    // -------------------------------------------

    private void generarIntervalosTiempo() {
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 30) {
                String hora = String.format("%02d:%02d", h, m);
                timeComboBox.addItem(hora);
            }
        }
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
                "Histórico de Sensores", "Registro", "Valor",
                dataset, PlotOrientation.VERTICAL, true, true, false
        );

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        plot.getRenderer().setSeriesPaint(0, Color.RED);
        plot.getRenderer().setSeriesPaint(1, Color.GREEN);
        plot.getRenderer().setSeriesPaint(2, Color.BLUE);

        chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
    }

    private void cargarDatos() {
        btnConsultar.setEnabled(false);
        lblStatus.setText("Cargando...");

        Date fechaDate = (Date) dateSpinner.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaStr = sdf.format(fechaDate);

        String horaInicio = (String) timeComboBox.getSelectedItem();
        String horaFin = calcularHoraFin(horaInicio);
        String horaInicioFull = horaInicio + ":00";

        new Thread(() -> {
            String respuesta = SocketClient.pedirHistorial(fechaStr, horaInicioFull, horaFin);

            SwingUtilities.invokeLater(() -> {
                procesarRespuesta(respuesta);
                btnConsultar.setEnabled(true);
            });
        }).start();
    }

    private String calcularHoraFin(String horaInicio) {
        String[] partes = horaInicio.split(":");
        int h = Integer.parseInt(partes[0]);
        int m = Integer.parseInt(partes[1]);
        m += 29;
        return String.format("%02d:%02d:59", h, m);
    }

    private void procesarRespuesta(String respuesta) {
        seriesX.clear();
        seriesY.clear();
        seriesZ.clear();

        if (respuesta == null || respuesta.equals("NO_DATA") || respuesta.isEmpty()) {
            lblStatus.setText("Sin datos.");
            JOptionPane.showMessageDialog(this, "No hay registros para este horario.");
            return;
        }

        try {
            String[] registros = respuesta.split(";");
            int contador = 0;

            for (String reg : registros) {
                String[] valores = reg.split(":");
                if (valores.length >= 3) {
                    double x = Double.parseDouble(valores[0]);
                    double y = Double.parseDouble(valores[1]);
                    double z = Double.parseDouble(valores[2]);

                    seriesX.add(contador, x);
                    seriesY.add(contador, y);
                    seriesZ.add(contador, z);
                    contador++;
                }
            }
            lblStatus.setText("Registros: " + contador);

        } catch (Exception e) {
            lblStatus.setText("Error procesando.");
            e.printStackTrace();
        }
    }
}