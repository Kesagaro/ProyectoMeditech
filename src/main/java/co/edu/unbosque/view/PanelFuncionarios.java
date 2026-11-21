package co.edu.unbosque.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class PanelFuncionarios extends JPanel {
	private Tabla tabla;
	private PersonalButton eliminarButton;
	private PersonalButton atrasButton;

	public PanelFuncionarios() {
		super(new BorderLayout());
		atrasButton = new PersonalButton("Atrás", false);
		add(Tema.encabezado(atrasButton, "Funcionarios", null), BorderLayout.NORTH);

		tabla = new Tabla(new String[] {"Cédula", "Nombre", "Correo"},
				new Class<?>[] {String.class, String.class, String.class});
		tabla.setMensajeVacio("No hay funcionarios registrados.");

		eliminarButton = new PersonalButton("Eliminar seleccionado", false);
		JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		acciones.setOpaque(false);
		acciones.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
		acciones.add(eliminarButton);

		TransparentPanel tarjeta = new TransparentPanel();
		tarjeta.setLayout(new BorderLayout());
		tarjeta.add(tabla, BorderLayout.CENTER);
		tarjeta.add(acciones, BorderLayout.SOUTH);
		add(Tema.contenido(tarjeta), BorderLayout.CENTER);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Tema.pintarFondo(g, this);
	}

	public Tabla getTabla() {
		return tabla;
	}

	/** Cédula del funcionario elegido en la tabla, o {@code null} si no hay ninguno. */
	public String getCedulaSeleccionada() {
		return (String) tabla.valorSeleccionado(0);
	}

	public PersonalButton getEliminarButton() {
		return eliminarButton;
	}

	public PersonalButton getAtrasButton() {
		return atrasButton;
	}
}
