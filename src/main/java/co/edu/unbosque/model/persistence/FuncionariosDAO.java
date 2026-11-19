package co.edu.unbosque.model.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Funcionarios en data/usuarios.txt, una línea por funcionario: cedula,contraseña,nombre,correo.
 * La contraseña se guarda como hash PBKDF2 con sal aleatoria: pbkdf2$iteraciones$sal$hash.
 * Las líneas antiguas (contraseña en texto plano, con o sin nombre y correo) se aceptan y se
 * convierten a hash la primera vez que el funcionario inicia sesión.
 */
public class FuncionariosDAO {
    private final Path archivo;
    private static final String PREFIJO_HASH = "pbkdf2$";
    private static final int ITERACIONES = 65536;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    public FuncionariosDAO() {
        this(Paths.get("data"));
    }

    /** Permite usar otra carpeta de datos, por ejemplo una temporal en las pruebas. */
    public FuncionariosDAO(Path directorio) {
        this.archivo = directorio.resolve("usuarios.txt");
    }

    public void guardarUsuario(FuncionarioDTO funcionario) {
        String linea = String.join(",", funcionario.getCedula(), hashear(funcionario.getPassword()),
                funcionario.getNombre(), funcionario.getCorreo());
        try {
            Files.createDirectories(archivo.getParent());
            Files.writeString(archivo, linea + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error al guardar el funcionario: " + e.getMessage());
        }
    }

    public boolean verificarUsuario(String cedula, String password) {
        List<String> lineas = leerLineas();
        for (int i = 0; i < lineas.size(); i++) {
            String[] datos = lineas.get(i).split(",", -1);
            if (datos.length < 2 || !datos[0].equals(cedula)) {
                continue;
            }
            if (datos[1].startsWith(PREFIJO_HASH)) {
                return coincide(password, datos[1]);
            }
            // contraseña antigua en texto plano: si coincide, se reemplaza por su hash
            if (!datos[1].equals(password)) {
                return false;
            }
            datos[1] = hashear(password);
            lineas.set(i, String.join(",", datos));
            escribirLineas(lineas);
            return true;
        }
        return false;
    }

    /** Nombre del funcionario, o {@code null} si no existe o es una cuenta antigua sin nombre. */
    public String buscarNombre(String cedula) {
        for (String linea : leerLineas()) {
            String[] datos = linea.split(",", -1);
            if (datos[0].equals(cedula)) {
                return datos.length > 2 && !datos[2].isBlank() ? datos[2] : null;
            }
        }
        return null;
    }

    public boolean existeCedula(String cedula) {
        for (String linea : leerLineas()) {
            if (linea.split(",", -1)[0].equals(cedula)) {
                return true;
            }
        }
        return false;
    }

    public String eliminarPorCedula(String cedula) {
        List<String> lineas = leerLineas();
        boolean eliminado = lineas.removeIf(linea -> linea.split(",", -1)[0].equals(cedula));
        if (!eliminado) {
            return "Usuario no encontrado.";
        }
        return escribirLineas(lineas) ? "Usuario eliminado con éxito." : "Error al eliminar el usuario.";
    }

    public String mostrarUsuarios() {
        StringBuilder usuarios = new StringBuilder();
        for (String linea : leerLineas()) {
            String[] datos = linea.split(",", -1);
            if (datos.length < 2 || datos[0].isBlank()) {
                continue;
            }
            // nunca se muestra la contraseña (datos[1])
            usuarios.append("Cédula: ").append(datos[0])
                    .append(" | Nombre: ").append(datos.length > 2 ? datos[2] : "-")
                    .append(" | Correo: ").append(datos.length > 3 ? datos[3] : "-")
                    .append("\n");
        }
        return usuarios.toString();
    }

    private List<String> leerLineas() {
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        try {
            List<String> lineas = new ArrayList<>(Files.readAllLines(archivo, StandardCharsets.UTF_8));
            lineas.removeIf(String::isBlank);
            return lineas;
        } catch (IOException e) {
            System.err.println("Error al leer los funcionarios: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Escribe en un archivo temporal y luego lo reemplaza, para no dejar el original a medias. */
    private boolean escribirLineas(List<String> lineas) {
        Path temporal = archivo.resolveSibling("temp_usuarios.txt");
        try {
            Files.write(temporal, lineas, StandardCharsets.UTF_8);
            Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.err.println("Error al actualizar los funcionarios: " + e.getMessage());
            return false;
        }
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
