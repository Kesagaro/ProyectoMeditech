package co.edu.unbosque.model;

import co.edu.unbosque.model.persistence.MedicamentoDAO;
import co.edu.unbosque.model.persistence.MedicamentoDTO;
import co.edu.unbosque.model.persistence.FuncionariosDAO;
import co.edu.unbosque.model.persistence.OrdenDAO;
import co.edu.unbosque.model.persistence.OrdenDTO;

public class ClaseX {
    private MedicamentoDAO medicamentoDAO;
    private FuncionariosDAO usuarioDAO;
    private OrdenDAO ordenDAO;

    public ClaseX() {
        this.medicamentoDAO = new MedicamentoDAO();
        this.usuarioDAO = new FuncionariosDAO();
        this.ordenDAO = new OrdenDAO();
    }

    public String agregarMedicamento(long id, String nombre, String empresa, float precio, int cantidad) {
        if (medicamentoDAO.existeId(id)) {
            return "Ya existe un medicamento con el ID " + id + ".";
        }
        MedicamentoDTO medicamento = new MedicamentoDTO(id, nombre, empresa, precio, cantidad);
        medicamentoDAO.crear(medicamento);
        return "Medicamento agregado con éxito.";
    }

    public String listarMedicamentos() {
        String listado = medicamentoDAO.mostrar();
        return listado.isEmpty() ? "No hay medicamentos registrados." : listado;
    }

    public String eliminarMedicamento(long id) {
        return medicamentoDAO.eliminarPorId(id);
    }

    public String actualizarMedicamento(long id, String nombre, String empresa, float precio, int cantidad) {
        MedicamentoDTO medicamentoActualizado = new MedicamentoDTO(id, nombre, empresa, precio, cantidad);
        return medicamentoDAO.actualizarPorId(id, medicamentoActualizado);
    }

    public String eliminarMedicamentoPorNombre(String nombre) {
        return medicamentoDAO.eliminarPorNombre(nombre);
    }
    
    public String listarUsuarios() {
        return usuarioDAO.mostrarUsuarios();
    }
    
    public String eliminarUsuarioPorCedula(String cedula) {
        return usuarioDAO.eliminarPorCedula(cedula);
    }
    
    public void agregarOrden(String documento, String codigoEntidad, String nombreMedicamento, int cantidad) {
        OrdenDTO orden = new OrdenDTO(documento, codigoEntidad, nombreMedicamento, cantidad);
        ordenDAO.agregarOrden(orden);
    }
    
    public String listarOrdenes() {
        StringBuilder listado = new StringBuilder();
        for (OrdenDTO orden : ordenDAO.listarOrdenes()) {
            listado.append("Documento: ").append(orden.getDocumentoPaciente())
                   .append(", Entidad: ").append(orden.getCodigoEntidad())
                   .append(", Medicamento: ").append(orden.getNombreMedicamento())
                   .append(", Cantidad: ").append(orden.getCantidadMedicamento()).append("\n");
        }
        return listado.toString();
    }
}
