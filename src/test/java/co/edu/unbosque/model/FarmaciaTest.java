package co.edu.unbosque.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import co.edu.unbosque.model.persistence.BaseDeDatos;

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
        assertEquals(List.of(), farmacia.filasOrdenes());
    }

    @Test
    void rechazaOrdenSinStockSuficiente() {
        assertEquals("Stock insuficiente: quedan 5 unidades de Ibuprofeno.",
                farmacia.agregarOrden("1010", "EPS1", "Ibuprofeno", 6));
        assertEquals(List.of("[2, Ibuprofeno, MK, 2000.0, 5]"), texto(farmacia.filasBusqueda("Ibuprofeno")));
        assertEquals(List.of(), farmacia.filasOrdenes());
    }

    @Test
    void ordenValidaDescuentaStockYSePersiste() {
        assertEquals("Orden agregada por $10.000. Stock restante de Ibuprofeno: 0.",
                farmacia.agregarOrden("1010", "EPS1", "Ibuprofeno", 5));

        Farmacia reabierta = new Farmacia(datos, RELOJ);
        assertEquals(List.of("[2, Ibuprofeno, MK, 2000.0, 0]"), texto(reabierta.filasBusqueda("Ibuprofeno")));
        assertEquals(List.of("[2026-11-19T14:30, 1010, EPS1, Ibuprofeno, 5, 10000.0]"), texto(reabierta.filasOrdenes()));
    }

    @Test
    void elInventarioSeListaOrdenadoPorId() {
        farmacia.agregarMedicamento(0, "Aspirina", "Bayer", 800.5f, 3);
        assertEquals(List.of("[0, Aspirina, Bayer, 800.5, 3]", "[1, Paracetamol, Genfar, 1500.0, 20]",
                "[2, Ibuprofeno, MK, 2000.0, 5]"), texto(farmacia.filasMedicamentos()));
    }

    /** Cada fila como texto, para comparar tablas completas en una línea. */
    private static List<String> texto(List<Object[]> filas) {
        return filas.stream().map(Arrays::toString).toList();
    }

    @Test
    void laOrdenGuardaFechaYTotal() throws Exception {
        farmacia.agregarOrden("1010", "EPS1", "PARACETAMOL", 2);
        try (Connection conexion = new BaseDeDatos(datos).conectar();
                ResultSet fila = conexion.createStatement().executeQuery(
                        "SELECT documento, entidad, medicamento, cantidad, fecha, total FROM ordenes")) {
            assertTrue(fila.next());
            assertEquals("1010,EPS1,Paracetamol,2,2026-11-19T14:30,3000.0",
                    String.join(",", fila.getString(1), fila.getString(2), fila.getString(3),
                            String.valueOf(fila.getInt(4)), fila.getString(5), String.valueOf(fila.getDouble(6))),
                    "nombre normalizado, fecha sin segundos y total = precio x cantidad");
            assertFalse(fila.next());
        }
    }

    @Test
    void lasOrdenesAntiguasSeListanSinFechaNiTotal(@TempDir File antigua) throws Exception {
        Files.writeString(Path.of(antigua.getPath(), "ordenes.txt"), "1010,EPS1,Paracetamol,3\n");
        assertEquals(List.of("[null, 1010, EPS1, Paracetamol, 3, null]"),
                texto(new Farmacia(antigua, RELOJ).filasOrdenes()));
    }

    @Test
    void aceptaComasEnLosDatos() {
        farmacia.agregarMedicamento(3, "Vitamina C, 500 mg", "Genfar", 800, 10);
        assertTrue(farmacia.agregarOrden("1010", "EPS1", "Vitamina C, 500 mg", "2").exito());
        assertEquals("Vitamina C, 500 mg", new Farmacia(datos, RELOJ).filasOrdenes().get(0)[3]);
        assertTrue(farmacia.registrarFuncionario("777", "cla,ve", "Ruiz, Ana", "ana@mail.com").exito());
        assertEquals(new Resultado(true, "Ruiz, Ana"), farmacia.iniciarSesion("777", "cla,ve"));
    }

    @Test
    void formatoDePesos() {
        assertEquals("$0", Farmacia.pesos(0));
        assertEquals("$1.500", Farmacia.pesos(1500));
        assertEquals("$1.234.568", Farmacia.pesos(1234567.6));
    }

    @Test
    void top5SinOrdenes() {
        assertEquals(List.of(), farmacia.filasTop5());
    }

    @Test
    void top5SumaPorMedicamentoYOrdenaDeMayorAMenor() {
        farmacia.agregarOrden("1", "E", "Ibuprofeno", 4);
        farmacia.agregarOrden("2", "E", "Paracetamol", 3);
        farmacia.agregarOrden("3", "E", "Paracetamol", 2);
        assertEquals(List.of("[1, Paracetamol, 5]", "[2, Ibuprofeno, 4]"), texto(farmacia.filasTop5()));
    }

    @Test
    void top5MuestraComoMaximoCincoMedicamentos() {
        for (int id = 3; id <= 8; id++) {
            farmacia.agregarMedicamento(id, "Med" + id, "X", 1, 100);
            farmacia.agregarOrden("1", "E", "Med" + id, id);
        }
        List<String> ranking = texto(farmacia.filasTop5());
        assertEquals(5, ranking.size());
        assertEquals("[1, Med8, 8]", ranking.get(0));
        assertEquals("[5, Med4, 4]", ranking.get(4));
    }
}
