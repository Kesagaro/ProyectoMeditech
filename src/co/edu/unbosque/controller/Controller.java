package co.edu.unbosque.controller;

import co.edu.unbosque.model.*;
import co.edu.unbosque.model.persistence.FuncionariosDAO;
import co.edu.unbosque.model.persistence.FuncionarioDTO;
import co.edu.unbosque.view.*;

import javax.swing.*;

public class Controller {
	private View view;
	private FuncionariosDAO usuarioDAO;
	private Turnos turnos;
	private ClaseX claseX;

	public Controller(View view) {
		this.view = view;
		this.claseX = new ClaseX();
		this.usuarioDAO = new FuncionariosDAO();
		this.turnos = new Turnos();
		initController();
	}

	private void initController() {
		view.getPanelInventario().getAgregarButton().addActionListener(e -> agregarMedicamento());
		view.getPanelInventario().getActualizarButton().addActionListener(e -> actualizarMedicamento());
		view.getPanelInventario().getEliminarButton().addActionListener(e -> eliminarMedicamento());
		view.getPanelInventario().getListarButton().addActionListener(e -> listarMedicamentos());
		view.getPanelInventario().getAtrasButton().addActionListener(e -> showPanelMenu());

		view.getPanelInicio().getLoginButton().addActionListener(e -> showPanelLogin());
		view.getPanelInicio().getRegisterButton().addActionListener(e -> showPanelRegistro());
		view.getPanelLogin().getLoginButton().addActionListener(e -> loginUsuario());
		view.getPanelLogin().getAtrasButton().addActionListener(e -> showPanelInicio());

		view.getPanelRegistro().getAtrasButton().addActionListener(e -> showPanelInicio());
		view.getPanelRegistro().getRegistroButton().addActionListener(e -> registrarUsuario());
		view.getPanelMenu().getInventarioButton().addActionListener(e -> showPanelInventario());

		view.getPanelMenu().getAtrasButton().addActionListener(e -> showPanelLogin());
		view.getPanelMenu().getTurnoButton().addActionListener(e -> showPanelTurno());
		view.getPanelMenu().getReporButton().addActionListener(e -> showPanelReport());
		view.getPanelMenu().getFuncionariosButton().addActionListener(e -> showPanelFuncionarios());
		view.getPanelMenu().getMediButton().addActionListener(e -> view.showPanelMedicina());

		view.getPanelMedicina().getBuscarButton().addActionListener(e -> buscarMedicamento());
		view.getPanelMedicina().getBuscarField().addActionListener(e -> buscarMedicamento());
		view.getPanelMedicina().getAtrasButton().addActionListener(e -> showPanelMenu());

		view.getPanelTurno().getTurnoButton().addActionListener(e -> generarYMostrarTurno());
		view.getPanelTurno().getAtrasButton().addActionListener(e -> showPanelMenu());

		view.getPanelReport().getTop5Button().addActionListener(e -> showPanelTop5());
		view.getPanelReport().getUsuariosButton().addActionListener(e -> showPanelUsuarios());
		view.getPanelReport().getAtrasButton().addActionListener(e -> showPanelMenu());

		view.getPanelTop5().getAtrasButton().addActionListener(e -> showPanelReport());

		view.getPanelUsuarios().getAtrasButton().addActionListener(e -> showPanelReport());
		view.getPanelUsuarios().getAgregarOrdenButton().addActionListener(e -> agregarOrden());
	    view.getPanelUsuarios().getVerOrdenesButton().addActionListener(e -> verOrdenes());

		view.getPanelFuncionarios().getAtrasButton().addActionListener(e -> showPanelMenu());
		view.getPanelFuncionarios().getListarButton().addActionListener(e -> listarFuncionarios());
		view.getPanelFuncionarios().getEliminarButton().addActionListener(e -> eliminarFuncionario());

	}
	
	private void agregarOrden() {
		PanelUsuarios panel = view.getPanelUsuarios();
		if (hayCamposVacios(panel.getDocumentoField(), panel.getCodigoEntidadField(),
				panel.getNombreMedicamentoField(), panel.getCantidadField())) {
			JOptionPane.showMessageDialog(view, "Todos los campos son obligatorios.");
			return;
		}
		String documento = panel.getDocumentoField().getText().trim();
		String entidad = panel.getCodigoEntidadField().getText().trim();
		String medicamento = panel.getNombreMedicamentoField().getText().trim();
		if ((documento + entidad + medicamento).contains(",")) {
			JOptionPane.showMessageDialog(view, "Los datos no pueden contener comas.");
			return;
		}
		try {
			int cantidad = Integer.parseInt(panel.getCantidadField().getText().trim());
			if (cantidad <= 0) {
				JOptionPane.showMessageDialog(view, "La cantidad debe ser mayor que cero.");
				return;
			}
			JOptionPane.showMessageDialog(view, claseX.agregarOrden(documento, entidad, medicamento, cantidad));
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(view, "La cantidad debe ser un número entero.");
		}
	}
	
	private void verOrdenes() {
	    String ordenes = claseX.listarOrdenes();
	    view.getPanelUsuarios().getAreaOrdenes().setText(ordenes);
	}

	private void listarFuncionarios() {
		String usuarios = claseX.listarUsuarios();
		view.getPanelFuncionarios().getAreaFuncionarios().setText(usuarios);
	}

	private void eliminarFuncionario() {
		String cedula = JOptionPane.showInputDialog(view, "Ingrese la cédula del funcionario a eliminar:");
		if (cedula != null && !cedula.isEmpty()) {
			String resultado = claseX.eliminarUsuarioPorCedula(cedula);
			JOptionPane.showMessageDialog(view, resultado);
			listarFuncionarios(); // Refresca la lista después de eliminar
		} else {
			JOptionPane.showMessageDialog(view, "Cédula no válida.");
		}
	}

	private void agregarMedicamento() {
		guardarMedicamento(false);
	}

	private void actualizarMedicamento() {
		guardarMedicamento(true);
	}

	private void guardarMedicamento(boolean actualizar) {
		PanelInventario panel = view.getPanelInventario();
		if (hayCamposVacios(panel.getIdField(), panel.getNombreField(), panel.getEmpresaField(),
				panel.getPrecioField(), panel.getCantidadField())) {
			JOptionPane.showMessageDialog(view, "Todos los campos son obligatorios.");
			return;
		}
		try {
			long id = Long.parseLong(panel.getIdField().getText().trim());
			String nombre = panel.getNombreField().getText().trim();
			String empresa = panel.getEmpresaField().getText().trim();
			float precio = Float.parseFloat(panel.getPrecioField().getText().trim());
			int cantidad = Integer.parseInt(panel.getCantidadField().getText().trim());

			if (precio < 0 || cantidad < 0) {
				JOptionPane.showMessageDialog(view, "El precio y la cantidad no pueden ser negativos.");
				return;
			}

			String resultado = actualizar
					? claseX.actualizarMedicamento(id, nombre, empresa, precio, cantidad)
					: claseX.agregarMedicamento(id, nombre, empresa, precio, cantidad);
			JOptionPane.showMessageDialog(view, resultado);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(view, "ID, precio y cantidad deben ser números válidos.");
		}
	}

	private void eliminarMedicamento() {
		String texto = view.getPanelInventario().getIdField().getText().trim();
		if (texto.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Ingrese el ID del medicamento a eliminar.");
			return;
		}
		try {
			long id = Long.parseLong(texto);
			int opcion = JOptionPane.showConfirmDialog(view, "¿Eliminar el medicamento con ID " + id + "?",
					"Confirmar", JOptionPane.YES_NO_OPTION);
			if (opcion == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(view, claseX.eliminarMedicamento(id));
				listarMedicamentos();
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(view, "El ID debe ser un número válido.");
		}
	}

	private void buscarMedicamento() {
		PanelMedicina panel = view.getPanelMedicina();
		String texto = panel.getBuscarField().getText().trim();
		if (texto.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Ingrese el nombre del medicamento a buscar.");
			return;
		}
		panel.getAreaResultados().setText(claseX.buscarMedicamentos(texto));
		panel.getAreaResultados().setCaretPosition(0);
	}

	private void listarMedicamentos() {
		view.getPanelInventario().getAreaMedicamentos().setText(claseX.listarMedicamentos());
		view.getPanelInventario().getAreaMedicamentos().setCaretPosition(0);
	}

	private boolean hayCamposVacios(JTextField... campos) {
		for (JTextField campo : campos) {
			if (campo.getText().isBlank()) {
				return true;
			}
		}
		return false;
	}

	private void generarYMostrarTurno() {
		String cedula = view.getPanelTurno().getCedula().trim();
		if (cedula.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Ingrese la cédula del paciente.");
			return;
		}
		view.getPanelTurno().mostrarTurno(turnos.generarTurno(), cedula);
	}

	private void loginUsuario() {
		String cedula = view.getPanelLogin().getCedula().trim();
		String password = view.getPanelLogin().getPassword();

		if (cedula.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Ingrese cédula y contraseña.");
			return;
		}
		if (usuarioDAO.verificarUsuario(cedula, password)) {
			showPanelMenu();
		} else {
			JOptionPane.showMessageDialog(view, "Usuario o contraseña incorrectos. Intente de nuevo.");
		}
	}

	private void registrarUsuario() {
		String cedula = view.getPanelRegistro().getCedula().trim();
		String password = view.getPanelRegistro().getPassword();
		String nombre = view.getPanelRegistro().getNombre().trim();
		String correo = view.getPanelRegistro().getCorreo().trim();

		if (cedula.isEmpty() || password.isEmpty() || nombre.isEmpty() || correo.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Todos los campos son obligatorios.");
			return;
		}
		if ((cedula + password + nombre + correo).contains(",")) {
			JOptionPane.showMessageDialog(view, "Los datos no pueden contener comas.");
			return;
		}
		if (!correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
			JOptionPane.showMessageDialog(view, "Ingrese un correo válido.");
			return;
		}
		if (usuarioDAO.existeCedula(cedula)) {
			JOptionPane.showMessageDialog(view, "Ya existe un funcionario registrado con esa cédula.");
			return;
		}

		usuarioDAO.guardarUsuario(new FuncionarioDTO(cedula, password, nombre, correo));

		JOptionPane.showMessageDialog(view, "Usuario registrado con éxito.");
		showPanelInicio();
	}

	private void showPanelLogin() {
		view.showPanelLogin();
	}

	private void showPanelMenu() {
		view.showPanelMenu();
	}

	private void showPanelRegistro() {
		view.showPanelRegistro();
	}

	private void showPanelInicio() {
		view.showPanelInicio();
	}

	private void showPanelTurno() {
		view.showPanelTurno();
	}

	private void showPanelInventario() {
		view.showPanelInventario();
	}

	private void showPanelReport() {
		view.showPanelReport();
	}

	private void showPanelTop5() {
		view.getPanelTop5().mostrarReporte(claseX.top5Medicamentos());
		view.showPanelTop5();
	}

	private void showPanelUsuarios() {
		view.showPanelUsuarios();
	}

	private void showPanelFuncionarios() {
		view.showPanelFuncionarios();
	}

}