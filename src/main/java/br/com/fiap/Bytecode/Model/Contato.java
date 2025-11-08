package br.com.fiap.Bytecode.Model;

public class Contato {

    private int idContato;
    private int idPaciente; // Chave estrangeira para Paciente
    private String email;
    private String telefone;

    // Construtor vazio
    public Contato() {
    }

    // Construtor com todos os campos (exceto ID, que será gerado)
    public Contato(int idPaciente, String email, String telefone) {
        this.idPaciente = idPaciente;
        this.email = email;
        this.telefone = telefone;
    }

    // --- Getters e Setters ---

    public int getIdContato() {
        return idContato;
    }

    public void setIdContato(int idContato) {
        this.idContato = idContato;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return "Contato{" +
                "idContato=" + idContato +
                ", idPaciente=" + idPaciente +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}