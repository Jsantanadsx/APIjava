package br.com.fiap.Bytecode.Model;

public class PosConsulta {

    private int idPosConsulta;
    private int idConsulta; // Chave estrangeira para Consulta
    private String comparecimento; // CHAR(1) no banco, 'S' ou 'N'
    private String diagnostico;
    private String receitaMedica;

    // Construtor vazio
    public PosConsulta() {
    }

    // Construtor com todos os campos (exceto ID)
    public PosConsulta(int idConsulta, String comparecimento, String diagnostico, String receitaMedica) {
        this.idConsulta = idConsulta;
        this.comparecimento = comparecimento;
        this.diagnostico = diagnostico;
        this.receitaMedica = receitaMedica;
    }

    // --- Getters e Setters ---

    public int getIdPosConsulta() {
        return idPosConsulta;
    }

    public void setIdPosConsulta(int idPosConsulta) {
        this.idPosConsulta = idPosConsulta;
    }

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getComparecimento() {
        return comparecimento;
    }

    public void setComparecimento(String comparecimento) {
        this.comparecimento = comparecimento;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getReceitaMedica() {
        return receitaMedica;
    }

    public void setReceitaMedica(String receitaMedica) {
        this.receitaMedica = receitaMedica;
    }

    @Override
    public String toString() {
        return "PosConsulta{" +
                "idPosConsulta=" + idPosConsulta +
                ", idConsulta=" + idConsulta +
                ", comparecimento='" + comparecimento + '\'' +
                ", diagnostico='" + (diagnostico != null ? diagnostico.substring(0, Math.min(diagnostico.length(), 20)) + "..." : "N/A") + '\'' +
                '}';
    }
}