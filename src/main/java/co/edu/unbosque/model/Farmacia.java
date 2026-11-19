package co.edu.unbosque.model;

import co.edu.unbosque.model.persistence.MedicamentoDAO;
import co.edu.unbosque.model.persistence.MedicamentoDTO;
import co.edu.unbosque.model.persistence.FuncionarioDTO;
import co.edu.unbosque.model.persistence.FuncionariosDAO;
import co.edu.unbosque.model.persistence.OrdenDAO;
import co.edu.unbosque.model.persistence.OrdenDTO;

import java.io.File;
import java.text.NumberFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fachada del modelo. Los métodos que reciben texto (tal como llega de la pantalla) validan los
 * datos y devuelven un {@link Resultado} con el mensaje para el usuario.
 */
public class Farmacia {
    private static final String CORREO_VALIDO = "[^@\\s]+@[^@\\s]+\\.[^@\\s]+";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private MedicamentoDAO medicamentoDAO;
    private FuncionariosDAO usuarioDAO;
    private OrdenDAO ordenDAO;
    private Turnos turnos;
    private Clock reloj;

    public Farmacia() {
        this(new File("data"));
    }

    /** Permite usar otra carpeta de datos, por ejemplo una temporal en las pruebas. */
    public Farmacia(File directorioDatos) {
        this(directorioDatos, Clock.systemDefaultZone());
    }

    /** Permite además fijar la fecha y hora, para probar órdenes y turnos. */
    public Farmacia(File directorioDatos, Clock reloj) {
        this.reloj = reloj;
        this.medicamentoDAO = new MedicamentoDAO(directorioDatos);
        this.usuarioDAO = new FuncionariosDAO(directorioDatos.toPath());
        this.ordenDAO = new OrdenDAO(directorioDatos);
        this.turnos = new Turnos(new File(directorioDatos, "turnos.txt"), reloj);
    }

    // ---------------------------------------------------------------- funcionarios

    public Resultado iniciarSesion(String cedula, String password) {
        if (vacio(cedula) || password == null || password.isEmpty()) {
            return Resultado.error("Ingrese cédula y contraseña.");
        }
        return usuarioDAO.verificarUsuario(cedula.trim(), password)
                ? Resultado.ok("Bienvenido.")
                : Resultado.error("Usuario o contraseña incorrectos. Intente de nuevo.");
    }

    public Resultado registrarFuncionario(String cedula, String password, String nombre, String correo) {
        if (vacio(cedula) || password == null || password.isEmpty() || vacio(nombre) || vacio(correo)) {
            return Resultado.error("Todos los campos son obligatorios.");
        }
        cedula = cedula.trim();
        nombre = nombre.trim();
        correo = correo.trim();
        if (tieneComas(cedula, password, nombre, correo)) {
            return Resultado.error("Los datos no pueden contener comas.");
        }
        if (!correo.matches(CORREO_VALIDO)) {
            return Resultado.error("Ingrese un correo válido.");
        }
        if (usuarioDAO.existeCedula(cedula)) {
            return Resultado.error("Ya existe un funcionario registrado con esa cédula.");
        }
        usuarioDAO.guardarUsuario(new FuncionarioDTO(cedula, password, nombre, correo));
        return Resultado.ok("Usuario registrado con éxito.");
    }

    // ---------------------------------------------------------------- inventario

    /** Agrega (o actualiza, si {@code actualizar}) un medicamento a partir de los textos del formulario. */
    public Resultado guardarMedicamento(String id, String nombre, String empresa, String precio, String cantidad,
            boolean actualizar) {
        if (vacio(id) || vacio(nombre) || vacio(empresa) || vacio(precio) || vacio(cantidad)) {
            return Resultado.error("Todos los campos son obligatorios.");
        }
        long numId;
        float valor;
        int unidades;
        try {
            numId = Long.parseLong(id.trim());
            valor = Float.parseFloat(precio.trim());
            unidades = Integer.parseInt(cantidad.trim());
        } catch (NumberFormatException e) {
            return Resultado.error("ID, precio y cantidad deben ser números válidos.");
        }
        if (valor < 0 || unidades < 0) {
            return Resultado.error("El precio y la cantidad no pueden ser negativos.");
        }
        boolean existe = medicamentoDAO.existeId(numId);
        if (actualizar && !existe) {
            return Resultado.error("Medicamento no encontrado.");
        }
        if (!actualizar && existe) {
            return Resultado.error("Ya existe un medicamento con el ID " + numId + ".");
        }
        return Resultado.ok(actualizar
                ? actualizarMedicamento(numId, nombre.trim(), empresa.trim(), valor, unidades)
                : agregarMedicamento(numId, nombre.trim(), empresa.trim(), valor, unidades));
    }

    /** Revisa el ID escrito antes de pedir confirmación para eliminar. */
    public Resultado validarIdMedicamento(String id) {
        if (vacio(id)) {
            return Resultado.error("Ingrese el ID del medicamento a eliminar.");
        }
        try {
            Long.parseLong(id.trim());
            return Resultado.ok("");
        } catch (NumberFormatException e) {
            return Resultado.error("El ID debe ser un número válido.");
        }
    }

    public Resultado eliminarMedicamento(String id) {
        Resultado validacion = validarIdMedicamento(id);
        if (!validacion.exito()) {
            return validacion;
        }
        long numId = Long.parseLong(id.trim());
        if (!medicamentoDAO.existeId(numId)) {
            return Resultado.error("Medicamento no encontrado.");
        }
        return Resultado.ok(eliminarMedicamento(numId));
    }

    public String agregarMedicamento(long id, String nombre, String empresa, float precio, int cantidad) {
        if (medicamentoDAO.existeId(id)) {
            return "Ya existe un medicamento con el ID " + id + ".";
        }
        MedicamentoDTO medicamento = new MedicamentoDTO(id, nombre, empresa, precio, cantidad);
        medicamentoDAO.crear(medicamento);
        return "Medicamento agregado con éxito.";
    }

    public String listarMedicamentos() {
        String listado = medicamentoDAO.mostrar();
        return listado.isEmpty() ? "No hay medicamentos registrados." : listado;
    }

    public Resultado buscarMedicamentos(String texto) {
        if (vacio(texto)) {
            return Resultado.error("Ingrese el nombre del medicamento a buscar.");
        }
        texto = texto.trim();
        StringBuilder resultado = new StringBuilder();
        for (MedicamentoDTO m : medicamentoDAO.buscarPorNombre(texto)) {
            resultado.append("ID ").append(m.getNumId())
                    .append(" | ").append(m.getNombre())
                    .append(" | ").append(m.getEmpresa())
                    .append(" | Precio: ").append(m.getPrecio())
                    .append(" | Stock: ").append(m.getCantidad()).append("\n");
        }
        return Resultado.ok(resultado.length() == 0
                ? "No se encontraron medicamentos con \"" + texto + "\"."
                : resultado.toString());
    }

    public String eliminarMedicamento(long id) {
        return medicamentoDAO.eliminarPorId(id);
    }

    public String actualizarMedicamento(long id, String nombre, String empresa, float precio, int cantidad) {
        MedicamentoDTO medicamentoActualizado = new MedicamentoDTO(id, nombre, empresa, precio, cantidad);
        return medicamentoDAO.actualizarPorId(id, medicamentoActualizado);
    }
    
    public String listarUsuarios() {
        return usuarioDAO.mostrarUsuarios();
    }
    
    public String eliminarUsuarioPorCedula(String cedula) {
        if (vacio(cedula)) {
            return "Cédula no válida.";
        }
        return usuarioDAO.eliminarPorCedula(cedula.trim());
    }

    // ---------------------------------------------------------------- turnos

    public Resultado generarTurno(String cedulaPaciente) {
        if (vacio(cedulaPaciente)) {
            return Resultado.error("Ingrese la cédula del paciente.");
        }
        return Resultado.ok("Turno: " + turnos.generarTurno());
    }

    // ---------------------------------------------------------------- órdenes

    /** Registra una orden a partir de los textos del formulario. */
    public Resultado agregarOrden(String documento, String codigoEntidad, String nombreMedicamento, String cantidad) {
        if (vacio(documento) || vacio(codigoEntidad) || vacio(nombreMedicamento) || vacio(cantidad)) {
            return Resultado.error("Todos los campos son obligatorios.");
        }
        documento = documento.trim();
        codigoEntidad = codigoEntidad.trim();
        nombreMedicamento = nombreMedicamento.trim();
        if (tieneComas(documento, codigoEntidad, nombreMedicamento)) {
            return Resultado.error("Los datos no pueden contener comas.");
        }
        int unidades;
        try {
            unidades = Integer.parseInt(cantidad.trim());
        } catch (NumberFormatException e) {
            return Resultado.error("La cantidad debe ser un número entero.");
        }
        if (unidades <= 0) {
            return Resultado.error("La cantidad debe ser mayor que cero.");
        }
        return registrarOrden(documento, codigoEntidad, nombreMedicamento, unidades);
    }

    public String agregarOrden(String documento, String codigoEntidad, String nombreMedicamento, int cantidad) {
        return registrarOrden(documento, codigoEntidad, nombreMedicamento, cantidad).mensaje();
    }

    private Resultado registrarOrden(String documento, String codigoEntidad, String nombreMedicamento, int cantidad) {
        MedicamentoDTO medicamento = medicamentoDAO.buscarPorNombreExacto(nombreMedicamento);
        if (medicamento == null) {
            return Resultado.error("No existe el medicamento \"" + nombreMedicamento + "\" en el inventario.");
        }
        if (medicamento.getCantidad() < cantidad) {
            return Resultado.error("Stock insuficiente: quedan " + medicamento.getCantidad() + " unidades de "
                    + medicamento.getNombre() + ".");
        }
        medicamentoDAO.descontarStock(medicamento, cantidad);
        double total = (double) medicamento.getPrecio() * cantidad;
        LocalDateTime fecha = LocalDateTime.now(reloj).truncatedTo(ChronoUnit.MINUTES);
        ordenDAO.agregarOrden(new OrdenDTO(documento, codigoEntidad, medicamento.getNombre(), cantidad, fecha, total));
        return Resultado.ok("Orden agregada por " + pesos(total) + ". Stock restante de " + medicamento.getNombre() + ": "
                + medicamento.getCantidad() + ".");
    }
    
    public String top5Medicamentos() {
        Map<String, Integer> totales = new LinkedHashMap<>();
        for (OrdenDTO orden : ordenDAO.listarOrdenes()) {
            totales.merge(orden.getNombreMedicamento(), orden.getCantidadMedicamento(), Integer::sum);
        }
        if (totales.isEmpty()) {
            return "Aún no hay órdenes registradas.";
        }
        List<Map.Entry<String, Integer>> ranking = new ArrayList<>(totales.entrySet());
        ranking.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        StringBuilder reporte = new StringBuilder("Top 5 medicamentos más expendidos:\n\n");
        for (int i = 0; i < Math.min(5, ranking.size()); i++) {
            reporte.append(i + 1).append(". ").append(ranking.get(i).getKey())
                    .append(" - Cantidad expendida: ").append(ranking.get(i).getValue()).append("\n");
        }
        return reporte.toString();
    }

    public String listarOrdenes() {
        StringBuilder listado = new StringBuilder();
        for (OrdenDTO orden : ordenDAO.listarOrdenes()) {
            // las órdenes antiguas no tienen fecha ni total
            listado.append(orden.getFecha() == null ? "Sin fecha" : orden.getFecha().format(FORMATO_FECHA))
                   .append(" | Documento: ").append(orden.getDocumentoPaciente())
                   .append(" | Entidad: ").append(orden.getCodigoEntidad())
                   .append(" | ").append(orden.getNombreMedicamento())
                   .append(" x ").append(orden.getCantidadMedicamento())
                   .append(" | Total: ").append(orden.getTotal() == null ? "-" : pesos(orden.getTotal()))
                   .append("\n");
        }
        return listado.length() == 0 ? "No hay órdenes registradas." : listado.toString();
    }

    /** Valor en pesos colombianos sin decimales, por ejemplo $7.500. */
    static String pesos(double valor) {
        return "$" + NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CO")).format(Math.round(valor));
    }

    // ---------------------------------------------------------------- utilidades

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    /** Los archivos de datos separan los campos con comas. */
    private static boolean tieneComas(String... textos) {
        for (String texto : textos) {
            if (texto.contains(",")) {
                return true;
            }
        }
        return false;
    }
}
