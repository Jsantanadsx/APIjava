package br.com.fiap.Bytecode.Model;

public class ArmazenamentoChatbot {

    private int idArmazenamentoChatbot;
    private int idChatbot; // Chave estrangeira para Chatbot
    private String respostaUsuario;

    // Construtor vazio
    public ArmazenamentoChatbot() {
    }

    // Construtor com todos os campos (exceto ID)
    public ArmazenamentoChatbot(int idChatbot, String respostaUsuario) {
        this.idChatbot = idChatbot;
        this.respostaUsuario = respostaUsuario;
    }

    // --- Getters e Setters ---

    public int getIdArmazenamentoChatbot() {
        return idArmazenamentoChatbot;
    }

    public void setIdArmazenamentoChatbot(int idArmazenamentoChatbot) {
        this.idArmazenamentoChatbot = idArmazenamentoChatbot;
    }

    public int getIdChatbot() {
        return idChatbot;
    }

    public void setIdChatbot(int idChatbot) {
        this.idChatbot = idChatbot;
    }

    public String getRespostaUsuario() {
        return respostaUsuario;
    }

    public void setRespostaUsuario(String respostaUsuario) {
        this.respostaUsuario = respostaUsuario;
    }

    @Override
    public String toString() {
        return "ArmazenamentoChatbot{" +
                "idArmazenamentoChatbot=" + idArmazenamentoChatbot +
                ", idChatbot=" + idChatbot +
                ", respostaUsuario='" + respostaUsuario + '\'' +
                '}';
    }
}