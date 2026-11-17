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
	private Farmacia farmacia;

	public Controller(View view) {
		this.view = view;
		this.farmacia = new Farmacia();
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
		view.getPanelReport().getOrdenesButton().addActionListener(e -> showPanelOrdenes());
		view.getPanelReport().getAtrasButton().addActionListener(e -> showPanelMenu());

		view.getPanelTop5().getAtrasButton().addActionListener(e -> showPanelReport());

		view.getPanelOrdenes().getAtrasButton().addActionListener(e -> showPanelReport());
		view.getPanelOrdenes().getAgregarOrdenButton().addActionListener(e -> agregarOrden());
	    view.getPanelOrdenes().getVerOrdenesButton().addActionListener(e -> verOrdenes());

		view.getPanelFuncionarios().getAtrasButton().addActionListener(e -> showPanelMenu());
		view.getPanelFuncionarios().getListarButton().addActionListener(e -> listarFuncionarios());
		view.getPanelFuncionarios().getEliminarButton().addActionListener(e -> eliminarFuncionario());

	}
	
	private void agregarOrden() {
		PanelOrdenes panel = view.getPanelOrdenes();
		if (hayCamposVacios(panel.getDocumentoField(), panel.getCodigoEntidadField(),
				panel.getNombreMedicamentoField(), panel.getCantidadField())) {
			mostrarMensaje("Todos los campos son obligatorios.");
			return;
		}
		String documento = panel.getDocumentoField().getText().trim();
		String entidad = panel.getCodigoEntidadField().getText().trim();
		String medicamento = panel.getNombreMedicamentoField().getText().trim();
		if ((documento + entidad + medicamento).contains(",")) {
			mostrarMensaje("Los datos no pueden contener comas.");
			return;
		}
		try {
			int cantidad = Integer.parseInt(panel.getCantidadField().getText().trim());
			if (cantidad <= 0) {
				mostrarMensaje("La cantidad debe ser mayor que cero.");
				return;
			}
			mostrarMensaje(farmacia.agregarOrden(documento, entidad, medicamento, cantidad));
		} catch (NumberFormatException e) {
			mostrarMensaje("La cantidad debe ser un número entero.");
		}
	}
	
	private void verOrdenes() {
	    String ordenes = farmacia.listarOrdenes();
	    view.getPanelOrdenes().getAreaOrdenes().setText(ordenes);
	    view.getPanelOrdenes().getAreaOrdenes().setCaretPosition(0);
	}

	private void listarFuncionarios() {
		String usuarios = farmacia.listarUsuarios();
		view.getPanelFuncionarios().getAreaFuncionarios().setText(usuarios);
		view.getPanelFuncionarios().getAreaFuncionarios().setCaretPosition(0);
	}

	private void eliminarFuncionario() {
		String cedula = JOptionPane.showInputDialog(view, "Ingrese la cédula del funcionario a eliminar:",
				"Eliminar funcionario", JOptionPane.QUESTION_MESSAGE);
		if (cedula == null) {
			return;
		}
		cedula = cedula.trim();
		if (!cedula.isEmpty()) {
			String resultado = farmacia.eliminarUsuarioPorCedula(cedula);
			mostrarMensaje(resultado);
			listarFuncionarios(); // Refresca la lista después de eliminar
		} else {
			mostrarMensaje("Cédula no válida.");
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
			mostrarMensaje("Todos los campos son obligatorios.");
			return;
		}
		try {
			long id = Long.parseLong(panel.getIdField().getText().trim());
			String nombre = panel.getNombreField().getText().trim();
			String empresa = panel.getEmpresaField().getText().trim();
			float precio = Float.parseFloat(panel.getPrecioField().getText().trim());
			int cantidad = Integer.parseInt(panel.getCantidadField().getText().trim());

			if (precio < 0 || cantidad < 0) {
				mostrarMensaje("El precio y la cantidad no pueden ser negativos.");
				return;
			}

			String resultado = actualizar
					? farmacia.actualizarMedicamento(id, nombre, empresa, precio, cantidad)
					: farmacia.agregarMedicamento(id, nombre, empresa, precio, cantidad);
			mostrarMensaje(resultado);
		} catch (NumberFormatException e) {
			mostrarMensaje("ID, precio y cantidad deben ser números válidos.");
		}
	}

	private void eliminarMedicamento() {
		String texto = view.getPanelInventario().getIdField().getText().trim();
		if (texto.isEmpty()) {
			mostrarMensaje("Ingrese el ID del medicamento a eliminar.");
			return;
		}
		try {
			long id = Long.parseLong(texto);
			int opcion = JOptionPane.showConfirmDialog(view, "¿Eliminar el medicamento con ID " + id + "?",
					"Confirmar", JOptionPane.YES_NO_OPTION);
			if (opcion == JOptionPane.YES_OPTION) {
				mostrarMensaje(farmacia.eliminarMedicamento(id));
				listarMedicamentos();
			}
		} catch (NumberFormatException e) {
			mostrarMensaje("El ID debe ser un número válido.");
		}
	}

	private void buscarMedicamento() {
		PanelMedicina panel = view.getPanelMedicina();
		String texto = panel.getBuscarField().getText().trim();
		if (texto.isEmpty()) {
			mostrarMensaje("Ingrese el nombre del medicamento a buscar.");
			return;
		}
		panel.getAreaResultados().setText(farmacia.buscarMedicamentos(texto));
		panel.getAreaResultados().setCaretPosition(0);
	}

	private void listarMedicamentos() {
		view.getPanelInventario().getAreaMedicamentos().setText(farmacia.listarMedicamentos());
		view.getPanelInventario().getAreaMedicamentos().setCaretPosition(0);
	}

	private void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(view, mensaje, "BosqueFarma", JOptionPane.INFORMATION_MESSAGE);
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
			mostrarMensaje("Ingrese la cédula del paciente.");
			return;
		}
		view.getPanelTurno().mostrarTurno(turnos.generarTurno(), cedula);
	}

	private void loginUsuario() {
		String cedula = view.getPanelLogin().getCedula().trim();
		String password = view.getPanelLogin().getPassword();

		if (cedula.isEmpty() || password.isEmpty()) {
			mostrarMensaje("Ingrese cédula y contraseña.");
			return;
		}
		if (usuarioDAO.verificarUsuario(cedula, password)) {
			showPanelMenu();
		} else {
			mostrarMensaje("Usuario o contraseña incorrectos. Intente de nuevo.");
		}
	}

	private void registrarUsuario() {
		String cedula = view.getPanelRegistro().getCedula().trim();
		String password = view.getPanelRegistro().getPassword();
		String nombre = view.getPanelRegistro().getNombre().trim();
		String correo = view.getPanelRegistro().getCorreo().trim();

		if (cedula.isEmpty() || password.isEmpty() || nombre.isEmpty() || correo.isEmpty()) {
			mostrarMensaje("Todos los campos son obligatorios.");
			return;
		}
		if ((cedula + password + nombre + correo).contains(",")) {
			mostrarMensaje("Los datos no pueden contener comas.");
			return;
		}
		if (!correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
			mostrarMensaje("Ingrese un correo válido.");
			return;
		}
		if (usuarioDAO.existeCedula(cedula)) {
			mostrarMensaje("Ya existe un funcionario registrado con esa cédula.");
			return;
		}

		usuarioDAO.guardarUsuario(new FuncionarioDTO(cedula, password, nombre, correo));

		mostrarMensaje("Usuario registrado con éxito.");
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
		view.getPanelTop5().mostrarReporte(farmacia.top5Medicamentos());
		view.showPanelTop5();
	}

	private void showPanelOrdenes() {
		view.showPanelOrdenes();
	}

	private void showPanelFuncionarios() {
		view.showPanelFuncionarios();
	}

}