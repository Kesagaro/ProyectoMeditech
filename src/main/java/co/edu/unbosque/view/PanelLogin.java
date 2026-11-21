package co.edu.unbosque.view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class PanelLogin extends JPanel {
	private JTextField cedulaField;
	private JPasswordField passwordField;
	private PersonalButton loginButton;
	private PersonalButton atrasButton;

	public PanelLogin() {
		super(new BorderLayout());
		atrasButton = new PersonalButton("Atrás", false);
		add(Tema.encabezado(atrasButton, "Iniciar sesión", null), BorderLayout.NORTH);

		TransparentPanel tarjeta = new TransparentPanel();
		tarjeta.setLayout(new GridBagLayout());
		cedulaField = new JTextField(16);
		cedulaField.putClientProperty("JTextField.placeholderText", "Número de cédula");
		passwordField = new JPasswordField(16);
		passwordField.putClientProperty("JComponent.showRevealButton", true);
		Tema.filaFormulario(tarjeta, 0, "Cédula", cedulaField);
		Tema.filaFormulario(tarjeta, 1, "Contraseña", passwordField);

		loginButton = new PersonalButton("Ingresar");
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 2;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(16, 0, 0, 0);
		tarjeta.add(loginButton, c);
		// Enter en la contraseña equivale a pulsar "Ingresar"
		passwordField.addActionListener(e -> loginButton.doClick());

		add(Tema.centrado(tarjeta, 380), BorderLayout.CENTER);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Tema.pintarFondo(g, this);
	}

	public PersonalButton getLoginButton() {
		return loginButton;
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

	/** Borra la contraseña para que no quede escrita al volver a esta pantalla. */
	public void limpiarPassword() {
		passwordField.setText("");
	}
}
