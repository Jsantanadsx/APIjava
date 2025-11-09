package br.com.fiap.Bytecode.Model;

import java.time.LocalDateTime;

public class Chatbot {

    private int idChatbot;
    private int idPaciente; // Chave estrangeira para Paciente
    private String perguntaUsuario;
    private LocalDateTime dataPergunta; // O banco preenche com DEFAULT SYSTIMESTAMP

    // Construtor vazio
    public Chatbot() {
    }

    // Construtor para inserir (sem ID e sem data)
    public Chatbot(int idPaciente, String perguntaUsuario) {
        this.idPaciente = idPaciente;
        this.perguntaUsuario = perguntaUsuario;
    }

    // --- Getters e Setters ---

    public int getIdChatbot() {
        return idChatbot;
    }

    public void setIdChatbot(int idChatbot) {
        this.idChatbot = idChatbot;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getPerguntaUsuario() {
        return perguntaUsuario;
    }

    public void setPerguntaUsuario(String perguntaUsuario) {
        this.perguntaUsuario = perguntaUsuario;
    }

    public LocalDateTime getDataPergunta() {
        return dataPergunta;
    }

    public void setDataPergunta(LocalDateTime dataPergunta) {
        this.dataPergunta = dataPergunta;
    }

    @Override
    public String toString() {
        return "Chatbot{" +
                "idChatbot=" + idChatbot +
                ", idPaciente=" + idPaciente +
                ", perguntaUsuario='" + perguntaUsuario + '\'' +
                ", dataPergunta=" + dataPergunta +
                '}';
    }
}