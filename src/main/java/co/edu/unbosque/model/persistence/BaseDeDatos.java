package co.edu.unbosque.model.persistence;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Base de datos SQLite de la aplicación, en el archivo bosquefarma.db de la carpeta de datos.
 * Al crearla por primera vez se importan los archivos del formato anterior, si existen
 * (ver {@link MigracionArchivos}).
 */
public class BaseDeDatos {
    public static final String ARCHIVO = "bosquefarma.db";

    private static final String[] ESQUEMA = {
        """
        CREATE TABLE IF NOT EXISTS funcionarios (
            cedula   TEXT PRIMARY KEY,
            password TEXT NOT NULL,
            nombre   TEXT,
            correo   TEXT
        )""",
        """
        CREATE TABLE IF NOT EXISTS medicamentos (
            id       INTEGER PRIMARY KEY,
            nombre   TEXT    NOT NULL,
            empresa  TEXT    NOT NULL,
            precio   REAL    NOT NULL,
            cantidad INTEGER NOT NULL CHECK (cantidad >= 0)
        )""",
        """
        CREATE TABLE IF NOT EXISTS ordenes (
            id          INTEGER PRIMARY KEY AUTOINCREMENT,
            documento   TEXT    NOT NULL,
            entidad     TEXT    NOT NULL,
            medicamento TEXT    NOT NULL,
            cantidad    INTEGER NOT NULL CHECK (cantidad > 0),
            fecha       TEXT,
            total       REAL
        )""",
        """
        CREATE TABLE IF NOT EXISTS turnos (
            id        INTEGER PRIMARY KEY CHECK (id = 1),
            dia       TEXT    NOT NULL,
            siguiente INTEGER NOT NULL
        )"""
    };

    private final String url;

    public BaseDeDatos(File directorio) {
        directorio.mkdirs();
        File archivo = new File(directorio, ARCHIVO);
        boolean nueva = !archivo.exists();
        this.url = "jdbc:sqlite:" + archivo.getAbsolutePath();
        try (Connection conexion = conectar(); Statement sentencia = conexion.createStatement()) {
            for (String tabla : ESQUEMA) {
                sentencia.execute(tabla);
            }
            if (nueva) {
                new MigracionArchivos(directorio).importar(conexion);
            }
        } catch (SQLException e) {
            if (nueva) {
                archivo.delete(); // así la importación se intenta de nuevo en el próximo arranque
            }
            throw new ErrorDeDatos("No se pudo abrir la base de datos " + archivo.getPath(), e);
        }
    }

    /** Abre una conexión nueva; quien la pide la cierra (try-with-resources). */
    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(url);
    }
}
