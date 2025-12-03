import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    private MainFrame mainFrame;

    public HomePanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new GridBagLayout());
        setBackground(AppStyle.BLANCO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // --- LOGO ---
        JLabel lblLogo = new JLabel();
        lblLogo.setPreferredSize(new Dimension(180, 150));
        //lblLogo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Borde temporal
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        // DESCOMENTAR Y PONER RUTA CUANDO TENGAS LA IMAGEN:
        try {
            java.net.URL imgURL = getClass().getResource("/buo.png");
            if (imgURL != null) {
                ImageIcon iconOriginal = new ImageIcon(imgURL);

                Image imagenEscalada = iconOriginal.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(imagenEscalada));
            } else {
                lblLogo.setText("Logo no encontrado");
            }
        } catch (Exception e) {
            lblLogo.setText("Error logo");
        }
        // lblLogo.setText("");
        add(lblLogo, gbc);

        // --- TITULO ---
        gbc.gridy++;
        JLabel lblTitulo = new JLabel("SISTEMA DE MONITOREO");
        lblTitulo.setFont(AppStyle.FONT_TITULO);
        lblTitulo.setForeground(AppStyle.AZUL_UNISON);
        add(lblTitulo, gbc);

        // --- AUTOR ---
        gbc.gridy++;
        JLabel lblAutor = new JLabel("Soto Cervantes José Antonio");
        lblAutor.setFont(AppStyle.FONT_TEXTO);
        add(lblAutor, gbc);

        // --- BOTONES ---
        gbc.gridy++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(AppStyle.BLANCO);

        BotonRedondeado btnMonitor = new BotonRedondeado("Monitor", AppStyle.AZUL_UNISON);
        btnMonitor.setPreferredSize(new Dimension(120, 40));
        btnMonitor.addActionListener(e -> mainFrame.showCard("MONITOR"));

        BotonRedondeado btnHistorico = new BotonRedondeado("Histórico", AppStyle.DORADO_UNISON);
        btnHistorico.setPreferredSize(new Dimension(120, 40));
        btnHistorico.addActionListener(e -> mainFrame.showCard("HISTORICO"));

        buttonPanel.add(btnMonitor);
        buttonPanel.add(btnHistorico);
        add(buttonPanel, gbc);
    }
}