package co.edu.unbosque.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
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
	private TransparentPanel glassPanel;
	private JLabel cedulaLabel;
	private JLabel nombreLabel;
	private JLabel correoLabel;
	
	public PanelRegistro() {
		setLayout(null);

		glassPanel = new TransparentPanel();
        glassPanel.setLayout(null); 
        glassPanel.setBounds(100, 50, 300, 300);
       
        
      
        cedulaLabel = new JLabel("Cédula:");
        cedulaLabel.setBounds(50, 20, 80, 25); 
        glassPanel.add(cedulaLabel);
        cedulaField = new JTextField(15);
        cedulaField.setBounds(140, 20, 120, 25); 
        glassPanel.add(cedulaField);
        
 
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setBounds(50, 50, 80, 25); 
        glassPanel.add(passwordLabel);
        passwordField = new JPasswordField(15);
        passwordField.setBounds(140, 50, 120, 25); 
        glassPanel.add(passwordField);
        
    
        nombreLabel = new JLabel ("Nombre:");
        nombreLabel.setBounds(50, 80, 80, 25);
        glassPanel.add(nombreLabel);
        nombreField = new JTextField(15);
        nombreField.setBounds(140, 80, 120, 25);
        glassPanel.add(nombreField);
        
       
        
        correoLabel = new JLabel ("Correo:");
        correoLabel.setBounds(50, 110, 80, 25);
        glassPanel.add(correoLabel);
        correoField = new JTextField(15);
        correoField.setBounds(140, 110, 120, 25);
        glassPanel.add(correoField);

        RegistroButton = new PersonalButton("Registrar");
        RegistroButton.setBounds(75, 140, 150, 40); 
        glassPanel.add(RegistroButton);

        atrasButton = new PersonalButton("Atrás");
        atrasButton.setBounds(75, 190, 150, 40); 
        glassPanel.add(atrasButton);

        add(glassPanel);
	}
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gradient = new GradientPaint(0, 0, new Color(107, 142, 78), 0, getHeight(), new Color(178, 197, 178));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
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