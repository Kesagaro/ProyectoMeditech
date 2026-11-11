package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;

public class PanelLogin extends JPanel {
	private JTextField cedulaField;
	private JPasswordField passwordField;
	private PersonalButton loginButton;
	private PersonalButton atrasButton;
	private TransparentPanel glassPanel;
	private JLabel cedulaLabel;

	public PanelLogin() {
		setLayout(null);

		glassPanel = new TransparentPanel();
        glassPanel.setLayout(null); 
        glassPanel.setBounds(100, 50, 300, 300);
       
        
        //etiqueta y campo cedula
        cedulaLabel = new JLabel("Cédula:");
        cedulaLabel.setBounds(50, 20, 80, 25); 
        glassPanel.add(cedulaLabel);
        cedulaField = new JTextField(15);
        cedulaField.setBounds(140, 20, 120, 25); 
        glassPanel.add(cedulaField);
        
        //etiqueta y campo contraseña
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setBounds(50, 60, 80, 25); 
        glassPanel.add(passwordLabel);
        passwordField = new JPasswordField(15);
        passwordField.setBounds(140, 60, 120, 25); 
        glassPanel.add(passwordField);
        

        loginButton = new PersonalButton("Login");
        loginButton.setBounds(75, 120, 150, 40); 
        glassPanel.add(loginButton);

        atrasButton = new PersonalButton("Atrás");
        atrasButton.setBounds(75, 180, 150, 40); 
        glassPanel.add(atrasButton);

        add(glassPanel);
    }
	 @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        Graphics2D g2d = (Graphics2D) g;

	        GradientPaint gradient = new GradientPaint(0, 0, new Color(107, 142, 78), 0, getHeight(), new Color(178, 197, 178));
	        g2d.setPaint(gradient);
	        g2d.fillRect(0, 0, getWidth(), getHeight());
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
}