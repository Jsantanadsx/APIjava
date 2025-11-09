package br.com.fiap.Bytecode.Model;

public class ArmazenamentoDadosConsulta {

    private int idArmazenamento;
    private int idConsulta; // Chave estrangeira para Consulta
    private String dadoArmazenado;

    // Construtor vazio
    public ArmazenamentoDadosConsulta() {
    }

    // Construtor com todos os campos (exceto ID)
    public ArmazenamentoDadosConsulta(int idConsulta, String dadoArmazenado) {
        this.idConsulta = idConsulta;
        this.dadoArmazenado = dadoArmazenado;
    }

    // --- Getters e Setters ---

    public int getIdArmazenamento() {
        return idArmazenamento;
    }

    public void setIdArmazenamento(int idArmazenamento) {
        this.idArmazenamento = idArmazenamento;
    }

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getDadoArmazenado() {
        return dadoArmazenado;
    }

    public void setDadoArmazenado(String dadoArmazenado) {
        this.dadoArmazenado = dadoArmazenado;
    }

    @Override
    public String toString() {
        return "ArmazenamentoDadosConsulta{" +
                "idArmazenamento=" + idArmazenamento +
                ", idConsulta=" + idConsulta +
                ", dadoArmazenado='" + dadoArmazenado + '\'' +
                '}';
    }
}