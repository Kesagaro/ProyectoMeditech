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
import javax.swing.JTextField;
import javax.swing.border.Border;

public class PanelInventario extends JPanel {

	private TransparentPanel glassPanel;
	private PersonalButton actualizarButton;
	private PersonalButton agregarButton;
	private PersonalButton atrasButton;

	private JTextField idField;
	private JTextField nombreField;
	private JTextField empresaField;
	private JTextField precioField;
	private JTextField cantidadField;

	private JLabel idLabel;
	private JLabel nombreLabel;
	private JLabel empresaLabel;
	private JLabel precioLabel;
	private JLabel cantidadLabel;

	public PanelInventario() {
		setLayout(null);

		glassPanel = new TransparentPanel();
		glassPanel.setLayout(null);
		glassPanel.setBounds(100, 50, 300, 300);

		// etiqueta y campo ID
		idLabel = new JLabel("Numero ID:");
		idLabel.setBounds(50, 20, 80, 25);
		glassPanel.add(idLabel);
		idField = new JTextField(15);
		idField.setBounds(140, 20, 120, 25);
		glassPanel.add(idField);

		// etiqueta y campo nombre medicamento
		nombreLabel = new JLabel("Nombre:");
		nombreLabel.setBounds(50, 50, 80, 25);
		glassPanel.add(nombreLabel);
		nombreField = new JTextField(15);
		nombreField.setBounds(140, 50, 120, 25);
		glassPanel.add(nombreField);

		// etiqueta y campo empresa
		empresaLabel = new JLabel("Empresa:");
		empresaLabel.setBounds(50, 80, 80, 25);
		glassPanel.add(empresaLabel);
		empresaField = new JTextField(15);
		empresaField.setBounds(140, 80, 120, 25);
		glassPanel.add(empresaField);

		// etiqueta y campo precio
		precioLabel = new JLabel("Precio:");
		precioLabel.setBounds(50, 110, 80, 25);
		glassPanel.add(precioLabel);
		precioField = new JTextField(15);
		precioField.setBounds(140, 110, 120, 25);
		glassPanel.add(precioField);

		// etiqueta y campo cantidad
		cantidadLabel = new JLabel("Cantidad:");
		cantidadLabel.setBounds(50, 140, 80, 25);
		glassPanel.add(cantidadLabel);
		cantidadField = new JTextField(15);
		cantidadField.setBounds(140, 140, 120, 25);
		glassPanel.add(cantidadField);

		// Botones
		atrasButton = new PersonalButton("Atras");
		atrasButton.setBounds(5, 5, 100, 40);
		add(atrasButton);

		actualizarButton = new PersonalButton("Actualizar");
		actualizarButton.setBounds(50, 170, 120, 30);
		glassPanel.add(actualizarButton);

		agregarButton = new PersonalButton("Agregar");
		agregarButton.setBounds(50, 220, 120, 30);
		glassPanel.add(agregarButton);

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

	public PersonalButton getActualizarButton() {
		return actualizarButton;
	}
	
	public PersonalButton getAgregarButton() {
		return agregarButton;
	}
	
	public PersonalButton getAtrasButton() {
		return atrasButton;
	}

	public JTextField getIdField() {
		return idField;
	}

	public void setIdField(JTextField idField) {
		this.idField = idField;
	}

	public JTextField getNombreField() {
		return nombreField;
	}

	public void setNombreField(JTextField nombreField) {
		this.nombreField = nombreField;
	}

	public JTextField getEmpresaField() {
		return empresaField;
	}

	public void setEmpresaField(JTextField empresaField) {
		this.empresaField = empresaField;
	}

	public JTextField getPrecioField() {
		return precioField;
	}

	public void setPrecioField(JTextField precioField) {
		this.precioField = precioField;
	}

	public JTextField getCantidadField() {
		return cantidadField;
	}

	public void setCantidadField(JTextField cantidadField) {
		this.cantidadField = cantidadField;
	}

}
