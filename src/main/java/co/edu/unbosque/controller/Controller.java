package co.edu.unbosque.controller;

import co.edu.unbosque.model.Farmacia;
import co.edu.unbosque.model.Resultado;
import co.edu.unbosque.view.*;

import javax.swing.*;
import java.io.File;

/**
 * Conecta la vista con el modelo: lee lo que el usuario escribió, se lo pasa a {@link Farmacia}
 * (que valida y aplica las reglas) y muestra la respuesta.
 */
public class Controller {
	private View view;
	private Farmacia farmacia;

	public Controller(View view) {
		this(view, new File("data"));
	}

	public Controller(View view, File carpetaDatos) {
		this.view = view;
		this.farmacia = new Farmacia(carpetaDatos);
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
		mostrarMensaje(farmacia.agregarOrden(panel.getDocumentoField().getText(),
				panel.getCodigoEntidadField().getText(), panel.getNombreMedicamentoField().getText(),
				panel.getCantidadField().getText()).mensaje());
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
			return; // el usuario canceló
		}
		mostrarMensaje(farmacia.eliminarUsuarioPorCedula(cedula));
		listarFuncionarios();
	}

	private void agregarMedicamento() {
		guardarMedicamento(false);
	}

	private void actualizarMedicamento() {
		guardarMedicamento(true);
	}

	private void guardarMedicamento(boolean actualizar) {
		PanelInventario panel = view.getPanelInventario();
		mostrarMensaje(farmacia.guardarMedicamento(panel.getIdField().getText(), panel.getNombreField().getText(),
				panel.getEmpresaField().getText(), panel.getPrecioField().getText(),
				panel.getCantidadField().getText(), actualizar).mensaje());
	}

	private void eliminarMedicamento() {
		String id = view.getPanelInventario().getIdField().getText();
		Resultado validacion = farmacia.validarIdMedicamento(id);
		if (!validacion.exito()) {
			mostrarMensaje(validacion.mensaje());
			return;
		}
		int opcion = JOptionPane.showConfirmDialog(view, "¿Eliminar el medicamento con ID " + id.trim() + "?",
				"Confirmar", JOptionPane.YES_NO_OPTION);
		if (opcion == JOptionPane.YES_OPTION) {
			mostrarMensaje(farmacia.eliminarMedicamento(id).mensaje());
			listarMedicamentos();
		}
	}

	private void buscarMedicamento() {
		PanelMedicina panel = view.getPanelMedicina();
		Resultado busqueda = farmacia.buscarMedicamentos(panel.getBuscarField().getText());
		if (!busqueda.exito()) {
			mostrarMensaje(busqueda.mensaje());
			return;
		}
		panel.getAreaResultados().setText(busqueda.mensaje());
		panel.getAreaResultados().setCaretPosition(0);
	}

	private void listarMedicamentos() {
		view.getPanelInventario().getAreaMedicamentos().setText(farmacia.listarMedicamentos());
		view.getPanelInventario().getAreaMedicamentos().setCaretPosition(0);
	}

	private void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(view, mensaje, "BosqueFarma", JOptionPane.INFORMATION_MESSAGE);
	}

	private void generarYMostrarTurno() {
		String cedula = view.getPanelTurno().getCedula();
		Resultado turno = farmacia.generarTurno(cedula);
		if (!turno.exito()) {
			mostrarMensaje(turno.mensaje());
			return;
		}
		view.getPanelTurno().mostrarTurno(turno.mensaje(), cedula.trim());
	}

	private void loginUsuario() {
		Resultado sesion = farmacia.iniciarSesion(view.getPanelLogin().getCedula(), view.getPanelLogin().getPassword());
		if (sesion.exito()) {
			showPanelMenu();
		} else {
			mostrarMensaje(sesion.mensaje());
		}
	}

	private void registrarUsuario() {
		PanelRegistro panel = view.getPanelRegistro();
		Resultado registro = farmacia.registrarFuncionario(panel.getCedula(), panel.getPassword(),
				panel.getNombre(), panel.getCorreo());
		mostrarMensaje(registro.mensaje());
		if (registro.exito()) {
			showPanelInicio();
		}
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