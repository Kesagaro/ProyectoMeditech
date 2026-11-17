package co.edu.unbosque.model.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Files;
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
    void sinArchivoNoHayOrdenes() {
        assertEquals(0, new OrdenDAO(datos).listarOrdenes().size());
    }
}
