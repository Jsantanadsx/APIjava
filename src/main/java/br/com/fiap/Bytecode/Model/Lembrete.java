package br.com.fiap.Bytecode.Model;

import java.time.LocalDateTime;

public class Lembrete {

    private int idLembrete;
    private int idConsulta; // Chave estrangeira para Consulta
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataHoraEnvio; // Pode ser nulo
    private String descricaoMensagem;
    private String statusEnvio;

    // Construtor vazio
    public Lembrete() {
    }

    // Construtor com todos os campos (exceto ID)
    public Lembrete(int idConsulta, LocalDateTime dataAgendamento, LocalDateTime dataHoraEnvio, String descricaoMensagem, String statusEnvio) {
        this.idConsulta = idConsulta;
        this.dataAgendamento = dataAgendamento;
        this.dataHoraEnvio = dataHoraEnvio;
        this.descricaoMensagem = descricaoMensagem;
        this.statusEnvio = statusEnvio;
    }

    // --- Getters e Setters ---

    public int getIdLembrete() {
        return idLembrete;
    }

    public void setIdLembrete(int idLembrete) {
        this.idLembrete = idLembrete;
    }

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalDateTime getDataHoraEnvio() {
        return dataHoraEnvio;
    }

    public void setDataHoraEnvio(LocalDateTime dataHoraEnvio) {
        this.dataHoraEnvio = dataHoraEnvio;
    }

    public String getDescricaoMensagem() {
        return descricaoMensagem;
    }

    public void setDescricaoMensagem(String descricaoMensagem) {
        this.descricaoMensagem = descricaoMensagem;
    }

    public String getStatusEnvio() {
        return statusEnvio;
    }

    public void setStatusEnvio(String statusEnvio) {
        this.statusEnvio = statusEnvio;
    }

    @Override
    public String toString() {
        return "Lembrete{" +
                "idLembrete=" + idLembrete +
                ", idConsulta=" + idConsulta +
                ", dataAgendamento=" + dataAgendamento +
                ", statusEnvio='" + statusEnvio + '\'' +
                '}';
    }
}