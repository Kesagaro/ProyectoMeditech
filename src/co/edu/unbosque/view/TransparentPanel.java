package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class TransparentPanel extends JPanel {
    private Color backgroundColor;

    public TransparentPanel() {
        this.backgroundColor = new Color(255, 255, 255, 150); // Color blanco con transparencia
        setOpaque(false); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        
        g2d.setColor(new Color(255, 255, 255, 100)); 
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); 
    }
}