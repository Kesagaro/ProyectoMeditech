package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;

public class PanelOrdenes extends JPanel {
	private TransparentPanel glassPanel;
	private JTextField documentoField;
	private JTextField entidadField;
	private JTextField nombreMedicamentoField;
	private JTextField cantidadField;
	private PersonalButton agregarButton;
	private PersonalButton verOrdenesButton;
	private JTextArea areaOrdenes;
	private PersonalButton atrasButton;

	public PanelOrdenes() {
		setLayout(null);

		glassPanel = new TransparentPanel();
		glassPanel.setLayout(null);
		glassPanel.setBounds(25, 50, 485, 390);

		JLabel documentoLabel = new JLabel("Documento del paciente:");
		documentoLabel.setBounds(15, 15, 190, 26);
		glassPanel.add(documentoLabel);

		documentoField = new JTextField();
		documentoField.setBounds(210, 15, 260, 26);
		glassPanel.add(documentoField);

		JLabel codigoEntidadLabel = new JLabel("Código de la entidad:");
		codigoEntidadLabel.setBounds(15, 50, 190, 26);
		glassPanel.add(codigoEntidadLabel);

		entidadField = new JTextField();
		entidadField.setBounds(210, 50, 260, 26);
		glassPanel.add(entidadField);

		JLabel nombreMedicamentoLabel = new JLabel("Nombre del medicamento:");
		nombreMedicamentoLabel.setBounds(15, 85, 190, 26);
		glassPanel.add(nombreMedicamentoLabel);

		nombreMedicamentoField = new JTextField();
		nombreMedicamentoField.setBounds(210, 85, 260, 26);
		glassPanel.add(nombreMedicamentoField);

		JLabel cantidadLabel = new JLabel("Cantidad:");
		cantidadLabel.setBounds(15, 120, 190, 26);
		glassPanel.add(cantidadLabel);

		cantidadField = new JTextField();
		cantidadField.setBounds(210, 120, 260, 26);
		glassPanel.add(cantidadField);

		agregarButton = new PersonalButton("Agregar orden");
		agregarButton.setBounds(15, 160, 220, 36);
		glassPanel.add(agregarButton);

		verOrdenesButton = new PersonalButton("Ver órdenes");
		verOrdenesButton.setBounds(250, 160, 220, 36);
		glassPanel.add(verOrdenesButton);

		areaOrdenes = new JTextArea();
		areaOrdenes.setEditable(false);
		areaOrdenes.setLineWrap(true);
		areaOrdenes.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(areaOrdenes);
		scrollPane.setBounds(15, 210, 455, 165);
		glassPanel.add(scrollPane);

		atrasButton = new PersonalButton("Atrás");
		atrasButton.setBounds(5, 5, 100, 40);
		add(atrasButton);

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