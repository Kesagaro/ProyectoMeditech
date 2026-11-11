package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;

public class PanelTurno extends JPanel {
	private JTextField cedulaField;
	private JPasswordField passwordField;
	private PersonalButton turnoButton;
	private PersonalButton atrasButton;
	private TransparentPanel glassPanel;
	private JLabel cedulaLabel;
	private JLabel turnoLabel;

	public PanelTurno() {
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
        

        turnoButton = new PersonalButton("Turno");
        turnoButton.setBounds(75, 140, 150, 40); 
        glassPanel.add(turnoButton);

        atrasButton = new PersonalButton("Atrás");
        atrasButton.setBounds(75, 200, 150, 40); 
        glassPanel.add(atrasButton);
        
        turnoLabel = new JLabel(""); 
        turnoLabel.setBounds(75, 80, 150, 30); 
        turnoLabel.setFont(new Font("Arial", Font.BOLD, 30)); 
        turnoLabel.setForeground(Color.BLACK);
        glassPanel.add(turnoLabel);

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
	

	public PersonalButton getTurnoButton() {
		return turnoButton;
	}

	public PersonalButton getAtrasButton() {
		return atrasButton;
	}

	public String getCedula() {
		return cedulaField.getText();
	}
	
	public void mostrarTurno(int turno) {
        turnoLabel.setText("Turno: " + turno); 
    }

}