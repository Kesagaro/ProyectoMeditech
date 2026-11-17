package co.edu.unbosque.model.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MedicamentoDAOTest {

    @TempDir
    File datos;

    private MedicamentoDTO paracetamol() {
        return new MedicamentoDTO(1, "Paracetamol", "Genfar", 1500, 20);
    }

    @Test
    void loCreadoSeConservaAlReabrir() {
        new MedicamentoDAO(datos).crear(paracetamol());
        MedicamentoDAO reabierto = new MedicamentoDAO(datos);
        assertTrue(reabierto.existeId(1));
        assertEquals(20, reabierto.buscarPorNombreExacto("Paracetamol").getCantidad());
    }

    @Test
    void actualizaYEliminaPorId() {
        MedicamentoDAO dao = new MedicamentoDAO(datos);
        dao.crear(paracetamol());
        assertEquals("Medicamento actualizado correctamente.",
                dao.actualizarPorId(1, new MedicamentoDTO(1, "Paracetamol", "MK", 1800, 10)));
        assertEquals("MK", dao.buscarPorNombreExacto("paracetamol").getEmpresa());
        assertEquals("Medicamento no encontrado.", dao.actualizarPorId(9, paracetamol()));

        assertEquals("Medicamento eliminado correctamente.", dao.eliminarPorId(1));
        assertEquals("Medicamento no encontrado.", dao.eliminarPorId(1));
        assertFalse(new MedicamentoDAO(datos).existeId(1));
    }

    @Test
    void buscaPorNombreSinDistinguirMayusculas() {
        MedicamentoDAO dao = new MedicamentoDAO(datos);
        dao.crear(paracetamol());
        dao.crear(new MedicamentoDTO(2, "Ibuprofeno", "MK", 2000, 5));
        assertEquals(1, dao.buscarPorNombre("PARA").size());
        assertEquals(0, dao.buscarPorNombre("xyz").size());
        assertNull(dao.buscarPorNombreExacto("Para"), "la búsqueda exacta no acepta nombres parciales");
    }

    @Test
    void descontarStockSePersiste() {
        MedicamentoDAO dao = new MedicamentoDAO(datos);
        dao.crear(paracetamol());
        dao.descontarStock(dao.buscarPorNombreExacto("Paracetamol"), 8);
        assertEquals(12, new MedicamentoDAO(datos).buscarPorNombreExacto("Paracetamol").getCantidad());
    }

    @Test
    void unArchivoDanadoSeApartaEnLugarDeSobrescribirse() throws Exception {
        File archivo = new File(datos, "Medicamentos.txt");
        Files.writeString(archivo.toPath(), "esto no es un inventario serializado");

        MedicamentoDAO dao = new MedicamentoDAO(datos);
        assertEquals("", dao.mostrar());
        assertFalse(archivo.exists());
        String[] respaldos = datos.list((carpeta, nombre) -> nombre.startsWith("Medicamentos.danado-"));
        assertEquals(1, respaldos.length);
        assertEquals("esto no es un inventario serializado",
                Files.readString(new File(datos, respaldos[0]).toPath()));
    }
}
