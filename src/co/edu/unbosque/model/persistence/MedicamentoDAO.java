package co.edu.unbosque.model.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO implements Crud<MedicamentoDTO> {
    private List<MedicamentoDTO> medicamentos;
    private static final String DIRECTORY = "data";
    private static final String FILE_PATH = DIRECTORY + "/Medicamentos.txt";
    
    public MedicamentoDAO() {
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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(medicamentos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private List<MedicamentoDTO> cargarMedicamentos() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<MedicamentoDTO>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

