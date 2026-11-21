package co.edu.unbosque.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class PanelTurno extends JPanel {
	private JTextField cedulaField;
	private PersonalButton turnoButton;
	private PersonalButton atrasButton;
	private JLabel turnoLabel;
	private JLabel pacienteLabel;

	public PanelTurno() {
		super(new BorderLayout());
		atrasButton = new PersonalButton("Atrás", false);
		add(Tema.encabezado(atrasButton, "Turnos de atención", null), BorderLayout.NORTH);

		TransparentPanel tarjeta = new TransparentPanel();
		tarjeta.setLayout(new GridBagLayout());
		cedulaField = new JTextField(16);
		cedulaField.putClientProperty("JTextField.placeholderText", "Cédula del paciente");
		Tema.filaFormulario(tarjeta, 0, "Cédula", cedulaField);

		turnoButton = new PersonalButton("Generar turno");
		cedulaField.addActionListener(e -> turnoButton.doClick());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 1;
		c.insets = new Insets(14, 0, 18, 0);
		tarjeta.add(turnoButton, c);

		// número grande del último turno generado, en el morado del logo
		turnoLabel = new JLabel(" ", SwingConstants.CENTER);
		turnoLabel.setFont(Tema.fuente(Font.BOLD, 34));
		turnoLabel.setForeground(Tema.ACENTO);
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		tarjeta.add(turnoLabel, c);

		pacienteLabel = Tema.subtitulo(" ");
		pacienteLabel.setHorizontalAlignment(SwingConstants.CENTER);
		c.gridy = 3;
		tarjeta.add(pacienteLabel, c);

		add(Tema.centrado(tarjeta, 400), BorderLayout.CENTER);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Tema.pintarFondo(g, this);
	}

	public PersonalButton getTurnoButton() {
		return turnoButton;
	}

	public PersonalButton getAtrasButton() {
		return atrasButton;
	}

	public String getCedula() {
		return cedulaField.getText();
	}

	public void mostrarTurno(String turno, String cedula) {
		turnoLabel.setText(turno);
		pacienteLabel.setText("Paciente CC " + cedula);
		cedulaField.setText("");
	}
}
