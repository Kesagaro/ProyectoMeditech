package co.edu.unbosque.model;

import co.edu.unbosque.model.persistence.TurnoDAO;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Numeración de turnos de atención. Con base de datos, el turno siguiente se conserva al cerrar
 * la aplicación; la numeración vuelve a 1 cada día.
 */
public class Turnos {
    private final TurnoDAO dao;
    private final Clock reloj;
    private LocalDate dia;
    private int turnoActual;

    /** Turnos solo en memoria: empiezan en 1 cada vez que se abre la aplicación. */
    public Turnos() {
        this(null, Clock.systemDefaultZone());
    }

    public Turnos(TurnoDAO dao, Clock reloj) {
        this.dao = dao;
        this.reloj = reloj;
        this.dia = LocalDate.now(reloj);
        this.turnoActual = dao == null ? 1 : dao.turnoSiguiente(dia);
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

    private void guardar() {
        if (dao != null) {
            dao.guardar(dia, turnoActual);
        }
    }
}
