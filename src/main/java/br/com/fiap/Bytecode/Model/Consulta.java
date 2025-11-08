package br.com.fiap.Bytecode.Model;

import java.time.LocalDateTime;

public class Consulta {

    private int idConsulta;
    private int idPaciente; // Chave estrangeira para Paciente
    private LocalDateTime dataHoraConsulta;
    private String linkConsulta;
    private String statusConsulta;

    // (Opcional) Chave estrangeira para a tabela filha
    // private PosConsulta posConsulta;

    // Construtor vazio
    public Consulta() {
    }

    // Construtor com todos os campos (exceto ID)
    public Consulta(int idPaciente, LocalDateTime dataHoraConsulta, String linkConsulta, String statusConsulta) {
        this.idPaciente = idPaciente;
        this.dataHoraConsulta = dataHoraConsulta;
        this.linkConsulta = linkConsulta;
        this.statusConsulta = statusConsulta;
    }

    // --- Getters e Setters ---

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public LocalDateTime getDataHoraConsulta() {
        return dataHoraConsulta;
    }

    public void setDataHoraConsulta(LocalDateTime dataHoraConsulta) {
        this.dataHoraConsulta = dataHoraConsulta;
    }

    public String getLinkConsulta() {
        return linkConsulta;
    }

    public void setLinkConsulta(String linkConsulta) {
        this.linkConsulta = linkConsulta;
    }

    public String getStatusConsulta() {
        return statusConsulta;
    }

    public void setStatusConsulta(String statusConsulta) {
        this.statusConsulta = statusConsulta;
    }

    @Override
    public String toString() {
        return "Consulta{" +
                "idConsulta=" + idConsulta +
                ", idPaciente=" + idPaciente +
                ", dataHoraConsulta=" + dataHoraConsulta +
                ", statusConsulta='" + statusConsulta + '\'' +
                '}';
    }
}