package co.edu.unbosque.model.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO implements Crud<MedicamentoDTO> {
    private List<MedicamentoDTO> medicamentos;
    private final File directorio;
    private final File archivo;

    public MedicamentoDAO() {
        this(new File("data"));
    }

    /** Permite usar otra carpeta de datos, por ejemplo una temporal en las pruebas. */
    public MedicamentoDAO(File directorio) {
        this.directorio = directorio;
        this.archivo = new File(directorio, "Medicamentos.txt");
        medicamentos = cargarMedicamentos();
    }

    @Override
    public void crear(MedicamentoDTO data) {
        medicamentos.add(data);
        guardarMedicamentos();
    }

    @Override
    public String mostrar() {
        StringBuilder sb = new StringBuilder();
        for (MedicamentoDTO medicamento : medicamentos) {
            sb.append("Número ID: ").append(medicamento.getNumId()).append("\n");
            sb.append("Nombre: ").append(medicamento.getNombre()).append("\n");
            sb.append("Empresa: ").append(medicamento.getEmpresa()).append("\n");
            sb.append("Precio: ").append(medicamento.getPrecio()).append("\n");
            sb.append("Cantidad: ").append(medicamento.getCantidad()).append("\n\n");
        }
        return sb.toString();
    }

    @Override
    public String actualizar(int index, MedicamentoDTO newData) {
        if (index >= 0 && index < medicamentos.size()) {
            medicamentos.set(index, newData);
            guardarMedicamentos();
            return "Medicamento actualizado correctamente.";
        } else {
            return "Índice inválido.";
        }
    }

    public boolean existeId(long id) {
        return buscarIndicePorId(id) >= 0;
    }

    public String actualizarPorId(long id, MedicamentoDTO newData) {
        int index = buscarIndicePorId(id);
        if (index < 0) {
            return "Medicamento no encontrado.";
        }
        medicamentos.set(index, newData);
        guardarMedicamentos();
        return "Medicamento actualizado correctamente.";
    }

    public List<MedicamentoDTO> buscarPorNombre(String texto) {
        String buscado = texto.toLowerCase();
        List<MedicamentoDTO> encontrados = new ArrayList<>();
        for (MedicamentoDTO medicamento : medicamentos) {
            if (medicamento.getNombre().toLowerCase().contains(buscado)) {
                encontrados.add(medicamento);
            }
        }
        return encontrados;
    }

    public MedicamentoDTO buscarPorNombreExacto(String nombre) {
        for (MedicamentoDTO medicamento : medicamentos) {
            if (medicamento.getNombre().equalsIgnoreCase(nombre)) {
                return medicamento;
            }
        }
        return null;
    }

    public void descontarStock(MedicamentoDTO medicamento, int cantidad) {
        medicamento.setCantidad(medicamento.getCantidad() - cantidad);
        guardarMedicamentos();
    }

    public String eliminarPorId(long id) {
        int index = buscarIndicePorId(id);
        if (index < 0) {
            return "Medicamento no encontrado.";
        }
        medicamentos.remove(index);
        guardarMedicamentos();
        return "Medicamento eliminado correctamente.";
    }

    private int buscarIndicePorId(long id) {
        for (int i = 0; i < medicamentos.size(); i++) {
            if (medicamentos.get(i).getNumId() == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String eliminarPorNombre(String name) {
        medicamentos.removeIf(medicamento -> medicamento.getNombre().equals(name));
        guardarMedicamentos();
        return "Medicamento eliminado correctamente.";
    }

    @Override
    public String checkearIndex(int index) {
        return (index >= 0 && index < medicamentos.size()) ? "Índice válido." : "Índice inválido.";
    }

    private void guardarMedicamentos() {
        directorio.mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(medicamentos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private List<MedicamentoDTO> cargarMedicamentos() {
        File file = archivo;
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<MedicamentoDTO>) ois.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            // se aparta el archivo dañado para que el próximo guardado no borre el inventario
            File respaldo = new File(directorio, "Medicamentos.danado-" + System.currentTimeMillis() + ".txt");
            boolean apartado = file.renameTo(respaldo);
            System.err.println("No se pudo leer el inventario (" + e.getMessage() + "). "
                    + (apartado ? "Se guardó una copia en " + respaldo.getPath() : "No se pudo respaldar el archivo."));
            return new ArrayList<>();
        }
    }
}

