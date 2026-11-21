package co.edu.unbosque.view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class PanelRegistro extends JPanel {
	private JTextField cedulaField;
	private JTextField nombreField;
	private JTextField correoField;
	private JPasswordField passwordField;
	private PersonalButton RegistroButton;
	private PersonalButton atrasButton;

	public PanelRegistro() {
		super(new BorderLayout());
		atrasButton = new PersonalButton("Atrás", false);
		add(Tema.encabezado(atrasButton, "Registrar funcionario", null), BorderLayout.NORTH);

		TransparentPanel tarjeta = new TransparentPanel();
		tarjeta.setLayout(new GridBagLayout());
		cedulaField = new JTextField(18);
		passwordField = new JPasswordField(18);
		passwordField.putClientProperty("JComponent.showRevealButton", true);
		nombreField = new JTextField(18);
		correoField = new JTextField(18);
		correoField.putClientProperty("JTextField.placeholderText", "nombre@correo.com");
		Tema.filaFormulario(tarjeta, 0, "Cédula", cedulaField);
		Tema.filaFormulario(tarjeta, 1, "Contraseña", passwordField);
		Tema.filaFormulario(tarjeta, 2, "Nombre", nombreField);
		Tema.filaFormulario(tarjeta, 3, "Correo", correoField);

		RegistroButton = new PersonalButton("Registrar");
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 4;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(16, 0, 0, 0);
		tarjeta.add(RegistroButton, c);

		add(Tema.centrado(tarjeta, 420), BorderLayout.CENTER);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Tema.pintarFondo(g, this);
	}

	public PersonalButton getRegistroButton() {
		return RegistroButton;
	}

	public PersonalButton getAtrasButton() {
		return atrasButton;
	}

	public String getCedula() {
		return cedulaField.getText();
	}

	public String getPassword() {
		return new String(passwordField.getPassword());
	}

	public String getNombre() {
		return nombreField.getText();
	}

	public String getCorreo() {
		return correoField.getText();
	}
}
