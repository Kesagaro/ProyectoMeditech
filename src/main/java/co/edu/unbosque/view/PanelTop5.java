package co.edu.unbosque.view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

public class PanelTop5 extends JPanel {
    private PersonalButton atrasButton;
    private Tabla tabla;

    public PanelTop5() {
        super(new BorderLayout());
        atrasButton = new PersonalButton("Atrás", false);
        add(Tema.encabezado(atrasButton, "Top 5 de ventas", null), BorderLayout.NORTH);

        tabla = new Tabla(new String[] {"Puesto", "Medicamento", "Unidades vendidas"},
                new Class<?>[] {Integer.class, String.class, Integer.class});
        tabla.setMensajeVacio("Aún no hay órdenes registradas.");
        tabla.getTabla().getColumnModel().getColumn(0).setMaxWidth(90);

        TransparentPanel tarjeta = new TransparentPanel();
        tarjeta.setLayout(new BorderLayout());
        tarjeta.add(tabla);
        add(Tema.contenido(tarjeta), BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Tema.pintarFondo(g, this);
    }

    public PersonalButton getAtrasButton() {
        return atrasButton;
    }

    public void mostrarReporte(List<Object[]> ranking) {
        tabla.mostrar(ranking);
    }

    public Tabla getTabla() {
        return tabla;
    }
}
