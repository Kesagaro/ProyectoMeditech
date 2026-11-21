package co.edu.unbosque.model.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MedicamentoDAOTest {

    @TempDir
    File datos;

    private MedicamentoDAO abrir() {
        return new MedicamentoDAO(new BaseDeDatos(datos));
    }

    private MedicamentoDTO paracetamol() {
        return new MedicamentoDTO(1, "Paracetamol", "Genfar", 1500, 20);
    }

    @Test
    void loCreadoSeConservaAlReabrir() {
        abrir().crear(paracetamol());
        MedicamentoDAO reabierto = abrir();
        assertTrue(reabierto.existeId(1));
        assertEquals(20, reabierto.buscarPorNombreExacto("Paracetamol").getCantidad());
        assertEquals(1500f, reabierto.buscarPorNombreExacto("Paracetamol").getPrecio());
    }

    @Test
    void actualizaYEliminaPorId() {
        MedicamentoDAO dao = abrir();
        dao.crear(paracetamol());
        assertEquals("Medicamento actualizado correctamente.",
                dao.actualizarPorId(1, new MedicamentoDTO(1, "Paracetamol", "MK", 1800, 10)));
        assertEquals("MK", dao.buscarPorNombreExacto("paracetamol").getEmpresa());
        assertEquals("Medicamento no encontrado.", dao.actualizarPorId(9, paracetamol()));

        assertEquals("Medicamento eliminado correctamente.", dao.eliminarPorId(1));
        assertEquals("Medicamento no encontrado.", dao.eliminarPorId(1));
        assertFalse(abrir().existeId(1));
    }

    @Test
    void buscaPorNombreSinDistinguirMayusculas() {
        MedicamentoDAO dao = abrir();
        dao.crear(paracetamol());
        dao.crear(new MedicamentoDTO(2, "Ibuprofeno", "MK", 2000, 5));
        dao.crear(new MedicamentoDTO(3, "Ácido fólico", "MK", 900, 5));
        assertEquals(1, dao.buscarPorNombre("PARA").size());
        assertEquals(0, dao.buscarPorNombre("xyz").size());
        assertEquals(1, dao.buscarPorNombre("ÁCIDO").size(), "también con tildes");
        assertEquals(3, dao.buscarPorNombreExacto("ácido FÓLICO").getNumId());
        assertNull(dao.buscarPorNombreExacto("Para"), "la búsqueda exacta no acepta nombres parciales");
    }

    @Test
    void elListadoVaOrdenadoPorId() {
        MedicamentoDAO dao = abrir();
        dao.crear(new MedicamentoDTO(2, "Ibuprofeno", "MK", 2000, 5));
        dao.crear(paracetamol());
        String listado = dao.mostrar();
        assertTrue(listado.indexOf("Paracetamol") < listado.indexOf("Ibuprofeno"));
        assertTrue(listado.contains("Precio: 1500.0\nCantidad: 20\n"));
    }

    @Test
    void sinMedicamentosElListadoEstaVacio() {
        assertEquals("", abrir().mostrar());
    }
}
