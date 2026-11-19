package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;

public class PanelMenu extends JPanel {
	private TransparentPanel glassPanel;
	private PersonalButton inventarioButton;
	private PersonalButton turnoButton;
	private PersonalButton funcionariosButton;
	private PersonalButton mediButton;
	private PersonalButton reporButton;
	private PersonalButton atrasButton;
	private JLabel sesionLabel;

	public PanelMenu() {
		setLayout(null);

		glassPanel = new TransparentPanel();
		glassPanel.setLayout(null);
		glassPanel.setBounds(70, 50, 400, 300);

		sesionLabel = new JLabel("", SwingConstants.CENTER);
		sesionLabel.setFont(new Font("Arial", Font.BOLD, 16));
		sesionLabel.setBounds(15, 20, 370, 30);
		glassPanel.add(sesionLabel);

		inventarioButton = new PersonalButton("Inventario");
		inventarioButton.setBounds(15, 125, 180, 36);
		glassPanel.add(inventarioButton);

		turnoButton = new PersonalButton("Turno");
		turnoButton.setBounds(110, 75, 180, 36);
		glassPanel.add(turnoButton);

		funcionariosButton = new PersonalButton("Funcionarios");
		funcionariosButton.setBounds(205, 125, 180, 36);
		glassPanel.add(funcionariosButton);

		mediButton = new PersonalButton("Medicina");
		mediButton.setBounds(15, 175, 180, 36);
		glassPanel.add(mediButton);

		atrasButton = new PersonalButton("Atrás");
		atrasButton.setBounds(5, 5, 100, 40);
		add(atrasButton);

		reporButton = new PersonalButton("Reportes");
		reporButton.setBounds(205, 175, 180, 36);
		glassPanel.add(reporButton);

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

	/** Muestra quién inició sesión; con {@code null} se borra al cerrar la sesión. */
	public void mostrarFuncionario(String nombre) {
		sesionLabel.setText(nombre == null ? "" : "Sesión: " + nombre);
	}

	public PersonalButton getInventarioButton() {
		return inventarioButton;
	}

	public PersonalButton getAtrasButton() {
		return atrasButton;
	}

	public PersonalButton getTurnoButton() {
		return turnoButton;
	}

	public PersonalButton getReporButton() {
		return reporButton;
	}

	public PersonalButton getFuncionariosButton() {
		return funcionariosButton;
	}

	public PersonalButton getMediButton() {
		return mediButton;
	}
}
