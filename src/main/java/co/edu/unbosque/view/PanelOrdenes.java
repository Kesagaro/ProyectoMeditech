package co.edu.unbosque.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PanelOrdenes extends JPanel {
	private JTextField documentoField;
	private JTextField entidadField;
	private JTextField nombreMedicamentoField;
	private JTextField cantidadField;
	private PersonalButton agregarButton;
	private PersonalButton atrasButton;
	private Tabla tabla;

	public PanelOrdenes() {
		super(new BorderLayout());
		atrasButton = new PersonalButton("Atrás", false);
		add(Tema.encabezado(atrasButton, "Órdenes de pacientes", null), BorderLayout.NORTH);

		TransparentPanel formulario = new TransparentPanel();
		formulario.setLayout(new GridBagLayout());
		documentoField = new JTextField(12);
		entidadField = new JTextField(12);
		entidadField.putClientProperty("JTextField.placeholderText", "Ej.: EPS1");
		nombreMedicamentoField = new JTextField(12);
		cantidadField = new JTextField(12);
		Tema.filaFormulario(formulario, 0, "Documento", documentoField);
		Tema.filaFormulario(formulario, 1, "Entidad", entidadField);
		Tema.filaFormulario(formulario, 2, "Medicamento", nombreMedicamentoField);
		Tema.filaFormulario(formulario, 3, "Cantidad", cantidadField);

		agregarButton = new PersonalButton("Registrar orden");
		cantidadField.addActionListener(e -> agregarButton.doClick());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 4;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(14, 0, 0, 0);
		formulario.add(agregarButton, c);
		c.gridy = 5;
		c.weighty = 1;
		formulario.add(Box.createVerticalGlue(), c);
		formulario.setPreferredSize(new Dimension(290, formulario.getPreferredSize().height));

		tabla = new Tabla(new String[] {"Fecha", "Documento", "Entidad", "Medicamento", "Cant.", "Total"},
				new Class<?>[] {LocalDateTime.class, String.class, String.class, String.class, Integer.class, Double.class});
		tabla.setMensajeVacio("No hay órdenes registradas.");
		tabla.anchos(125, 100, 95, 160, 50, 85);
		TransparentPanel listado = new TransparentPanel();
		listado.setLayout(new BorderLayout());
		listado.add(tabla);

		JPanel centro = new JPanel(new BorderLayout(16, 0));
		centro.setOpaque(false);
		centro.add(formulario, BorderLayout.WEST);
		centro.add(listado, BorderLayout.CENTER);
		add(Tema.contenido(centro), BorderLayout.CENTER);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Tema.pintarFondo(g, this);
	}

	public PersonalButton getAtrasButton() {
		return atrasButton;
	}

	public PersonalButton getAgregarOrdenButton() {
		return agregarButton;
	}

	public JTextField getDocumentoField() {
		return documentoField;
	}

	public JTextField getCodigoEntidadField() {
		return entidadField;
	}

	public JTextField getNombreMedicamentoField() {
		return nombreMedicamentoField;
	}

	public JTextField getCantidadField() {
		return cantidadField;
	}

	public Tabla getTabla() {
		return tabla;
	}
}
