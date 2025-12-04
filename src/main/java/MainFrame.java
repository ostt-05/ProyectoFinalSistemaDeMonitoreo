import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {


    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("Sistema de Monitoreo - UNISON");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


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


        HomePanel homePanel = new HomePanel(this);
        MonitorPanel monitorPanel = new MonitorPanel(this);


        HistoryPanel historyPanel = new HistoryPanel(this);

        mainPanel.add(homePanel, "HOME");
        mainPanel.add(monitorPanel, "MONITOR");
        mainPanel.add(historyPanel, "HISTORICO");

        add(mainPanel);
    }

    public void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }


    public static void main(String[] args) {

        boolean conectado = SocketClient.conectar();

        if (!conectado) {

            JOptionPane.showMessageDialog(null,
                    "No se pudo establecer conexión con el Servidor.\nEl programa no puede iniciar.",
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }


        Runtime.getRuntime().addShutdownHook(new Thread(SocketClient::desconectar));


        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}