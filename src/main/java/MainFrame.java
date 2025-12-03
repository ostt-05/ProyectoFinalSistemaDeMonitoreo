import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    // ... (El código del constructor y variables se mantiene igual)
    // Solo mostramos el cambio en el main y la estructura

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("Sistema de Monitoreo - UNISON");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Carga de icono y logo (Tu código existente)...
        java.net.URL imgURL = getClass().getResource("/buo.png");
        try {
            java.net.URL iconURL = getClass().getResource("/logo2.png");
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Pasamos 'this' para la navegación
        HomePanel homePanel = new HomePanel(this);
        MonitorPanel monitorPanel = new MonitorPanel(this);

        // Panel Histórico
        HistoryPanel historyPanel = new HistoryPanel(this);

        mainPanel.add(homePanel, "HOME");
        mainPanel.add(monitorPanel, "MONITOR");
        mainPanel.add(historyPanel, "HISTORICO");

        add(mainPanel);
    }

    public void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }

    // --- AQUÍ ESTÁ EL CAMBIO PRINCIPAL ---
    public static void main(String[] args) {
        // 1. Intentar conectar al servidor antes de nada
        boolean conectado = SocketClient.conectar();

        if (!conectado) {
            // Si falla, mostramos mensaje y cerramos
            JOptionPane.showMessageDialog(null,
                    "No se pudo establecer conexión con el Servidor.\nEl programa no puede iniciar.",
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // 2. Si conecta, añadimos un hook para desconectar al cerrar
        Runtime.getRuntime().addShutdownHook(new Thread(SocketClient::desconectar));

        // 3. Iniciamos la interfaz
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}