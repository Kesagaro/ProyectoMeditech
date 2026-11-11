package co.edu.unbosque.model.persistence;

import java.io.*;
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

            FileWriter writer = new FileWriter(FILE_PATH, true);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(orden.getDocumentoPaciente() + "," + orden.getCodigoEntidad() + "," + 
                     orden.getNombreMedicamento() + "," + orden.getCantidadMedicamento());
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.err.println("Error al guardar la orden en el archivo: " + e.getMessage());
        }
    }

    private void cargarOrdenesDesdeArchivo() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 4) {
                    String documento = datos[0];
                    String codigoEntidad = datos[1];
                    String nombreMedicamento = datos[2];
                    int cantidad = Integer.parseInt(datos[3]);

                    OrdenDTO orden = new OrdenDTO(documento, codigoEntidad, nombreMedicamento, cantidad);
                    ordenes.add(orden);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar las órdenes desde el archivo: " + e.getMessage());
        }
    }

}
