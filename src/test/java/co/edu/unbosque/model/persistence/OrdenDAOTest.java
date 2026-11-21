package co.edu.unbosque.model.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OrdenDAOTest {

    @TempDir
    File datos;

    private BaseDeDatos bd;

    @BeforeEach
    void preparar() {
        bd = new BaseDeDatos(datos);
    }

    @Test
    void lasOrdenesSeConservanAlReabrir() {
        new OrdenDAO(bd).agregarOrden(new OrdenDTO("1010", "EPS1", "Paracetamol", 3));
        List<OrdenDTO> ordenes = new OrdenDAO(new BaseDeDatos(datos)).listarOrdenes();
        assertEquals(1, ordenes.size());
        assertEquals("Paracetamol", ordenes.get(0).getNombreMedicamento());
        assertEquals(3, ordenes.get(0).getCantidadMedicamento());
    }

    @Test
    void conservaFechaYTotalYAdmiteOrdenesSinEllos() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 19, 14, 30);
        OrdenDAO dao = new OrdenDAO(bd);
        dao.agregarOrden(new OrdenDTO("1010", "EPS1", "Paracetamol", 2, fecha, 3000.0));
        dao.agregarOrden(new OrdenDTO("2020", "EPS2", "Aspirina", 1));

        List<OrdenDTO> ordenes = dao.listarOrdenes();
        assertEquals(fecha, ordenes.get(0).getFecha());
        assertEquals(3000.0, ordenes.get(0).getTotal());
        assertNull(ordenes.get(1).getFecha(), "orden importada del formato anterior, sin fecha");
        assertNull(ordenes.get(1).getTotal());
    }

    @Test
    void laVentaDescuentaStockYGuardaLaOrden() {
        MedicamentoDAO medicamentos = new MedicamentoDAO(bd);
        medicamentos.crear(new MedicamentoDTO(7, "Ibuprofeno", "MK", 2000, 5));
        OrdenDAO dao = new OrdenDAO(bd);

        assertTrue(dao.registrarVenta(new OrdenDTO("1010", "EPS1", "Ibuprofeno", 5), 7));
        assertEquals(0, medicamentos.buscarPorNombreExacto("Ibuprofeno").getCantidad());
        assertEquals(1, dao.listarOrdenes().size());
    }

    @Test
    void sinStockSuficienteNoSeDescuentaNiSeGuardaNada() {
        MedicamentoDAO medicamentos = new MedicamentoDAO(bd);
        medicamentos.crear(new MedicamentoDTO(7, "Ibuprofeno", "MK", 2000, 5));
        OrdenDAO dao = new OrdenDAO(bd);

        assertFalse(dao.registrarVenta(new OrdenDTO("1010", "EPS1", "Ibuprofeno", 6), 7));
        assertFalse(dao.registrarVenta(new OrdenDTO("1010", "EPS1", "Otro", 1), 99), "medicamento inexistente");
        assertEquals(5, medicamentos.buscarPorNombreExacto("Ibuprofeno").getCantidad());
        assertEquals(0, dao.listarOrdenes().size());
    }

    @Test
    void sinOrdenesLaListaEstaVacia() {
        assertEquals(0, new OrdenDAO(bd).listarOrdenes().size());
    }
}
