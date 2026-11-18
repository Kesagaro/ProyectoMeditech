package co.edu.unbosque.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Reglas que se aplican a lo que el usuario escribe en cada pantalla. */
class ValidacionesTest {

    @TempDir
    File datos;

    private Farmacia farmacia;

    @BeforeEach
    void preparar() {
        farmacia = new Farmacia(datos);
    }

    private static void error(String mensajeEsperado, Resultado resultado) {
        assertFalse(resultado.exito(), "debía rechazarse: " + resultado.mensaje());
        assertEquals(mensajeEsperado, resultado.mensaje());
    }

    // ---------------------------------------------------------------- inventario

    @ParameterizedTest(name = "[{index}] id=''{0}'' nombre=''{1}'' empresa=''{2}'' precio=''{3}'' cantidad=''{4}''")
    @CsvSource({
            "'', Paracetamol, Genfar, 1500, 20",
            "1, '  ', Genfar, 1500, 20",
            "1, Paracetamol, '', 1500, 20",
            "1, Paracetamol, Genfar, '', 20",
            "1, Paracetamol, Genfar, 1500, ''",
    })
    void medicamentoConCamposVacios(String id, String nombre, String empresa, String precio, String cantidad) {
        error("Todos los campos son obligatorios.",
                farmacia.guardarMedicamento(id, nombre, empresa, precio, cantidad, false));
    }

    @ParameterizedTest
    @CsvSource({ "x, 1500, 20", "1, abc, 20", "1, 1500, 2.5" })
    void medicamentoConNumerosInvalidos(String id, String precio, String cantidad) {
        error("ID, precio y cantidad deben ser números válidos.",
                farmacia.guardarMedicamento(id, "Paracetamol", "Genfar", precio, cantidad, false));
    }

    @ParameterizedTest
    @CsvSource({ "-1, 20", "1500, -3" })
    void medicamentoConValoresNegativos(String precio, String cantidad) {
        error("El precio y la cantidad no pueden ser negativos.",
                farmacia.guardarMedicamento("1", "Paracetamol", "Genfar", precio, cantidad, false));
    }

    @Test
    void agregarYActualizarMedicamento() {
        Resultado agregado = farmacia.guardarMedicamento(" 1 ", " Paracetamol ", "Genfar", "1500", "20", false);
        assertTrue(agregado.exito());
        assertEquals("Medicamento agregado con éxito.", agregado.mensaje());
        assertTrue(farmacia.buscarMedicamentos("Paracetamol").mensaje().startsWith("ID 1 | Paracetamol | Genfar"),
                "los espacios alrededor de los datos se recortan");

        error("Ya existe un medicamento con el ID 1.",
                farmacia.guardarMedicamento("1", "Otro", "X", "1", "1", false));

        Resultado actualizado = farmacia.guardarMedicamento("1", "Paracetamol", "MK", "1800", "10", true);
        assertTrue(actualizado.exito());
        assertEquals("Medicamento actualizado correctamente.", actualizado.mensaje());
        error("Medicamento no encontrado.", farmacia.guardarMedicamento("9", "X", "Y", "1", "1", true));
    }

    @Test
    void eliminarMedicamentoValidaElId() {
        error("Ingrese el ID del medicamento a eliminar.", farmacia.validarIdMedicamento("  "));
        error("El ID debe ser un número válido.", farmacia.validarIdMedicamento("x"));
        assertTrue(farmacia.validarIdMedicamento(" 7 ").exito());

        farmacia.guardarMedicamento("7", "Aspirina", "Bayer", "800", "5", false);
        error("Medicamento no encontrado.", farmacia.eliminarMedicamento("8"));
        assertEquals(new Resultado(true, "Medicamento eliminado correctamente."), farmacia.eliminarMedicamento("7"));
    }

    @Test
    void buscarSinTextoSeRechaza() {
        error("Ingrese el nombre del medicamento a buscar.", farmacia.buscarMedicamentos(" "));
        assertEquals(new Resultado(true, "No se encontraron medicamentos con \"xyz\"."),
                farmacia.buscarMedicamentos(" xyz "));
    }

    // ---------------------------------------------------------------- órdenes

    @ParameterizedTest
    @CsvSource({
            "'', EPS1, Paracetamol, 1, Todos los campos son obligatorios.",
            "1010, EPS1, Paracetamol, '', Todos los campos son obligatorios.",
            "'10,10', EPS1, Paracetamol, 1, Los datos no pueden contener comas.",
            "1010, EPS1, 'Para,cetamol', 1, Los datos no pueden contener comas.",
            "1010, EPS1, Paracetamol, x, La cantidad debe ser un número entero.",
            "1010, EPS1, Paracetamol, 1.5, La cantidad debe ser un número entero.",
            "1010, EPS1, Paracetamol, 0, La cantidad debe ser mayor que cero.",
            "1010, EPS1, Paracetamol, -2, La cantidad debe ser mayor que cero.",
    })
    void ordenInvalida(String documento, String entidad, String medicamento, String cantidad, String mensaje) {
        farmacia.guardarMedicamento("1", "Paracetamol", "Genfar", "1500", "20", false);
        error(mensaje, farmacia.agregarOrden(documento, entidad, medicamento, cantidad));
        assertEquals("No hay órdenes registradas.", farmacia.listarOrdenes(), "no debe guardarse la orden");
    }

    @Test
    void ordenValidaDesdeElFormulario() {
        farmacia.guardarMedicamento("1", "Paracetamol", "Genfar", "1500", "20", false);
        Resultado orden = farmacia.agregarOrden(" 1010 ", "EPS1", " paracetamol ", " 5 ");
        assertTrue(orden.exito());
        assertEquals("Orden agregada. Stock restante de Paracetamol: 15.", orden.mensaje());
        error("Stock insuficiente: quedan 15 unidades de Paracetamol.",
                farmacia.agregarOrden("1010", "EPS1", "Paracetamol", "16"));
    }

    // ---------------------------------------------------------------- funcionarios

    @ParameterizedTest
    @CsvSource({
            "'', clave, Ana, ana@mail.com, Todos los campos son obligatorios.",
            "777, '', Ana, ana@mail.com, Todos los campos son obligatorios.",
            "777, clave, '  ', ana@mail.com, Todos los campos son obligatorios.",
            "777, clave, Ana, '', Todos los campos son obligatorios.",
            "777, clave, 'Ruiz, Ana', ana@mail.com, Los datos no pueden contener comas.",
            "777, 'cla,ve', Ana, ana@mail.com, Los datos no pueden contener comas.",
            "777, clave, Ana, malo, Ingrese un correo válido.",
            "777, clave, Ana, ana@mail, Ingrese un correo válido.",
            "777, clave, Ana, 'ana @mail.com', Ingrese un correo válido.",
    })
    void registroInvalido(String cedula, String password, String nombre, String correo, String mensaje) {
        error(mensaje, farmacia.registrarFuncionario(cedula, password, nombre, correo));
        assertEquals("", farmacia.listarUsuarios(), "no debe guardarse el funcionario");
    }

    @Test
    void registroEInicioDeSesion() {
        assertEquals(new Resultado(true, "Usuario registrado con éxito."),
                farmacia.registrarFuncionario(" 777 ", "clave", " Ana Ruiz ", " ana@mail.com "));
        assertEquals("Cédula: 777 | Nombre: Ana Ruiz | Correo: ana@mail.com\n", farmacia.listarUsuarios());
        error("Ya existe un funcionario registrado con esa cédula.",
                farmacia.registrarFuncionario("777", "otra", "Otro", "o@mail.com"));

        error("Ingrese cédula y contraseña.", farmacia.iniciarSesion("777", ""));
        error("Ingrese cédula y contraseña.", farmacia.iniciarSesion(" ", "clave"));
        error("Usuario o contraseña incorrectos. Intente de nuevo.", farmacia.iniciarSesion("777", "mala"));
        assertTrue(farmacia.iniciarSesion(" 777 ", "clave").exito());
    }

    @Test
    void eliminarFuncionarioSinCedula() {
        assertEquals("Cédula no válida.", farmacia.eliminarUsuarioPorCedula("  "));
    }

    // ---------------------------------------------------------------- turnos

    @Test
    void turnosExigenLaCedulaDelPaciente() {
        error("Ingrese la cédula del paciente.", farmacia.generarTurno(" "));
        assertEquals(new Resultado(true, "Turno: 1"), farmacia.generarTurno("1010"));
        assertEquals(new Resultado(true, "Turno: 2"), farmacia.generarTurno("2020"));
    }
}
