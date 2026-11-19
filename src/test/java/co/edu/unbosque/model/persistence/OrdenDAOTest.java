package co.edu.unbosque.model.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OrdenDAOTest {

    @TempDir
    File datos;

    @Test
    void lasOrdenesSeConservanAlReabrir() {
        new OrdenDAO(datos).agregarOrden(new OrdenDTO("1010", "EPS1", "Paracetamol", 3));
        List<OrdenDTO> ordenes = new OrdenDAO(datos).listarOrdenes();
        assertEquals(1, ordenes.size());
        assertEquals("Paracetamol", ordenes.get(0).getNombreMedicamento());
        assertEquals(3, ordenes.get(0).getCantidadMedicamento());
    }

    @Test
    void ignoraLineasDanadasYCargaLasDemas() throws Exception {
        Files.writeString(new File(datos, "ordenes.txt").toPath(),
                "1,E,Paracetamol,3\na,b,c,x\nsolo,dos\n\n2,E,Aspirina,-2\n3,E,Ibuprofeno,4\n");
        List<OrdenDTO> ordenes = new OrdenDAO(datos).listarOrdenes();
        assertEquals(2, ordenes.size());
        assertEquals("Paracetamol", ordenes.get(0).getNombreMedicamento());
        assertEquals("Ibuprofeno", ordenes.get(1).getNombreMedicamento());
    }

    @Test
    void conservaFechaYTotalYLeeLineasAntiguas() throws Exception {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 19, 14, 30);
        new OrdenDAO(datos).agregarOrden(new OrdenDTO("1010", "EPS1", "Paracetamol", 2, fecha, 3000.0));
        Files.writeString(new File(datos, "ordenes.txt").toPath(), "2020,EPS2,Aspirina,1\n", StandardOpenOption.APPEND);

        List<OrdenDTO> ordenes = new OrdenDAO(datos).listarOrdenes();
        assertEquals(fecha, ordenes.get(0).getFecha());
        assertEquals(3000.0, ordenes.get(0).getTotal());
        assertNull(ordenes.get(1).getFecha(), "la línea antigua no tiene fecha");
        assertNull(ordenes.get(1).getTotal());
    }

    @Test
    void ignoraFechaOTotalInvalidos() throws Exception {
        Files.writeString(new File(datos, "ordenes.txt").toPath(),
                "1,E,Paracetamol,3,ayer,100\n2,E,Aspirina,1,2026-11-19T10:00,mucho\n3,E,Ibuprofeno,4,2026-11-19T10:00,800.0\n");
        List<OrdenDTO> ordenes = new OrdenDAO(datos).listarOrdenes();
        assertEquals(1, ordenes.size());
        assertEquals("Ibuprofeno", ordenes.get(0).getNombreMedicamento());
    }

    @Test
    void sinArchivoNoHayOrdenes() {
        assertEquals(0, new OrdenDAO(datos).listarOrdenes().size());
    }
}
