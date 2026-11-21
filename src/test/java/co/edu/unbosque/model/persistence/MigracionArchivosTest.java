package co.edu.unbosque.model.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Importación de los archivos de las versiones anteriores al crear la base de datos. */
class MigracionArchivosTest {

    @TempDir
    File datos;

    private void escribir(String archivo, String contenido) throws Exception {
        Files.writeString(new File(datos, archivo).toPath(), contenido);
    }

    private void serializar(Object inventario) throws Exception {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(new File(datos, "Medicamentos.txt")))) {
            salida.writeObject(inventario);
        }
    }

    @Test
    void importaLosCuatroArchivosAlCrearLaBase() throws Exception {
        escribir("usuarios.txt", "112233,112233\n777,pbkdf2$1$c2Fs$aGFzaA==,Ana Ruiz,ana@mail.com\n");
        serializar(new ArrayList<>(List.of(new MedicamentoDTO(1, "Paracetamol", "Genfar", 1500, 20),
                new MedicamentoDTO(2, "Ibuprofeno", "MK", 2000, 5))));
        escribir("ordenes.txt", "1010,EPS1,Paracetamol,2,2026-11-19T14:30,3000.0\n2020,EPS2,Ibuprofeno,1\n");
        escribir("turnos.txt", "2026-11-19,8");

        BaseDeDatos bd = new BaseDeDatos(datos);

        FuncionariosDAO funcionarios = new FuncionariosDAO(bd);
        assertTrue(funcionarios.verificarUsuario("112233", "112233"));
        assertEquals("Ana Ruiz", funcionarios.buscarNombre("777"));

        MedicamentoDAO medicamentos = new MedicamentoDAO(bd);
        assertEquals(20, medicamentos.buscarPorNombreExacto("Paracetamol").getCantidad());
        assertEquals(2000f, medicamentos.buscarPorNombreExacto("Ibuprofeno").getPrecio());

        List<OrdenDTO> ordenes = new OrdenDAO(bd).listarOrdenes();
        assertEquals(2, ordenes.size());
        assertEquals(LocalDateTime.of(2026, 11, 19, 14, 30), ordenes.get(0).getFecha());
        assertEquals(3000.0, ordenes.get(0).getTotal());
        assertNull(ordenes.get(1).getFecha());

        assertEquals(8, new TurnoDAO(bd).turnoSiguiente(LocalDate.of(2026, 11, 19)));
    }

    @Test
    void losArchivosAntiguosQuedanIntactosComoRespaldo() throws Exception {
        escribir("ordenes.txt", "1010,EPS1,Paracetamol,2\n");
        new BaseDeDatos(datos);
        assertEquals("1010,EPS1,Paracetamol,2\n", Files.readString(new File(datos, "ordenes.txt").toPath()));
    }

    @Test
    void soloImportaUnaVez() throws Exception {
        escribir("ordenes.txt", "1010,EPS1,Paracetamol,2\n");
        new BaseDeDatos(datos);
        BaseDeDatos reabierta = new BaseDeDatos(datos);
        assertEquals(1, new OrdenDAO(reabierta).listarOrdenes().size(), "reabrir no duplica las órdenes");
    }

    @Test
    void ignoraLineasDanadasEImportaLasDemas() throws Exception {
        escribir("usuarios.txt", "solo-cedula\n,sin,cedula,x\n900,clave9,Viejo,v@x.co\n");
        escribir("ordenes.txt", "1,E,Paracetamol,3\na,b,c,x\nsolo,dos\n\n2,E,Aspirina,-2\n"
                + "3,E,Ibuprofeno,4,ayer,100\n4,E,Aspirina,1,2026-11-19T10:00,mucho\n5,E,Naproxeno,4,2026-11-19T10:00,800.0\n");
        escribir("turnos.txt", "esto no es un turno");

        BaseDeDatos bd = new BaseDeDatos(datos);

        List<FuncionarioDTO> funcionarios = new FuncionariosDAO(bd).listar();
        assertEquals(1, funcionarios.size());
        assertEquals("FuncionarioDTO[cedula=900, nombre=Viejo, correo=v@x.co]", funcionarios.get(0).toString());
        List<OrdenDTO> ordenes = new OrdenDAO(bd).listarOrdenes();
        assertEquals(2, ordenes.size());
        assertEquals("Paracetamol", ordenes.get(0).getNombreMedicamento());
        assertEquals("Naproxeno", ordenes.get(1).getNombreMedicamento());
        assertEquals(1, new TurnoDAO(bd).turnoSiguiente(LocalDate.of(2026, 11, 19)));
    }

    @Test
    void unInventarioDanadoNoImpideArrancarNiSeBorra() throws Exception {
        escribir("Medicamentos.txt", "esto no es un inventario serializado");
        escribir("ordenes.txt", "1,E,Paracetamol,3\n");

        BaseDeDatos bd = new BaseDeDatos(datos);

        assertEquals("", new MedicamentoDAO(bd).mostrar());
        assertEquals(1, new OrdenDAO(bd).listarOrdenes().size(), "el resto se importa igual");
        assertEquals("esto no es un inventario serializado",
                Files.readString(new File(datos, "Medicamentos.txt").toPath()));
    }

    @Test
    void rechazaClasesQueNoSonDelInventario() throws Exception {
        serializar(new ArrayList<>(List.of(new Date())));
        BaseDeDatos bd = new BaseDeDatos(datos);
        assertEquals("", new MedicamentoDAO(bd).mostrar());
    }

    @Test
    void sinArchivosAntiguosLaBaseEmpiezaVacia() {
        BaseDeDatos bd = new BaseDeDatos(datos);
        assertTrue(new File(datos, BaseDeDatos.ARCHIVO).exists());
        assertEquals(List.of(), new FuncionariosDAO(bd).listar());
        assertFalse(new MedicamentoDAO(bd).existeId(1));
        assertEquals(0, new OrdenDAO(bd).listarOrdenes().size());
    }
}
