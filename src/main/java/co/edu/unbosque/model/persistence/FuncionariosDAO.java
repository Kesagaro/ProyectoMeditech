package co.edu.unbosque.model.persistence;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Funcionarios en la tabla funcionarios (cedula, password, nombre, correo).
 * La contraseña se guarda como hash PBKDF2 con sal aleatoria: pbkdf2$iteraciones$sal$hash.
 * Las cuentas importadas del formato anterior pueden tener la contraseña en texto plano;
 * se convierte a hash la primera vez que el funcionario inicia sesión.
 */
public class FuncionariosDAO {
    private final BaseDeDatos bd;
    private static final String PREFIJO_HASH = "pbkdf2$";
    private static final int ITERACIONES = 65536;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    public FuncionariosDAO(BaseDeDatos bd) {
        this.bd = bd;
    }

    public void guardarUsuario(FuncionarioDTO funcionario) {
        try (Connection conexion = bd.conectar();
                PreparedStatement sql = conexion.prepareStatement(
                        "INSERT INTO funcionarios (cedula, password, nombre, correo) VALUES (?, ?, ?, ?)")) {
            sql.setString(1, funcionario.getCedula());
            sql.setString(2, hashear(funcionario.getPassword()));
            sql.setString(3, funcionario.getNombre());
            sql.setString(4, funcionario.getCorreo());
            sql.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudo guardar el funcionario", e);
        }
    }

    public boolean verificarUsuario(String cedula, String password) {
        String guardada = consultar("SELECT password FROM funcionarios WHERE cedula = ?", cedula);
        if (guardada == null) {
            return false;
        }
        if (guardada.startsWith(PREFIJO_HASH)) {
            return coincide(password, guardada);
        }
        // contraseña antigua en texto plano: si coincide, se reemplaza por su hash
        if (!guardada.equals(password)) {
            return false;
        }
        try (Connection conexion = bd.conectar();
                PreparedStatement sql = conexion.prepareStatement("UPDATE funcionarios SET password = ? WHERE cedula = ?")) {
            sql.setString(1, hashear(password));
            sql.setString(2, cedula);
            sql.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudo actualizar la contraseña", e);
        }
        return true;
    }

    /** Nombre del funcionario, o {@code null} si no existe o es una cuenta antigua sin nombre. */
    public String buscarNombre(String cedula) {
        return vacioANulo(consultar("SELECT nombre FROM funcionarios WHERE cedula = ?", cedula));
    }

    public boolean existeCedula(String cedula) {
        return consultar("SELECT cedula FROM funcionarios WHERE cedula = ?", cedula) != null;
    }

    public String eliminarPorCedula(String cedula) {
        try (Connection conexion = bd.conectar();
                PreparedStatement sql = conexion.prepareStatement("DELETE FROM funcionarios WHERE cedula = ?")) {
            sql.setString(1, cedula);
            return sql.executeUpdate() > 0 ? "Usuario eliminado con éxito." : "Usuario no encontrado.";
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudo eliminar el funcionario", e);
        }
    }

    /**
     * Funcionarios en el orden en que se registraron. La contraseña nunca se consulta: los
     * objetos devueltos la tienen en {@code null}. Las cuentas antiguas sin nombre ni correo
     * los traen en {@code null}.
     */
    public List<FuncionarioDTO> listar() {
        List<FuncionarioDTO> funcionarios = new ArrayList<>();
        try (Connection conexion = bd.conectar();
                PreparedStatement sql = conexion.prepareStatement(
                        "SELECT cedula, nombre, correo FROM funcionarios ORDER BY rowid");
                ResultSet filas = sql.executeQuery()) {
            while (filas.next()) {
                funcionarios.add(new FuncionarioDTO(filas.getString("cedula"), null,
                        vacioANulo(filas.getString("nombre")), vacioANulo(filas.getString("correo"))));
            }
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudieron leer los funcionarios", e);
        }
        return funcionarios;
    }

    /** Primera columna de la primera fila, o {@code null} si no hay filas. */
    private String consultar(String consulta, String cedula) {
        try (Connection conexion = bd.conectar(); PreparedStatement sql = conexion.prepareStatement(consulta)) {
            sql.setString(1, cedula);
            try (ResultSet filas = sql.executeQuery()) {
                return filas.next() ? filas.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new ErrorDeDatos("No se pudieron leer los funcionarios", e);
        }
    }

    private static String vacioANulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto;
    }

    private static String hashear(String password) {
        byte[] sal = new byte[BYTES_SAL];
        ALEATORIO.nextBytes(sal);
        Base64.Encoder b64 = Base64.getEncoder();
        return PREFIJO_HASH + ITERACIONES + "$" + b64.encodeToString(sal) + "$"
                + b64.encodeToString(pbkdf2(password, sal, ITERACIONES));
    }

    private static boolean coincide(String password, String guardado) {
        String[] partes = guardado.split("\\$");
        if (partes.length != 4) {
            return false;
        }
        try {
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            // comparación en tiempo constante
            return MessageDigest.isEqual(esperado, pbkdf2(password, sal, iteraciones));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(String password, byte[] sal, int iteraciones) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), sal, iteraciones, BITS_HASH);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("PBKDF2 no disponible", e);
        }
    }
}
