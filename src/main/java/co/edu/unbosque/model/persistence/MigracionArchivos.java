package co.edu.unbosque.model.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Importa a la base de datos los archivos que usaban las versiones anteriores (hasta la 1.1.0):
 * usuarios.txt, Medicamentos.txt (serialización Java), ordenes.txt y turnos.txt.
 * Se ejecuta una sola vez, al crear la base. Los archivos no se modifican: quedan como respaldo.
 * Las líneas dañadas se ignoran para no impedir la importación del resto.
 */
class MigracionArchivos {
    /** Solo se aceptan las clases que guardaba el inventario; cualquier otra se rechaza al leer. */
    private static final ObjectInputFilter FILTRO_INVENTARIO = ObjectInputFilter.Config.createFilter(
            "java.util.ArrayList;" + MedicamentoDTO.class.getName() + ";java.lang.Object;!*");

    private final File directorio;

    MigracionArchivos(File directorio) {
        this.directorio = directorio;
    }

    void importar(Connection conexion) throws SQLException {
        conexion.setAutoCommit(false);
        try {
            importarFuncionarios(conexion);
            importarMedicamentos(conexion);
            importarOrdenes(conexion);
            importarTurno(conexion);
            conexion.commit();
        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
    }

    /** Líneas cedula,contraseña[,nombre,correo]; las contraseñas en texto plano se convierten al iniciar sesión. */
    private void importarFuncionarios(Connection conexion) throws SQLException {
        try (PreparedStatement sql = conexion.prepareStatement(
                "INSERT OR IGNORE INTO funcionarios (cedula, password, nombre, correo) VALUES (?, ?, ?, ?)")) {
            for (String linea : leer("usuarios.txt")) {
                String[] datos = linea.split(",", -1);
                if ((datos.length != 2 && datos.length != 4) || datos[0].isBlank() || datos[1].isEmpty()) {
                    ignorada("usuarios.txt", "cédula " + datos[0]); // sin imprimir la contraseña
                    continue;
                }
                sql.setString(1, datos[0].trim());
                sql.setString(2, datos[1]);
                sql.setString(3, datos.length == 4 ? datos[2] : null);
                sql.setString(4, datos.length == 4 ? datos[3] : null);
                sql.executeUpdate();
            }
        }
    }

    private void importarMedicamentos(Connection conexion) throws SQLException {
        File archivo = new File(directorio, "Medicamentos.txt");
        if (!archivo.exists()) {
            return;
        }
        List<?> medicamentos;
        try (FileInputStream bytes = new FileInputStream(archivo); ObjectInputStream entrada = new ObjectInputStream(bytes)) {
            entrada.setObjectInputFilter(FILTRO_INVENTARIO);
            medicamentos = (List<?>) entrada.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("No se pudo importar el inventario de " + archivo.getPath() + " (" + e.getMessage()
                    + "); el archivo se conserva sin cambios.");
            return;
        }
        try (PreparedStatement sql = conexion.prepareStatement(
                "INSERT OR IGNORE INTO medicamentos (id, nombre, empresa, precio, cantidad) VALUES (?, ?, ?, ?, ?)")) {
            for (Object elemento : medicamentos) {
                if (!(elemento instanceof MedicamentoDTO m) || m.getNombre() == null || m.getEmpresa() == null
                        || m.getCantidad() < 0) {
                    System.err.println("Medicamento ignorado al importar: " + elemento);
                    continue;
                }
                sql.setLong(1, m.getNumId());
                sql.setString(2, m.getNombre());
                sql.setString(3, m.getEmpresa());
                sql.setFloat(4, m.getPrecio());
                sql.setInt(5, m.getCantidad());
                sql.executeUpdate();
            }
        }
    }

    /** Líneas documento,entidad,medicamento,cantidad[,fecha,total]. */
    private void importarOrdenes(Connection conexion) throws SQLException {
        for (String linea : leer("ordenes.txt")) {
            String[] datos = linea.split(",", -1);
            OrdenDTO orden;
            try {
                if (datos.length != 4 && datos.length != 6) {
                    throw new IllegalArgumentException("se esperaban 4 o 6 campos");
                }
                int cantidad = Integer.parseInt(datos[3].trim());
                if (cantidad <= 0) {
                    throw new IllegalArgumentException("cantidad no positiva");
                }
                LocalDateTime fecha = datos.length == 6 && !datos[4].isBlank() ? LocalDateTime.parse(datos[4].trim()) : null;
                Double total = datos.length == 6 && !datos[5].isBlank() ? Double.valueOf(datos[5].trim()) : null;
                orden = new OrdenDTO(datos[0].trim(), datos[1].trim(), datos[2].trim(), cantidad, fecha, total);
            } catch (IllegalArgumentException | DateTimeParseException e) {
                ignorada("ordenes.txt", linea);
                continue;
            }
            OrdenDAO.insertar(conexion, orden);
        }
    }

    /** Una línea fecha,turnoSiguiente. */
    private void importarTurno(Connection conexion) throws SQLException {
        for (String linea : leer("turnos.txt")) {
            String[] datos = linea.trim().split(",");
            try {
                TurnoDAO.guardar(conexion, LocalDate.parse(datos[0]), Integer.parseInt(datos[1]));
            } catch (DateTimeParseException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                ignorada("turnos.txt", linea);
            }
        }
    }

    private List<String> leer(String nombre) {
        File archivo = new File(directorio, nombre);
        if (!archivo.exists()) {
            return List.of();
        }
        try {
            List<String> lineas = Files.readAllLines(archivo.toPath(), StandardCharsets.UTF_8);
            lineas.removeIf(String::isBlank);
            return lineas;
        } catch (IOException e) {
            System.err.println("No se pudo leer " + archivo.getPath() + ": " + e.getMessage());
            return List.of();
        }
    }

    private static void ignorada(String archivo, String linea) {
        System.err.println("Línea ignorada al importar " + archivo + ": " + linea);
    }
}
