package co.edu.unbosque.model.persistence;

public class FuncionarioDTO {
    private String cedula;
    private String password;


    public FuncionarioDTO(String cedula, String password) {
        this.cedula = cedula;
        this.password = password;
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
        return cedula + "," + password;
    }
}