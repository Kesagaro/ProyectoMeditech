package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;

public class PanelTop5 extends JPanel {
    private PersonalButton atrasButton;
    private TransparentPanel glassPanel;
    private JTextArea textArea;

    public PanelTop5() {
        setLayout(null);

        glassPanel = new TransparentPanel();
        glassPanel.setLayout(null);
        glassPanel.setBounds(25, 50, 485, 390);

        JLabel tituloLabel = new JLabel("Top ventas", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 20));
        tituloLabel.setBounds(15, 15, 455, 30);
        glassPanel.add(tituloLabel);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 15));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(15, 60, 455, 315);
        glassPanel.add(scrollPane);

        atrasButton = new PersonalButton("Atrás");
        atrasButton.setBounds(5, 5, 100, 40);
        add(atrasButton);

        add(glassPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gradient = new GradientPaint(0, 0, new Color(107, 142, 78), 0, getHeight(),
                new Color(178, 197, 178));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    public PersonalButton getAtrasButton() {
        return atrasButton;
    }

    public void mostrarReporte(String reporte) {
        textArea.setText(reporte);
        textArea.setCaretPosition(0);
    }
}