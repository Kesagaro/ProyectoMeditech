package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;

public class PanelTop5 extends JPanel {
    private PersonalButton atrasButton;
    private TransparentPanel glassPanel;
    private JTextArea textArea;

    public PanelTop5() {
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        
        atrasButton = new PersonalButton("Atrás");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(atrasButton);

        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public PersonalButton getAtrasButton() {
        return atrasButton;
    }

    public void mostrarReporte(String reporte) {
        textArea.setText(reporte);
        textArea.setCaretPosition(0);
    }
}