package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;

public class PanelMedicina extends JPanel {
	private TransparentPanel glassPanel;
	private JTextField buscarField;
	private PersonalButton buscarButton;
	private PersonalButton atrasButton;
	private JTextArea areaResultados;

	public PanelMedicina() {
		setLayout(null);

		glassPanel = new TransparentPanel();
		glassPanel.setLayout(null);
		glassPanel.setBounds(25, 50, 485, 390);

		JLabel tituloLabel = new JLabel("Buscar medicamento", SwingConstants.CENTER);
		tituloLabel.setFont(new Font("Arial", Font.BOLD, 20));
		tituloLabel.setBounds(15, 15, 455, 30);
		glassPanel.add(tituloLabel);

		JLabel nombreLabel = new JLabel("Nombre:");
		nombreLabel.setBounds(15, 60, 70, 30);
		glassPanel.add(nombreLabel);

		buscarField = new JTextField();
		buscarField.setBounds(85, 60, 250, 30);
		glassPanel.add(buscarField);

		buscarButton = new PersonalButton("Buscar");
		buscarButton.setFont(new Font("Garlic", Font.BOLD, 14));
		buscarButton.setBounds(350, 59, 120, 32);
		glassPanel.add(buscarButton);

		areaResultados = new JTextArea();
		areaResultados.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(areaResultados);
		scrollPane.setBounds(15, 105, 455, 270);
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

	public JTextField getBuscarField() {
		return buscarField;
	}

	public PersonalButton getBuscarButton() {
		return buscarButton;
	}

	public PersonalButton getAtrasButton() {
		return atrasButton;
	}

	public JTextArea getAreaResultados() {
		return areaResultados;
	}
}
