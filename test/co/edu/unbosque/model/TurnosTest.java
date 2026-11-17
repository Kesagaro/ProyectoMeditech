package co.edu.unbosque.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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
    void reiniciarVuelveAlTurnoUno() {
        Turnos turnos = new Turnos();
        turnos.generarTurno();
        turnos.generarTurno();
        turnos.reiniciarTurnos();
        assertEquals(1, turnos.generarTurno());
    }
}
