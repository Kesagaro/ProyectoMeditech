package co.edu.unbosque.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class PanelInventario extends JPanel {
	private PersonalButton actualizarButton;
	private PersonalButton agregarButton;
	private PersonalButton atrasButton;
	private PersonalButton eliminarButton;
	private PersonalButton limpiarButton;
	private Tabla tabla;

	private JTextField idField;
	private JTextField nombreField;
	private JTextField empresaField;
	private JTextField precioField;
	private JTextField cantidadField;

	public PanelInventario() {
		super(new BorderLayout());
		atrasButton = new PersonalButton("Atrás", false);
		add(Tema.encabezado(atrasButton, "Inventario", null), BorderLayout.NORTH);

		// formulario a la izquierda
		TransparentPanel formulario = new TransparentPanel();
		formulario.setLayout(new GridBagLayout());
		idField = new JTextField(12);
		nombreField = new JTextField(12);
		empresaField = new JTextField(12);
		precioField = new JTextField(12);
		cantidadField = new JTextField(12);
		Tema.filaFormulario(formulario, 0, "ID", idField);
		Tema.filaFormulario(formulario, 1, "Nombre", nombreField);
		Tema.filaFormulario(formulario, 2, "Empresa", empresaField);
		Tema.filaFormulario(formulario, 3, "Precio", precioField);
		Tema.filaFormulario(formulario, 4, "Cantidad", cantidadField);

		agregarButton = new PersonalButton("Agregar");
		actualizarButton = new PersonalButton("Actualizar");
		eliminarButton = new PersonalButton("Eliminar", false);
		limpiarButton = new PersonalButton("Limpiar", false);
		JPanel botones = new JPanel(new GridLayout(2, 2, 8, 8));
		botones.setOpaque(false);
		botones.add(agregarButton);
		botones.add(actualizarButton);
		botones.add(eliminarButton);
		botones.add(limpiarButton);
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 5;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(14, 0, 0, 0);
		formulario.add(botones, c);
		c.gridy = 6;
		c.weighty = 1; // empuja el formulario hacia arriba
		formulario.add(javax.swing.Box.createVerticalGlue(), c);
		formulario.setPreferredSize(new Dimension(290, formulario.getPreferredSize().height));
		limpiarButton.addActionListener(e -> limpiar());

		// tabla a la derecha; al elegir una fila se copia al formulario para editarla
		tabla = new Tabla(new String[] {"ID", "Nombre", "Empresa", "Precio", "Stock"},
				new Class<?>[] {Long.class, String.class, String.class, Double.class, Integer.class});
		tabla.setMensajeVacio("No hay medicamentos registrados.");
		tabla.anchos(55, 190, 130, 85, 65);
		tabla.getTabla().getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && tabla.valorSeleccionado(0) != null) {
				copiarSeleccion();
			}
		});
		TransparentPanel listado = new TransparentPanel();
		listado.setLayout(new BorderLayout());
		listado.add(tabla);

		JPanel centro = new JPanel(new BorderLayout(16, 0));
		centro.setOpaque(false);
		centro.add(formulario, BorderLayout.WEST);
		centro.add(listado, BorderLayout.CENTER);
		add(Tema.contenido(centro), BorderLayout.CENTER);
	}

	private void copiarSeleccion() {
		idField.setText(String.valueOf(tabla.valorSeleccionado(0)));
		nombreField.setText((String) tabla.valorSeleccionado(1));
		empresaField.setText((String) tabla.valorSeleccionado(2));
		double precio = (Double) tabla.valorSeleccionado(3);
		precioField.setText(precio == Math.rint(precio) ? String.valueOf((long) precio) : String.valueOf(precio));
		cantidadField.setText(String.valueOf(tabla.valorSeleccionado(4)));
	}

	/** Vacía el formulario y quita la selección de la tabla. */
	public void limpiar() {
		tabla.getTabla().clearSelection();
		for (JTextField campo : new JTextField[] {idField, nombreField, empresaField, precioField, cantidadField}) {
			campo.setText("");
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Tema.pintarFondo(g, this);
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

	public PersonalButton getEliminarButton() {
		return eliminarButton;
	}

	public PersonalButton getLimpiarButton() {
		return limpiarButton;
	}

	public Tabla getTabla() {
		return tabla;
	}

	public JTextField getIdField() {
		return idField;
	}

	public JTextField getNombreField() {
		return nombreField;
	}

	public JTextField getEmpresaField() {
		return empresaField;
	}

	public JTextField getPrecioField() {
		return precioField;
	}

	public JTextField getCantidadField() {
		return cantidadField;
	}
}
