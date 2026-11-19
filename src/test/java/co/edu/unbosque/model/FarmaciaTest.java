package co.edu.unbosque.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FarmaciaTest {

    /** 19/11/2026 a las 14:30:45 en Bogotá. */
    static final Clock RELOJ = Clock.fixed(Instant.parse("2026-11-19T19:30:45Z"), ZoneId.of("America/Bogota"));

    @TempDir
    File datos;

    private Farmacia farmacia;

    @BeforeEach
    void preparar() {
        farmacia = new Farmacia(datos, RELOJ);
        farmacia.agregarMedicamento(1, "Paracetamol", "Genfar", 1500, 20);
        farmacia.agregarMedicamento(2, "Ibuprofeno", "MK", 2000, 5);
    }

    @Test
    void noPermiteDosMedicamentosConElMismoId() {
        assertEquals("Ya existe un medicamento con el ID 1.",
                farmacia.agregarMedicamento(1, "Otro", "X", 1, 1));
    }

    @Test
    void rechazaOrdenDeMedicamentoInexistente() {
        assertEquals("No existe el medicamento \"Amoxicilina\" en el inventario.",
                farmacia.agregarOrden("1010", "EPS1", "Amoxicilina", 1));
        assertEquals("No hay órdenes registradas.", farmacia.listarOrdenes());
    }

    @Test
    void rechazaOrdenSinStockSuficiente() {
        assertEquals("Stock insuficiente: quedan 5 unidades de Ibuprofeno.",
                farmacia.agregarOrden("1010", "EPS1", "Ibuprofeno", 6));
        assertTrue(farmacia.buscarMedicamentos("Ibuprofeno").mensaje().contains("Stock: 5"));
        assertEquals("No hay órdenes registradas.", farmacia.listarOrdenes());
    }

    @Test
    void ordenValidaDescuentaStockYSePersiste() {
        assertEquals("Orden agregada por $10.000. Stock restante de Ibuprofeno: 0.",
                farmacia.agregarOrden("1010", "EPS1", "Ibuprofeno", 5));

        Farmacia reabierta = new Farmacia(datos, RELOJ);
        assertTrue(reabierta.buscarMedicamentos("Ibuprofeno").mensaje().contains("Stock: 0"));
        assertEquals("19/11/2026 14:30 | Documento: 1010 | Entidad: EPS1 | Ibuprofeno x 5 | Total: $10.000\n",
                reabierta.listarOrdenes());
    }

    @Test
    void laOrdenGuardaFechaYTotal() throws Exception {
        farmacia.agregarOrden("1010", "EPS1", "PARACETAMOL", 2);
        List<String> lineas = Files.readAllLines(Path.of(datos.getPath(), "ordenes.txt"));
        assertEquals(List.of("1010,EPS1,Paracetamol,2,2026-11-19T14:30,3000.0"), lineas,
                "nombre normalizado, fecha sin segundos y total = precio x cantidad");
    }

    @Test
    void lasOrdenesAntiguasSeListanSinFechaNiTotal() throws Exception {
        Files.writeString(Path.of(datos.getPath(), "ordenes.txt"), "1010,EPS1,Paracetamol,3\n");
        assertEquals("Sin fecha | Documento: 1010 | Entidad: EPS1 | Paracetamol x 3 | Total: -\n",
                new Farmacia(datos, RELOJ).listarOrdenes());
    }

    @Test
    void formatoDePesos() {
        assertEquals("$0", Farmacia.pesos(0));
        assertEquals("$1.500", Farmacia.pesos(1500));
        assertEquals("$1.234.568", Farmacia.pesos(1234567.6));
    }

    @Test
    void top5SinOrdenes() {
        assertEquals("Aún no hay órdenes registradas.", farmacia.top5Medicamentos());
    }

    @Test
    void top5SumaPorMedicamentoYOrdenaDeMayorAMenor() {
        farmacia.agregarOrden("1", "E", "Ibuprofeno", 4);
        farmacia.agregarOrden("2", "E", "Paracetamol", 3);
        farmacia.agregarOrden("3", "E", "Paracetamol", 2);
        assertEquals("Top 5 medicamentos más expendidos:\n\n"
                + "1. Paracetamol - Cantidad expendida: 5\n"
                + "2. Ibuprofeno - Cantidad expendida: 4\n", farmacia.top5Medicamentos());
    }

    @Test
    void top5MuestraComoMaximoCincoMedicamentos() {
        for (int id = 3; id <= 8; id++) {
            farmacia.agregarMedicamento(id, "Med" + id, "X", 1, 100);
            farmacia.agregarOrden("1", "E", "Med" + id, id);
        }
        String reporte = farmacia.top5Medicamentos();
        assertTrue(reporte.contains("1. Med8 - Cantidad expendida: 8"));
        assertTrue(reporte.contains("5. Med4 - Cantidad expendida: 4"));
        assertFalse(reporte.contains("Med3"));
    }
}
