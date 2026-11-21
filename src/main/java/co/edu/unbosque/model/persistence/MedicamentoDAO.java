package co.edu.unbosque.model.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Inventario en la tabla medicamentos (id, nombre, empresa, precio, cantidad). */
public class MedicamentoDAO implements Crud<MedicamentoDTO> {
    private static final String COLUMNAS = "SELECT id, nombre, empresa, precio, cantidad FROM medicamentos ";

    private final BaseDeDatos bd;

    public MedicamentoDAO(BaseDeDatos bd) {
        this.bd = bd;
    }

    @Override
    public void crear(MedicamentoDTO data) {
        actualizarFilas("INSERT INTO medicamentos (nombre, empresa, precio, cantidad, id) VALUES (?, ?, ?, ?, ?)", data);
    }

    @Override
    public String mostrar() {
        StringBuilder sb = new StringBuilder();
        for (MedicamentoDTO medicamento : consultar(COLUMNAS + "ORDER BY id")) {
            sb.append("Número ID: ").append(medicamento.getNumId()).append("\n");
            sb.append("Nombre: ").append(medicamento.getNombre()).append("\n");
            sb.append("Empresa: ").append(medicamento.getEmpresa()).append("\n");
            sb.append("Precio: ").append(medicamento.getPrecio()).append("\n");
            sb.append("Cantidad: ").append(medicamento.getCantidad()).append("\n\n");
        }
        return sb.toString();
    }

    /** Reemplaza el medicamento que ocupa la posición {@code index} del listado (ordenado por ID). */
    @Override
    public String actualizar(int index, MedicamentoDTO newData) {
        List<MedicamentoDTO> medicamentos = consultar(COLUMNAS + "ORDER BY id");
        if (index < 0 || index >= medicamentos.size()) {
            return "Índice inválido.";
        }
        return actualizarPorId(medicamentos.get(index).getNumId(), newData);
    }

    /** Todo el inventario, ordenado por ID. */
    public List<MedicamentoDTO> listar() {
        return consultar(COLUMNAS + "ORDER BY id");
    }

    public boolean existeId(long id) {
        return !consultar(COLUMNAS + "WHERE id = ?", id).isEmpty();
    }

    public String actualizarPorId(long id, MedicamentoDTO newData) {
        MedicamentoDTO conId = new MedicamentoDTO(id, newData.getNombre(), newData.getEmpresa(),
                newData.getPrecio(), newData.getCantidad());
        int filas = actualizarFilas("UPDATE medicamentos SET nombre = ?, empresa = ?, precio = ?, cantidad = ? WHERE id = ?", conId);
        return filas > 0 ? "Medicamento actualizado correctamente." : "Medicamento no encontrado.";
    }

    // Las búsquedas por nombre comparan en Java: lower() de SQLite solo convierte letras ASCII
    // y "ÁCIDO" no coincidiría con "ácido".

    public List<MedicamentoDTO> buscarPorNombre(String texto) {
        String buscado = texto.toLowerCase();
        List<MedicamentoDTO> encontrados = consultar(COLUMNAS + "ORDER BY id");
        encontrados.removeIf(m -> !m.getNombre().toLowerCase().contains(buscado));
        return encontrados;
    }

    public MedicamentoDTO buscarPorNombreExacto(String nombre) {
        for (MedicamentoDTO medicamento : consultar(COLUMNAS + "ORDER BY id")) {
            if (medicamento.getNombre().equalsIgnoreCase(nombre)) {
                return medicamento;
            }
        }
        return null;
    }

    public String eliminarPorId(long id) {
        return borrar("DELETE FROM medicamentos WHERE id = ?", id) > 0
                ? "Medicamento eliminado correctamente." : "Medicamento no encontrado.";
    }

    @Override
    public String eliminarPorNombre(String name) {
        borrar("DELETE FROM medicamentos WHERE nombre = ?", name);
        return "Medicamento eliminado correctamente.";
    }

    @Override
    public String checkearIndex(int index) {
        return (index >= 0 && index < consultar(COLUMNAS).size()) ? "Índice válido." : "Índice inválido.";
    }

    private List<MedicamentoDTO> consultar(String consulta, Object... parametros) {
        List<MedicamentoDTO> medicamentos = new ArrayList<>();
        try (Connection conexion = bd.conectar(); PreparedStatement sql = conexion.prepareStatement(consulta)) {
            for (int i = 0; i < parametros.length; i++) {
                sql.setObject(i + 1, parametros[i]);
            }
            try (ResultSet filas = sql.executeQuery()) {
                while (filas.next()) {
                    medicamentos.add(new MedicamentoDTO(filas.getLong("id"), filas.getString("nombre"),
                            filas.getString("empresa"), filas.getFloat("precio"), filas.getInt("cantidad")));
                }
            }
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudo leer el inventario", e);
        }
        return medicamentos;
    }

    /** Ejecuta un INSERT o UPDATE con los campos en orden: nombre, empresa, precio, cantidad, id. */
    private int actualizarFilas(String sentencia, MedicamentoDTO m) {
        try (Connection conexion = bd.conectar(); PreparedStatement sql = conexion.prepareStatement(sentencia)) {
            sql.setString(1, m.getNombre());
            sql.setString(2, m.getEmpresa());
            sql.setFloat(3, m.getPrecio());
            sql.setInt(4, m.getCantidad());
            sql.setLong(5, m.getNumId());
            return sql.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudo guardar el medicamento", e);
        }
    }

    private int borrar(String sentencia, Object parametro) {
        try (Connection conexion = bd.conectar(); PreparedStatement sql = conexion.prepareStatement(sentencia)) {
            sql.setObject(1, parametro);
            return sql.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudo eliminar el medicamento", e);
        }
    }
}
