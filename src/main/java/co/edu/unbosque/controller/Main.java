package co.edu.unbosque.controller;

import co.edu.unbosque.view.View;

import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Botones de los diálogos en español, sin importar el idioma del sistema
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "Aceptar");
        View view = new View(); // Crear la vista
        new Controller(view);
    }
}