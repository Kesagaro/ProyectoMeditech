package co.edu.unbosque.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

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
		glassPanel.setBounds(25, 50, 485, 390);

		JLabel tituloLabel = new JLabel("Funcionarios", SwingConstants.CENTER);
		tituloLabel.setFont(new Font("Arial", Font.BOLD, 20));
		tituloLabel.setBounds(15, 15, 455, 30);
		glassPanel.add(tituloLabel);

		areaFuncionarios = new JTextArea();
		areaFuncionarios.setEditable(false);
		areaFuncionarios.setLineWrap(true);
		areaFuncionarios.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(areaFuncionarios);
		scrollPane.setBounds(15, 60, 455, 255);
		glassPanel.add(scrollPane);

		listarButton = new PersonalButton("Listar");
		listarButton.setBounds(15, 335, 220, 40);
		glassPanel.add(listarButton);

		eliminarButton = new PersonalButton("Eliminar");
		eliminarButton.setBounds(250, 335, 220, 40);
		glassPanel.add(eliminarButton);

		atrasButton = new PersonalButton("Atrás");
		atrasButton.setBounds(5, 5, 100, 40);
		add(atrasButton);

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
