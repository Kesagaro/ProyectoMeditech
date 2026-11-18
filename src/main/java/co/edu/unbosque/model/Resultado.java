package co.edu.unbosque.model;

/**
 * Respuesta de una operación del modelo: si se pudo hacer y el mensaje para mostrar al usuario.
 */
public record Resultado(boolean exito, String mensaje) {

    public static Resultado ok(String mensaje) {
        return new Resultado(true, mensaje);
    }

    public static Resultado error(String mensaje) {
        return new Resultado(false, mensaje);
    }
}
