package co.edu.unbosque.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Numeración de turnos de atención. Con archivo, el turno siguiente se conserva al cerrar la
 * aplicación y la numeración vuelve a 1 cada día (formato del archivo: fecha,turnoSiguiente).
 */
public class Turnos {
    private final File archivo;
    private final Clock reloj;
    private LocalDate dia;
    private int turnoActual;

    /** Turnos solo en memoria: empiezan en 1 cada vez que se abre la aplicación. */
    public Turnos() {
        this(null, Clock.systemDefaultZone());
    }

    public Turnos(File archivo, Clock reloj) {
        this.archivo = archivo;
        this.reloj = reloj;
        this.dia = LocalDate.now(reloj);
        this.turnoActual = 1;
        cargar();
    }

    public int generarTurno() {
        LocalDate hoy = LocalDate.now(reloj);
        if (!hoy.equals(dia)) { // nuevo día: la numeración empieza de nuevo
            dia = hoy;
            turnoActual = 1;
        }
        int turno = turnoActual;
        turnoActual++;
        guardar();
        return turno;
    }

    public void reiniciarTurnos() {
        turnoActual = 1;
        guardar();
    }

    private void cargar() {
        if (archivo == null || !archivo.exists()) {
            return;
        }
        try {
            String[] datos = Files.readString(archivo.toPath(), StandardCharsets.UTF_8).trim().split(",");
            if (datos.length == 2 && LocalDate.parse(datos[0]).equals(dia)) {
                turnoActual = Math.max(1, Integer.parseInt(datos[1]));
            }
        } catch (IOException | DateTimeParseException | NumberFormatException e) {
            System.err.println("No se pudo leer el turno guardado (" + e.getMessage() + "); se empieza en 1.");
        }
    }

    private void guardar() {
        if (archivo == null) {
            return;
        }
        try {
            File carpeta = archivo.getParentFile();
            if (carpeta != null) {
                carpeta.mkdirs();
            }
            Files.writeString(archivo.toPath(), dia + "," + turnoActual, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("No se pudo guardar el turno: " + e.getMessage());
        }
    }
}
