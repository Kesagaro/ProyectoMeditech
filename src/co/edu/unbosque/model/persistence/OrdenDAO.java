package co.edu.unbosque.model.persistence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OrdenDAO {
	private static final String DIRECTORY = "data";
    private static final String FILE_PATH = DIRECTORY + "/ordenes.txt";
    private List<OrdenDTO> ordenes;

    public OrdenDAO() {
        ordenes = new ArrayList<>();
        cargarOrdenesDesdeArchivo();
    }

    public void agregarOrden(OrdenDTO orden) {
        ordenes.add(orden);
        guardarOrdenEnArchivo(orden);
    }

    public List<OrdenDTO> listarOrdenes() {
        return ordenes;
    }

    private void guardarOrdenEnArchivo(OrdenDTO orden) {
        try {
            File directory = new File(DIRECTORY);
            if (!directory.exists()) {
                directory.mkdir();
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, StandardCharsets.UTF_8, true))) {
                bw.write(orden.getDocumentoPaciente() + "," + orden.getCodigoEntidad() + ","
                        + orden.getNombreMedicamento() + "," + orden.getCantidadMedicamento());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar la orden en el archivo: " + e.getMessage());
        }
    }

    private void cargarOrdenesDesdeArchivo() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String linea;
            int numero = 0;
            while ((linea = br.readLine()) != null) {
                numero++;
                if (linea.isBlank()) continue;
                String[] datos = linea.split(",", -1);
                try {
                    if (datos.length != 4) throw new IllegalArgumentException("se esperaban 4 campos");
                    int cantidad = Integer.parseInt(datos[3].trim());
                    if (cantidad <= 0) throw new IllegalArgumentException("cantidad no positiva");
                    ordenes.add(new OrdenDTO(datos[0].trim(), datos[1].trim(), datos[2].trim(), cantidad));
                } catch (IllegalArgumentException e) {
                    // una línea dañada no impide cargar las demás
                    System.err.println("Orden ignorada en la línea " + numero + " de " + FILE_PATH + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar las órdenes desde el archivo: " + e.getMessage());
        }
    }

}
