package co.edu.unbosque.model.persistence;

import java.io.Serializable;

public class MedicamentoDTO implements Serializable{
	private static final long serialVersionUID = 1L; // Definir un serialVersionUID
    private long numId;
    private String nombre;
    private String empresa;
    private float precio;
    private int cantidad;

	public MedicamentoDTO(long numId, String nombre, String empresa, float precio, int cantidad) {
		this.cantidad = cantidad;
		this.empresa = empresa;
		this.nombre = nombre;
		this.numId = numId;
		this.precio = precio;
	}
	
	// Getters y Setters

	public long getNumId() {
		return numId;
	}

	public void setNumId(long numId) {
		this.numId = numId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

}