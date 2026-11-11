package co.edu.unbosque.controller;

import co.edu.unbosque.view.View;

public class Main {
    public static void main(String[] args) {
        View view = new View(); // Crear la vista
        Controller c = new Controller(view); // Crear el controlador
    }
}