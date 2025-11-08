package br.com.fiap.Bytecode.Model;

public class Login {

    private int idLogin;
    private int idPaciente; // Chave estrangeira para Paciente
    private String email;
    private String senha;

    // Construtor vazio
    public Login() {
    }

    // Construtor com todos os campos (exceto ID, que será gerado)
    public Login(int idPaciente, String email, String senha) {
        this.idPaciente = idPaciente;
        this.email = email;
        this.senha = senha;
    }

    // --- Getters e Setters ---

    public int getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(int idLogin) {
        this.idLogin = idLogin;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Login{" +
                "idLogin=" + idLogin +
                ", idPaciente=" + idPaciente +
                ", email='" + email + '\'' +
                '}';
    }
}