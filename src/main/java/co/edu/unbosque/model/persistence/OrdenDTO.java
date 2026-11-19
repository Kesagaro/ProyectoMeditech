package co.edu.unbosque.model.persistence;

import java.time.LocalDateTime;

public class OrdenDTO {
    private String documentoPaciente;
    private String codigoEntidad;
    private String nombreMedicamento;
    private int cantidadMedicamento;
    /** Fecha y hora de registro; {@code null} en órdenes guardadas antes de que existiera el campo. */
    private LocalDateTime fecha;
    /** Precio unitario por cantidad, en pesos; {@code null} en órdenes antiguas. */
    private Double total;

    public OrdenDTO(String documentoPaciente, String codigoEntidad, String nombreMedicamento, int cantidadMedicamento) {
        this(documentoPaciente, codigoEntidad, nombreMedicamento, cantidadMedicamento, null, null);
    }

    public OrdenDTO(String documentoPaciente, String codigoEntidad, String nombreMedicamento, int cantidadMedicamento,
            LocalDateTime fecha, Double total) {
        this.documentoPaciente = documentoPaciente;
        this.codigoEntidad = codigoEntidad;
        this.nombreMedicamento = nombreMedicamento;
        this.cantidadMedicamento = cantidadMedicamento;
        this.fecha = fecha;
        this.total = total;
    }

    // Getters y Setters
    public String getDocumentoPaciente() { return documentoPaciente; }
    public void setDocumentoPaciente(String documentoPaciente) { this.documentoPaciente = documentoPaciente; }

    public String getCodigoEntidad() { return codigoEntidad; }
    public void setCodigoEntidad(String codigoEntidad) { this.codigoEntidad = codigoEntidad; }

    public String getNombreMedicamento() { return nombreMedicamento; }
    public void setNombreMedicamento(String nombreMedicamento) { this.nombreMedicamento = nombreMedicamento; }

    public int getCantidadMedicamento() { return cantidadMedicamento; }
    public void setCantidadMedicamento(int cantidadMedicamento) { this.cantidadMedicamento = cantidadMedicamento; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}
