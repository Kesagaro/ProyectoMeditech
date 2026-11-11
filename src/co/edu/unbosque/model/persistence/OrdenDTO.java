package co.edu.unbosque.model.persistence;

public class OrdenDTO {
    private String documentoPaciente;
    private String codigoEntidad;
    private String nombreMedicamento;
    private int cantidadMedicamento;

    public OrdenDTO(String documentoPaciente, String codigoEntidad, String nombreMedicamento, int cantidadMedicamento) {
        this.documentoPaciente = documentoPaciente;
        this.codigoEntidad = codigoEntidad;
        this.nombreMedicamento = nombreMedicamento;
        this.cantidadMedicamento = cantidadMedicamento;
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
}
