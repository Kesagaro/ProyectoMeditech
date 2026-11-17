package co.edu.unbosque.model.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FuncionariosDAOTest {

    @TempDir
    Path datos;

    private FuncionariosDAO dao;

    @BeforeEach
    void preparar() {
        dao = new FuncionariosDAO(datos);
    }

    private String campo(String cedula, int indice) throws Exception {
        for (String linea : Files.readAllLines(datos.resolve("usuarios.txt"))) {
            String[] partes = linea.split(",", -1);
            if (partes[0].equals(cedula)) {
                return partes[indice];
            }
        }
        return null;
    }

    @Test
    void guardaLaContrasenaComoHashYNoEnTextoPlano() throws Exception {
        dao.guardarUsuario(new FuncionarioDTO("777", "secreta", "Ana", "ana@mail.com"));
        assertTrue(campo("777", 1).startsWith("pbkdf2$65536$"));
        assertFalse(Files.readString(datos.resolve("usuarios.txt")).contains("secreta"));
    }

    @Test
    void laMismaContrasenaProduceHashesDistintosPorLaSal() throws Exception {
        dao.guardarUsuario(new FuncionarioDTO("1", "clave", "A", "a@x.co"));
        dao.guardarUsuario(new FuncionarioDTO("2", "clave", "B", "b@x.co"));
        assertNotEquals(campo("1", 1), campo("2", 1));
    }

    @Test
    void verificaContrasenaCorrectaEIncorrecta() {
        dao.guardarUsuario(new FuncionarioDTO("777", "secreta", "Ana", "ana@mail.com"));
        assertTrue(dao.verificarUsuario("777", "secreta"));
        assertFalse(dao.verificarUsuario("777", "Secreta"));
        assertFalse(dao.verificarUsuario("000", "secreta"));
    }

    @Test
    void migraContrasenasAntiguasAlIniciarSesion() throws Exception {
        Files.writeString(datos.resolve("usuarios.txt"), "112233,112233\n900,clave9,Viejo,v@x.co\n");

        assertFalse(dao.verificarUsuario("900", "otra"));
        assertEquals("clave9", campo("900", 1), "una clave incorrecta no debe migrar");

        assertTrue(dao.verificarUsuario("900", "clave9"));
        assertTrue(campo("900", 1).startsWith("pbkdf2$"));
        assertEquals("Viejo", campo("900", 2));
        assertEquals("v@x.co", campo("900", 3));
        assertTrue(dao.verificarUsuario("900", "clave9"), "debe seguir entrando después de migrar");

        assertTrue(dao.verificarUsuario("112233", "112233"));
        assertTrue(campo("112233", 1).startsWith("pbkdf2$"));
    }

    @Test
    void elListadoNoMuestraContrasenas() {
        dao.guardarUsuario(new FuncionarioDTO("777", "secreta", "Ana Ruiz", "ana@mail.com"));
        assertEquals("Cédula: 777 | Nombre: Ana Ruiz | Correo: ana@mail.com\n", dao.mostrarUsuarios());
    }

    @Test
    void eliminarPorCedula() {
        dao.guardarUsuario(new FuncionarioDTO("1", "a", "A", "a@x.co"));
        dao.guardarUsuario(new FuncionarioDTO("2", "b", "B", "b@x.co"));
        assertEquals("Usuario eliminado con éxito.", dao.eliminarPorCedula("1"));
        assertEquals("Usuario no encontrado.", dao.eliminarPorCedula("1"));
        assertFalse(dao.existeCedula("1"));
        assertTrue(dao.verificarUsuario("2", "b"));
        assertFalse(Files.exists(datos.resolve("temp_usuarios.txt")));
    }

    @Test
    void sinArchivoNoHayUsuarios() {
        assertFalse(dao.existeCedula("1"));
        assertFalse(dao.verificarUsuario("1", "x"));
        assertEquals("", dao.mostrarUsuarios());
    }
}
