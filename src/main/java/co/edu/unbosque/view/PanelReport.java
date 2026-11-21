package co.edu.unbosque.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class PanelReport extends JPanel {
    private PersonalButton atrasButton;
    private PersonalButton top5Button;
    private PersonalButton ordenesButton;

    public PanelReport() {
        super(new BorderLayout());
        atrasButton = new PersonalButton("Atrás", false);
        add(Tema.encabezado(atrasButton, "Reportes", null), BorderLayout.NORTH);

        top5Button = new PersonalButton("<html><center><span style='font-size:13pt'>Top ventas</span><br>"
                + "<span style='font-size:9pt;font-weight:normal'>Los 5 medicamentos más vendidos</span></center></html>");
        ordenesButton = new PersonalButton("<html><center><span style='font-size:13pt'>Órdenes</span><br>"
                + "<span style='font-size:9pt;font-weight:normal'>Registrar y consultar órdenes</span></center></html>");
        TransparentPanel tarjeta = new TransparentPanel();
        tarjeta.setLayout(new GridLayout(1, 2, 16, 16));
        for (PersonalButton boton : new PersonalButton[] {top5Button, ordenesButton}) {
            boton.setPreferredSize(new Dimension(220, 92));
            tarjeta.add(boton);
        }
        add(Tema.centrado(tarjeta, 520), BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Tema.pintarFondo(g, this);
    }

    public PersonalButton getAtrasButton() {
        return atrasButton;
    }

    public PersonalButton getOrdenesButton() {
        return ordenesButton;
    }

    public PersonalButton getTop5Button() {
        return top5Button;
    }
}
