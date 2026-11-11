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

	public PanelMenu() {
		setLayout(null);

		glassPanel = new TransparentPanel();
		glassPanel.setLayout(null);
		glassPanel.setBounds(100, 50, 300, 300);

		inventarioButton = new PersonalButton("Inventario");
		inventarioButton.setBounds(5, 120, 140, 30);
		glassPanel.add(inventarioButton);

		turnoButton = new PersonalButton("Turno");
		turnoButton.setBounds(80, 80, 140, 30);
		glassPanel.add(turnoButton);

		funcionariosButton = new PersonalButton("Funcionarios");
		funcionariosButton.setBounds(155, 120, 140, 30);
		glassPanel.add(funcionariosButton);

		mediButton = new PersonalButton("Medicina");
		mediButton.setBounds(5, 160, 140, 30);
		glassPanel.add(mediButton);

		atrasButton = new PersonalButton("Atrás");
		atrasButton.setBounds(5, 5, 100, 40);
		add(atrasButton);

		reporButton = new PersonalButton("Reportes");
		reporButton.setBounds(155, 160, 140, 30);
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
}
