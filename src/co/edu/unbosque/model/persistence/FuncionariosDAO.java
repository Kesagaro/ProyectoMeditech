package co.edu.unbosque.model.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FuncionariosDAO {
    private static final String DIRECTORY = "data";
    private static final String FILE_PATH = DIRECTORY + "/usuarios.txt";

    public void guardarUsuario(FuncionarioDTO funcionario) {
        File directory = new File(DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(funcionario.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean verificarUsuario(String cedula, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                // líneas antiguas: cedula,password; actuales: cedula,password,nombre,correo
                if (datos.length >= 2 && datos[0].equals(cedula) && datos[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean existeCedula(String cedula) {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.split(",")[0].equals(cedula)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String eliminarPorCedula(String cedula) {
        File inputFile = new File(FILE_PATH);
        File tempFile = new File(DIRECTORY + "/temp_usuarios.txt");
        boolean eliminado = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos[0].equals(cedula)) {
                    eliminado = true;
                    continue; // Omitir la línea para eliminar el usuario
                }
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (eliminado && inputFile.delete()) {
            tempFile.renameTo(inputFile);
            return "Usuario eliminado con éxito.";
        } else {
            tempFile.delete();
            return "Usuario no encontrado o error al eliminar.";
        }
    }
    
    public String mostrarUsuarios() {
        StringBuilder usuarios = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                usuarios.append(linea).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usuarios.toString();
    }
}