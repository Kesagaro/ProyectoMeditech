package co.edu.unbosque.model.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/** Turno siguiente del día, en la tabla turnos (una sola fila). */
public class TurnoDAO {
    private final BaseDeDatos bd;

    public TurnoDAO(BaseDeDatos bd) {
        this.bd = bd;
    }

    /** Turno siguiente guardado para ese día, o 1 si lo guardado es de otro día o no hay nada. */
    public int turnoSiguiente(LocalDate dia) {
        try (Connection conexion = bd.conectar();
                PreparedStatement sql = conexion.prepareStatement("SELECT siguiente FROM turnos WHERE id = 1 AND dia = ?")) {
            sql.setString(1, dia.toString());
            try (ResultSet filas = sql.executeQuery()) {
                return filas.next() ? Math.max(1, filas.getInt(1)) : 1;
            }
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudo leer el turno", e);
        }
    }

    public void guardar(LocalDate dia, int siguiente) {
        try (Connection conexion = bd.conectar()) {
            guardar(conexion, dia, siguiente);
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudo guardar el turno", e);
        }
    }

    static void guardar(Connection conexion, LocalDate dia, int siguiente) throws SQLException {
        try (PreparedStatement sql = conexion.prepareStatement(
                "INSERT OR REPLACE INTO turnos (id, dia, siguiente) VALUES (1, ?, ?)")) {
            sql.setString(1, dia.toString());
            sql.setInt(2, siguiente);
            sql.executeUpdate();
        }
    }
}
