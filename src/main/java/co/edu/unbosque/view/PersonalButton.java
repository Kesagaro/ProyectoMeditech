package co.edu.unbosque.view;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PersonalButton extends JButton {
    private Color backgroundColor;
    private Color hoverColor;

    public PersonalButton(String text) {
        super(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        backgroundColor = new Color(0, 119, 182); // Color de fondo original
        hoverColor = new Color(254,245, 231); // Color al pasar el mouse
        setFont(new Font("Garlic", Font.BOLD, 18)); // Cambiar la fuente
        setForeground(Color.BLACK); // Color del texto blanco
        setPreferredSize(new Dimension(200, 40)); // Tamaño preferido del botón

        // Cambiar el color de fondo al pasar el mouse
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(backgroundColor);
            }
            
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(getBackground());
        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30)); // Bordes redondeados
        super.paintComponent(g);
    }

    @Override
    public void setBackground(Color color) {
        this.backgroundColor = color;
        super.setBackground(color);
    }
}