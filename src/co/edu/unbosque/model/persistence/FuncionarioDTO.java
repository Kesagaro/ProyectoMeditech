package co.edu.unbosque.model.persistence;

public class FuncionarioDTO {
    private String cedula;
    private String password;
    private String nombre;
    private String correo;

    public FuncionarioDTO(String cedula, String password, String nombre, String correo) {
        this.cedula = cedula;
        this.password = password;
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }


    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return cedula + "," + password + "," + nombre + "," + correo;
    }
}