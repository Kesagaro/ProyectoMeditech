package co.edu.unbosque.model.persistence;

/** Falla al leer o escribir la base de datos (archivo bloqueado, disco lleno, sin permisos...). */
public class ErrorDeDatos extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ErrorDeDatos(String mensaje, Throwable causa) {
        super(mensaje + ": " + causa.getMessage(), causa);
    }
}
