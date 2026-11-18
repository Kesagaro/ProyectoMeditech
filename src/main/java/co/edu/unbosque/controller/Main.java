package co.edu.unbosque.controller;

import co.edu.unbosque.view.View;

import javax.swing.UIManager;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Botones de los diálogos en español, sin importar el idioma del sistema
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "Aceptar");
        View view = new View(); // Crear la vista
        new Controller(view, carpetaDatos());
    }

    /**
     * Carpeta de datos: junto al .jar cuando la aplicación se ejecuta empaquetada (así funciona
     * con doble clic sin importar la carpeta actual); en desarrollo, data/ en la carpeta actual.
     */
    public static File carpetaDatos() {
        try {
            File origen = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (origen.isFile()) {
                return new File(origen.getParentFile(), "data");
            }
        } catch (Exception e) {
            // si no se puede saber de dónde se cargó la clase, se usa la carpeta actual
        }
        return new File("data");
    }
}