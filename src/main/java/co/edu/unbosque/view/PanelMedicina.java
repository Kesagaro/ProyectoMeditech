package co.edu.unbosque.view;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PanelMedicina extends JPanel {
	private JTextField buscarField;
	private PersonalButton buscarButton;
	private PersonalButton atrasButton;
	private Tabla tabla;

	public PanelMedicina() {
		super(new BorderLayout());
		atrasButton = new PersonalButton("Atrás", false);
		add(Tema.encabezado(atrasButton, "Buscar medicamento", null), BorderLayout.NORTH);

		buscarField = new JTextField();
		buscarField.putClientProperty("JTextField.placeholderText", "Nombre o parte del nombre");
		buscarField.putClientProperty("JTextField.showClearButton", true);
		buscarButton = new PersonalButton("Buscar");
		JPanel barra = new JPanel(new BorderLayout(10, 0));
		barra.setOpaque(false);
		barra.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
		barra.add(buscarField, BorderLayout.CENTER);
		barra.add(buscarButton, BorderLayout.EAST);

		tabla = new Tabla(new String[] {"ID", "Nombre", "Empresa", "Precio", "Stock"},
				new Class<?>[] {Long.class, String.class, String.class, Double.class, Integer.class});
		tabla.setMensajeVacio("Escriba un nombre y pulse Buscar.");
		tabla.anchos(60, 260, 180, 100, 80);

		TransparentPanel tarjeta = new TransparentPanel();
		tarjeta.setLayout(new BorderLayout());
		tarjeta.add(barra, BorderLayout.NORTH);
		tarjeta.add(tabla, BorderLayout.CENTER);
		add(Tema.contenido(tarjeta), BorderLayout.CENTER);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Tema.pintarFondo(g, this);
	}

	public JTextField getBuscarField() {
		return buscarField;
	}

	public PersonalButton getBuscarButton() {
		return buscarButton;
	}

	public PersonalButton getAtrasButton() {
		return atrasButton;
	}

	public Tabla getTabla() {
		return tabla;
	}
}
