package co.edu.unbosque.model;

public class Turnos {
    private int turnoActual;

    public Turnos() {
        turnoActual = 0; // Inicia el turno en 1
    }

    public int generarTurno() {
        int turno = turnoActual;
        turnoActual++; // Incrementa el turno para la siguiente vez
        return turno;
    }

    public void reiniciarTurnos() {
        turnoActual = 1; // Reinicia el turno a 1 cuando sea necesario
    }
}
