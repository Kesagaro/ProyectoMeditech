package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;

public class PanelUsuarios extends JPanel {
	private TransparentPanel glassPanel;
	private JTextArea areaCliente;
	private JTextField documentoField;
	private JTextField entidadField;
	private JTextField nombreMedicamentoField;
	private JTextField cantidadField;
	private PersonalButton agregarButton;
	private PersonalButton verOrdenesButton;
	private JTextArea areaOrdenes;
	private PersonalButton atrasButton;

	public PanelUsuarios() {
		setLayout(null);

		glassPanel = new TransparentPanel();
		glassPanel.setLayout(null);
		glassPanel.setBounds(100, 50, 350, 380);

		JLabel documentoLabel = new JLabel("Documento del paciente:");
		documentoLabel.setBounds(10, 20, 200, 25);
		glassPanel.add(documentoLabel);

		documentoField = new JTextField();
		documentoField.setBounds(180, 20, 150, 25);
		glassPanel.add(documentoField);

		JLabel codigoEntidadLabel = new JLabel("Código de la entidad:");
		codigoEntidadLabel.setBounds(10, 60, 200, 25);
		glassPanel.add(codigoEntidadLabel);

		entidadField = new JTextField();
		entidadField.setBounds(180, 60, 150, 25);
		glassPanel.add(entidadField);

		JLabel nombreMedicamentoLabel = new JLabel("Nombre del medicamento:");
		nombreMedicamentoLabel.setBounds(10, 100, 200, 25);
		glassPanel.add(nombreMedicamentoLabel);

		nombreMedicamentoField = new JTextField();
		nombreMedicamentoField.setBounds(180, 100, 150, 25);
		glassPanel.add(nombreMedicamentoField);

		JLabel cantidadLabel = new JLabel("Cantidad:");
		cantidadLabel.setBounds(10, 140, 200, 25);
		glassPanel.add(cantidadLabel);

		cantidadField = new JTextField();
		cantidadField.setBounds(180, 140, 150, 25);
		glassPanel.add(cantidadField);

		agregarButton = new PersonalButton("Agregar Orden");
		agregarButton.setBounds(10, 180, 150, 25);
		glassPanel.add(agregarButton);

		verOrdenesButton = new PersonalButton("Ver Órdenes");
		verOrdenesButton.setBounds(180, 180, 150, 25);
		glassPanel.add(verOrdenesButton);

		areaOrdenes = new JTextArea();
		areaOrdenes.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(areaOrdenes);
		scrollPane.setBounds(10, 220, 320, 100);
		glassPanel.add(scrollPane);

		atrasButton = new PersonalButton("Atrás");
		atrasButton.setBounds(75, 330, 160, 40);
		glassPanel.add(atrasButton);

		add(glassPanel);
	}

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

	public PersonalButton getAgregarOrdenButton() {
		return agregarButton;
	}

	public PersonalButton getVerOrdenesButton() {
		return verOrdenesButton;
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

	public JTextArea getAreaOrdenes() {
		return areaOrdenes;
	}

}