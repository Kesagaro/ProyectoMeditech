package co.edu.unbosque.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** Tarjeta blanca casi opaca con esquinas redondeadas y una sombra suave, sobre el fondo verde. */
public class TransparentPanel extends JPanel {
    private static final int ARCO = 18;
    private static final int SOMBRA = 4;

    public TransparentPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(22, 26, 22 + SOMBRA, 26 + SOMBRA));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int ancho = getWidth() - SOMBRA;
        int alto = getHeight() - SOMBRA;
        g2d.setColor(new Color(0, 0, 0, 28));
        g2d.fillRoundRect(SOMBRA, SOMBRA, ancho, alto, ARCO, ARCO);
        g2d.setColor(new Color(255, 255, 255, 242));
        g2d.fillRoundRect(0, 0, ancho, alto, ARCO, ARCO);
        g2d.dispose();
        super.paintComponent(g);
    }
}
