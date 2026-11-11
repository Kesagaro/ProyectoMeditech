package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;

public class PanelTop5 extends JPanel {
    private PersonalButton atrasButton;
    private TransparentPanel glassPanel;

    public PanelTop5() {
        setLayout(new BorderLayout());

        String reporteTop5 = generarReporteTop5();
        JTextArea textArea = new JTextArea(reporteTop5);
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

    private String generarReporteTop5() {
        
        return "Top 5 Medicamentos Más Expendidos:\n\n"
               + "Medicamento: Paracetamol - Cantidad expendida: 150\n"
               + "Medicamento: Ibuprofeno - Cantidad expendida: 120\n"
               + "Medicamento: Amoxicilina - Cantidad expendida: 100\n"
               + "Medicamento: Aspirina - Cantidad expendida: 90\n"
               + "Medicamento: Omeprazol - Cantidad expendida: 85\n";
    }
}