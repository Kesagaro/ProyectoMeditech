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
	    String documento = view.getPanelUsuarios().getDocumentoField().getText();
	    String codigoEntidad = view.getPanelUsuarios().getCodigoEntidadField().getText();
	    String nombreMedicamento = view.getPanelUsuarios().getNombreMedicamentoField().getText();
	    int cantidad = Integer.parseInt(view.getPanelUsuarios().getCantidadField().getText());

	    claseX.agregarOrden(documento, codigoEntidad, nombreMedicamento, cantidad);
	    JOptionPane.showMessageDialog(view, "Orden agregada exitosamente.");
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

	private boolean hayCamposVacios(JTextField... campos) {
		for (JTextField campo : campos) {
			if (campo.getText().isBlank()) {
				return true;
			}
		}
		return false;
	}

	private void generarYMostrarTurno() {
		int turno = turnos.generarTurno();
		view.getPanelTurno().mostrarTurno(turno);
	}

	private void loginUsuario() {
		String cedula = view.getPanelLogin().getCedula();
		String password = view.getPanelLogin().getPassword();

		if (usuarioDAO.verificarUsuario(cedula, password)) {
			showPanelMenu();
		} else {
			JOptionPane.showMessageDialog(view, "Usuario o contraseña incorrectos. Intente de nuevo.");
		}
	}

	private void registrarUsuario() {
		String cedula = view.getPanelRegistro().getCedula();
		String password = view.getPanelRegistro().getPassword();

		FuncionarioDTO usuario = new FuncionarioDTO(cedula, password);
		usuarioDAO.guardarUsuario(usuario.getCedula(), usuario.getPassword());

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
		view.showPanelTop5();
	}

	private void showPanelUsuarios() {
		view.showPanelUsuarios();
	}

	private void showPanelFuncionarios() {
		view.showPanelFuncionarios();
	}

}