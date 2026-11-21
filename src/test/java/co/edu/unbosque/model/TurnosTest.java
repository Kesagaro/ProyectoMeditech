package co.edu.unbosque.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.edu.unbosque.model.persistence.BaseDeDatos;
import co.edu.unbosque.model.persistence.TurnoDAO;

import java.io.File;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TurnosTest {

    @Test
    void elPrimerTurnoEsUno() {
        assertEquals(1, new Turnos().generarTurno());
    }

    @Test
    void losTurnosSonConsecutivos() {
        Turnos turnos = new Turnos();
        turnos.generarTurno();
        turnos.generarTurno();
        assertEquals(3, turnos.generarTurno());
    }

    @Test
    void elTurnoSeConservaAlReabrirElMismoDia(@TempDir File datos) {
        Clock hoy = reloj("2026-11-19T15:00:00Z");
        Turnos turnos = new Turnos(dao(datos), hoy);
        turnos.generarTurno();
        turnos.generarTurno();

        assertEquals(3, new Turnos(dao(datos), hoy).generarTurno(), "sigue la numeración tras cerrar la app");
    }

    @Test
    void laNumeracionVuelveAUnoAlDiaSiguiente(@TempDir File datos) {
        new Turnos(dao(datos), reloj("2026-11-19T15:00:00Z")).generarTurno();

        assertEquals(1, new Turnos(dao(datos), reloj("2026-11-20T15:00:00Z")).generarTurno());
    }

    @Test
    void cambiaDeDiaConLaAplicacionAbierta(@TempDir File datos) {
        MutableClock reloj = new MutableClock(Instant.parse("2026-11-19T23:00:00Z"));
        Turnos turnos = new Turnos(dao(datos), reloj);
        assertEquals(1, turnos.generarTurno());
        assertEquals(2, turnos.generarTurno());
        reloj.instante = Instant.parse("2026-11-20T13:00:00Z");
        assertEquals(1, turnos.generarTurno());
    }

    @Test
    void reiniciarTambienSeGuarda(@TempDir File datos) {
        Clock hoy = reloj("2026-11-19T15:00:00Z");
        Turnos turnos = new Turnos(dao(datos), hoy);
        turnos.generarTurno();
        turnos.generarTurno();
        turnos.reiniciarTurnos();
        assertEquals(1, new Turnos(dao(datos), hoy).generarTurno());
    }

    /** Cada llamada abre la base otra vez, como al reiniciar la aplicación. */
    private static TurnoDAO dao(File datos) {
        return new TurnoDAO(new BaseDeDatos(datos));
    }

    private static Clock reloj(String instante) {
        return Clock.fixed(Instant.parse(instante), ZoneId.of("America/Bogota"));
    }

    /** Reloj que se puede adelantar dentro de una prueba. */
    private static class MutableClock extends Clock {
        Instant instante;

        MutableClock(Instant instante) {
            this.instante = instante;
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("America/Bogota");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instante;
        }
    }

    @Test
    void reiniciarVuelveAlTurnoUno() {
        Turnos turnos = new Turnos();
        turnos.generarTurno();
        turnos.generarTurno();
        turnos.reiniciarTurnos();
        assertEquals(1, turnos.generarTurno());
    }
}
