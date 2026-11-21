package co.edu.unbosque.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelMenu extends JPanel {
	private PersonalButton inventarioButton;
	private PersonalButton turnoButton;
	private PersonalButton funcionariosButton;
	private PersonalButton mediButton;
	private PersonalButton reporButton;
	private PersonalButton atrasButton;
	private JLabel sesionLabel;

	public PanelMenu() {
		super(new BorderLayout());
		atrasButton = new PersonalButton("Cerrar sesión", false);
		sesionLabel = new JLabel("");
		sesionLabel.setFont(Tema.fuente(Font.BOLD, 1));
		sesionLabel.setForeground(Color.WHITE);
		add(Tema.encabezado(atrasButton, "Menú principal", sesionLabel), BorderLayout.NORTH);

		turnoButton = opcion("Turnos", "Asignar turno a un paciente");
		inventarioButton = opcion("Inventario", "Agregar, editar y eliminar");
		mediButton = opcion("Medicina", "Buscar precio y stock");
		funcionariosButton = opcion("Funcionarios", "Ver y eliminar cuentas");
		reporButton = opcion("Reportes", "Órdenes y top de ventas");

		// tres opciones arriba y dos centradas abajo
		TransparentPanel tarjeta = new TransparentPanel();
		tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
		tarjeta.add(fila(turnoButton, inventarioButton, mediButton));
		tarjeta.add(Box.createVerticalStrut(16));
		tarjeta.add(fila(funcionariosButton, reporButton));
		add(Tema.centrado(tarjeta, 720), BorderLayout.CENTER);
	}

	private static JPanel fila(PersonalButton... botones) {
		JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
		fila.setOpaque(false);
		for (PersonalButton boton : botones) {
			fila.add(boton);
		}
		return fila;
	}

	/** Botón grande del menú con un título y una descripción corta. */
	private static PersonalButton opcion(String titulo, String descripcion) {
		PersonalButton boton = new PersonalButton("<html><center><span style='font-size:13pt'>" + titulo
				+ "</span><br><span style='font-size:9pt;font-weight:normal'>" + descripcion + "</span></center></html>");
		boton.setPreferredSize(new Dimension(200, 92));
		return boton;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Tema.pintarFondo(g, this);
	}

	/** Muestra quién inició sesión; con {@code null} se borra al cerrar la sesión. */
	public void mostrarFuncionario(String nombre) {
		sesionLabel.setText(nombre == null ? "" : "Sesión: " + nombre);
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

	public PersonalButton getMediButton() {
		return mediButton;
	}
}
