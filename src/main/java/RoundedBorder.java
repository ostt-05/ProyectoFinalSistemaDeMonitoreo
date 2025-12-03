import javax.swing.JButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Cursor;

class BotonRedondeado extends JButton {
    private Color colorFondo;

    public BotonRedondeado(String texto, Color colorFondo) {
        super(texto);
        this.colorFondo = colorFondo;
        setFont(AppStyle.FONT_BOTON);
        setForeground(Color.WHITE);
        setContentAreaFilled(false); // Importante para quitar el fondo default
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void setColorFondo(Color color) {
        this.colorFondo = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Antialiasing para bordes suaves
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pintar fondo redondeado
        g2.setColor(colorFondo);
        // Radio de 8px de diámetro = 4px de radio en las esquinas
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

        g2.dispose();

        // Pintar el texto encima
        super.paintComponent(g);
    }
}