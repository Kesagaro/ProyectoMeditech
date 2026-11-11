package co.edu.unbosque.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class PanelFuncionarios extends JPanel{
	private TransparentPanel glassPanel;
	private JTextArea areaFuncionarios;
    private PersonalButton eliminarButton;
    private PersonalButton atrasButton;
    private PersonalButton listarButton;
	
	public PanelFuncionarios() {
		setLayout(null);
		
		glassPanel = new TransparentPanel();
		glassPanel.setLayout(null);
		glassPanel.setBounds(100, 50, 300, 300);
		
		areaFuncionarios = new JTextArea();
		areaFuncionarios.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaFuncionarios);
        scrollPane.setBounds(50, 20, 200, 100);
        glassPanel.add(scrollPane);
        
        listarButton = new PersonalButton("Listar Funcionarios");
        listarButton.setBounds(75, 240, 160, 40);
        glassPanel.add(listarButton);
        
		atrasButton = new PersonalButton("Atrás");
        atrasButton.setBounds(75, 190, 160, 40); 
        glassPanel.add(atrasButton);
        
        eliminarButton = new PersonalButton("Eliminar");
        eliminarButton.setBounds(75, 140, 160, 40); 
        glassPanel.add(eliminarButton);
        
		
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
	
	public JTextArea getAreaFuncionarios() {
        return areaFuncionarios;
    }

    public PersonalButton getEliminarButton() {
        return eliminarButton;
    }

    public PersonalButton getAtrasButton() {
        return atrasButton;
    }

    public PersonalButton getListarButton() {
        return listarButton;
    }
}
