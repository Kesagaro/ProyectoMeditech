package co.edu.unbosque.model.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FuncionariosDAOTest {

    @TempDir
    Path datos;

    private BaseDeDatos bd;
    private FuncionariosDAO dao;

    @BeforeEach
    void preparar() {
        bd = new BaseDeDatos(datos.toFile());
        dao = new FuncionariosDAO(bd);
    }

    /** Columna guardada en la base: password, nombre o correo. */
    private String campo(String cedula, String columna) throws Exception {
        try (Connection conexion = bd.conectar();
                PreparedStatement sql = conexion.prepareStatement("SELECT " + columna + " FROM funcionarios WHERE cedula = ?")) {
            sql.setString(1, cedula);
            try (ResultSet filas = sql.executeQuery()) {
                return filas.next() ? filas.getString(1) : null;
            }
        }
    }

    /** DAO sobre una base nueva creada a partir de un usuarios.txt del formato anterior. */
    private FuncionariosDAO desdeArchivoAntiguo(String contenido) throws Exception {
        File carpeta = datos.resolve("antigua").toFile();
        carpeta.mkdirs();
        Files.writeString(carpeta.toPath().resolve("usuarios.txt"), contenido);
        bd = new BaseDeDatos(carpeta);
        return new FuncionariosDAO(bd);
    }

    @Test
    void guardaLaContrasenaComoHashYNoEnTextoPlano() throws Exception {
        dao.guardarUsuario(new FuncionarioDTO("777", "secreta", "Ana", "ana@mail.com"));
        assertTrue(campo("777", "password").startsWith("pbkdf2$65536$"));
        String archivo = Files.readString(datos.resolve(BaseDeDatos.ARCHIVO), StandardCharsets.ISO_8859_1);
        assertFalse(archivo.contains("secreta"));
    }

    @Test
    void laMismaContrasenaProduceHashesDistintosPorLaSal() throws Exception {
        dao.guardarUsuario(new FuncionarioDTO("1", "clave", "A", "a@x.co"));
        dao.guardarUsuario(new FuncionarioDTO("2", "clave", "B", "b@x.co"));
        assertNotEquals(campo("1", "password"), campo("2", "password"));
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
        FuncionariosDAO dao = desdeArchivoAntiguo("112233,112233\n900,clave9,Viejo,v@x.co\n");

        assertFalse(dao.verificarUsuario("900", "otra"));
        assertEquals("clave9", campo("900", "password"), "una clave incorrecta no debe migrar");

        assertTrue(dao.verificarUsuario("900", "clave9"));
        assertTrue(campo("900", "password").startsWith("pbkdf2$"));
        assertEquals("Viejo", campo("900", "nombre"));
        assertEquals("v@x.co", campo("900", "correo"));
        assertTrue(dao.verificarUsuario("900", "clave9"), "debe seguir entrando después de migrar");

        assertTrue(dao.verificarUsuario("112233", "112233"));
        assertTrue(campo("112233", "password").startsWith("pbkdf2$"));
    }

    @Test
    void elListadoNoTraeContrasenas() {
        dao.guardarUsuario(new FuncionarioDTO("777", "secreta", "Ana Ruiz", "ana@mail.com"));
        List<FuncionarioDTO> funcionarios = dao.listar();
        assertEquals(1, funcionarios.size());
        assertEquals("FuncionarioDTO[cedula=777, nombre=Ana Ruiz, correo=ana@mail.com]", funcionarios.get(0).toString());
        assertNull(funcionarios.get(0).getPassword());
    }

    @Test
    void lasCuentasAntiguasSinNombreSeListanSinNombreNiCorreo() throws Exception {
        FuncionarioDTO antiguo = desdeArchivoAntiguo("112233,112233\n").listar().get(0);
        assertEquals("112233", antiguo.getCedula());
        assertNull(antiguo.getNombre());
        assertNull(antiguo.getCorreo());
    }

    @Test
    void eliminarPorCedula() {
        dao.guardarUsuario(new FuncionarioDTO("1", "a", "A", "a@x.co"));
        dao.guardarUsuario(new FuncionarioDTO("2", "b", "B", "b@x.co"));
        assertEquals("Usuario eliminado con éxito.", dao.eliminarPorCedula("1"));
        assertEquals("Usuario no encontrado.", dao.eliminarPorCedula("1"));
        assertFalse(dao.existeCedula("1"));
        assertTrue(dao.verificarUsuario("2", "b"));
    }

    @Test
    void buscaElNombreDelFuncionario() throws Exception {
        FuncionariosDAO dao = desdeArchivoAntiguo("112233,112233\n");
        dao.guardarUsuario(new FuncionarioDTO("777", "secreta", "Ana Ruiz", "ana@mail.com"));
        assertEquals("Ana Ruiz", dao.buscarNombre("777"));
        assertNull(dao.buscarNombre("112233"), "cuenta antigua sin nombre");
        assertNull(dao.buscarNombre("999"));
    }

    @Test
    void elToStringNoIncluyeLaContrasena() {
        String texto = new FuncionarioDTO("777", "secreta", "Ana Ruiz", "ana@mail.com").toString();
        assertFalse(texto.contains("secreta"));
        assertTrue(texto.contains("777") && texto.contains("Ana Ruiz"));
    }

    @Test
    void sinFuncionariosRegistrados() {
        assertFalse(dao.existeCedula("1"));
        assertFalse(dao.verificarUsuario("1", "x"));
        assertEquals(List.of(), dao.listar());
    }
}
