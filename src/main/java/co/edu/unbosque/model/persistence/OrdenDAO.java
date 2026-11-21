package co.edu.unbosque.model.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Órdenes en la tabla ordenes; la fecha se guarda en formato ISO (2026-11-19T14:30). */
public class OrdenDAO {
    private final BaseDeDatos bd;

    public OrdenDAO(BaseDeDatos bd) {
        this.bd = bd;
    }

    public void agregarOrden(OrdenDTO orden) {
        try (Connection conexion = bd.conectar()) {
            insertar(conexion, orden);
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudo guardar la orden", e);
        }
    }

    /**
     * Descuenta el stock del medicamento y guarda la orden en una sola transacción: o se hacen
     * las dos cosas o ninguna. Devuelve {@code false} si ya no hay stock suficiente.
     */
    public boolean registrarVenta(OrdenDTO orden, long idMedicamento) {
        try (Connection conexion = bd.conectar()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement descontar = conexion.prepareStatement(
                    "UPDATE medicamentos SET cantidad = cantidad - ? WHERE id = ? AND cantidad >= ?")) {
                descontar.setInt(1, orden.getCantidadMedicamento());
                descontar.setLong(2, idMedicamento);
                descontar.setInt(3, orden.getCantidadMedicamento());
                if (descontar.executeUpdate() == 0) {
                    conexion.rollback();
                    return false;
                }
                insertar(conexion, orden);
                conexion.commit();
                return true;
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudo registrar la orden", e);
        }
    }

    public List<OrdenDTO> listarOrdenes() {
        List<OrdenDTO> ordenes = new ArrayList<>();
        try (Connection conexion = bd.conectar();
                PreparedStatement sql = conexion.prepareStatement(
                        "SELECT documento, entidad, medicamento, cantidad, fecha, total FROM ordenes ORDER BY id");
                ResultSet filas = sql.executeQuery()) {
            while (filas.next()) {
                String fecha = filas.getString("fecha");
                double valor = filas.getDouble("total");
                Double total = filas.wasNull() ? null : valor; // las órdenes antiguas no tienen total
                ordenes.add(new OrdenDTO(filas.getString("documento"), filas.getString("entidad"),
                        filas.getString("medicamento"), filas.getInt("cantidad"),
                        fecha == null ? null : LocalDateTime.parse(fecha), total));
            }
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudieron leer las órdenes", e);
        }
        return ordenes;
    }

    static void insertar(Connection conexion, OrdenDTO orden) throws SQLException {
        try (PreparedStatement sql = conexion.prepareStatement(
                "INSERT INTO ordenes (documento, entidad, medicamento, cantidad, fecha, total) VALUES (?, ?, ?, ?, ?, ?)")) {
            sql.setString(1, orden.getDocumentoPaciente());
            sql.setString(2, orden.getCodigoEntidad());
            sql.setString(3, orden.getNombreMedicamento());
            sql.setInt(4, orden.getCantidadMedicamento());
            sql.setString(5, orden.getFecha() == null ? null : orden.getFecha().toString());
            if (orden.getTotal() == null) {
                sql.setNull(6, Types.REAL);
            } else {
                sql.setDouble(6, orden.getTotal());
            }
            sql.executeUpdate();
        }
    }
}
