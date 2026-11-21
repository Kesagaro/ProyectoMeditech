package co.edu.unbosque.model;

import co.edu.unbosque.model.persistence.BaseDeDatos;
import co.edu.unbosque.model.persistence.MedicamentoDAO;
import co.edu.unbosque.model.persistence.MedicamentoDTO;
import co.edu.unbosque.model.persistence.FuncionarioDTO;
import co.edu.unbosque.model.persistence.FuncionariosDAO;
import co.edu.unbosque.model.persistence.OrdenDAO;
import co.edu.unbosque.model.persistence.OrdenDTO;
import co.edu.unbosque.model.persistence.TurnoDAO;

import java.io.File;
import java.text.NumberFormat;
import java.time.Clock;
import java.time.LocalDateTime;
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
        BaseDeDatos bd = new BaseDeDatos(directorioDatos);
        this.medicamentoDAO = new MedicamentoDAO(bd);
        this.usuarioDAO = new FuncionariosDAO(bd);
        this.ordenDAO = new OrdenDAO(bd);
        this.turnos = new Turnos(new TurnoDAO(bd), reloj);
    }

    // ---------------------------------------------------------------- funcionarios

    /**
     * Si las credenciales son correctas, el mensaje del resultado es el nombre del funcionario
     * (o su cédula, en cuentas antiguas sin nombre), para mostrarlo durante la sesión.
     */
    public Resultado iniciarSesion(String cedula, String password) {
        if (vacio(cedula) || password == null || password.isEmpty()) {
            return Resultado.error("Ingrese cédula y contraseña.");
        }
        cedula = cedula.trim();
        if (!usuarioDAO.verificarUsuario(cedula, password)) {
            return Resultado.error("Usuario o contraseña incorrectos. Intente de nuevo.");
        }
        String nombre = usuarioDAO.buscarNombre(cedula);
        return Resultado.ok(nombre != null ? nombre : "CC " + cedula);
    }

    public Resultado registrarFuncionario(String cedula, String password, String nombre, String correo) {
        if (vacio(cedula) || password == null || password.isEmpty() || vacio(nombre) || vacio(correo)) {
            return Resultado.error("Todos los campos son obligatorios.");
        }
        cedula = cedula.trim();
        nombre = nombre.trim();
        correo = correo.trim();
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

    /** Inventario completo, una fila por medicamento: ID, nombre, empresa, precio y stock. */
    public List<Object[]> filasMedicamentos() {
        return filas(medicamentoDAO.listar());
    }

    /**
     * Medicamentos cuyo nombre contiene el texto buscado, con las mismas columnas que
     * {@link #filasMedicamentos()}. Si el texto está vacío o nada coincide, el resultado es un
     * error con el mensaje para el usuario.
     */
    public Resultado buscarMedicamentos(String texto) {
        if (vacio(texto)) {
            return Resultado.error("Ingrese el nombre del medicamento a buscar.");
        }
        int encontrados = medicamentoDAO.buscarPorNombre(texto.trim()).size();
        return encontrados == 0
                ? Resultado.error("No se encontraron medicamentos con \"" + texto.trim() + "\".")
                : Resultado.ok(encontrados + (encontrados == 1 ? " medicamento encontrado." : " medicamentos encontrados."));
    }

    public List<Object[]> filasBusqueda(String texto) {
        return vacio(texto) ? List.of() : filas(medicamentoDAO.buscarPorNombre(texto.trim()));
    }

    private static List<Object[]> filas(List<MedicamentoDTO> medicamentos) {
        List<Object[]> filas = new ArrayList<>();
        for (MedicamentoDTO m : medicamentos) {
            filas.add(new Object[] {m.getNumId(), m.getNombre(), m.getEmpresa(), (double) m.getPrecio(), m.getCantidad()});
        }
        return filas;
    }

    public String eliminarMedicamento(long id) {
        return medicamentoDAO.eliminarPorId(id);
    }

    public String actualizarMedicamento(long id, String nombre, String empresa, float precio, int cantidad) {
        MedicamentoDTO medicamentoActualizado = new MedicamentoDTO(id, nombre, empresa, precio, cantidad);
        return medicamentoDAO.actualizarPorId(id, medicamentoActualizado);
    }
    
    /** Funcionarios registrados: cédula, nombre y correo (nunca la contraseña). */
    public List<Object[]> filasFuncionarios() {
        List<Object[]> filas = new ArrayList<>();
        for (FuncionarioDTO f : usuarioDAO.listar()) {
            filas.add(new Object[] {f.getCedula(), f.getNombre(), f.getCorreo()});
        }
        return filas;
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
        double total = (double) medicamento.getPrecio() * cantidad;
        LocalDateTime fecha = LocalDateTime.now(reloj).truncatedTo(ChronoUnit.MINUTES);
        OrdenDTO orden = new OrdenDTO(documento, codigoEntidad, medicamento.getNombre(), cantidad, fecha, total);
        if (!ordenDAO.registrarVenta(orden, medicamento.getNumId())) {
            return Resultado.error("Stock insuficiente para " + medicamento.getNombre() + ".");
        }
        return Resultado.ok("Orden agregada por " + pesos(total) + ". Stock restante de " + medicamento.getNombre() + ": "
                + (medicamento.getCantidad() - cantidad) + ".");
    }
    
    /**
     * Los 5 medicamentos con más unidades vendidas, de mayor a menor: puesto, nombre y unidades.
     * Con empate, queda primero el que se vendió antes.
     */
    public List<Object[]> filasTop5() {
        Map<String, Integer> totales = new LinkedHashMap<>();
        for (OrdenDTO orden : ordenDAO.listarOrdenes()) {
            totales.merge(orden.getNombreMedicamento(), orden.getCantidadMedicamento(), Integer::sum);
        }
        List<Map.Entry<String, Integer>> ranking = new ArrayList<>(totales.entrySet());
        ranking.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        List<Object[]> filas = new ArrayList<>();
        for (int i = 0; i < Math.min(5, ranking.size()); i++) {
            filas.add(new Object[] {i + 1, ranking.get(i).getKey(), ranking.get(i).getValue()});
        }
        return filas;
    }

    /**
     * Órdenes en el orden en que se registraron: fecha, documento, entidad, medicamento, cantidad
     * y total. Las órdenes de versiones antiguas no tienen fecha ni total ({@code null}).
     */
    public List<Object[]> filasOrdenes() {
        List<Object[]> filas = new ArrayList<>();
        for (OrdenDTO o : ordenDAO.listarOrdenes()) {
            filas.add(new Object[] {o.getFecha(), o.getDocumentoPaciente(), o.getCodigoEntidad(),
                    o.getNombreMedicamento(), o.getCantidadMedicamento(), o.getTotal()});
        }
        return filas;
    }

    /** Valor en pesos colombianos sin decimales, por ejemplo $7.500. */
    public static String pesos(double valor) {
        return "$" + NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CO")).format(Math.round(valor));
    }

    // ---------------------------------------------------------------- utilidades

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
