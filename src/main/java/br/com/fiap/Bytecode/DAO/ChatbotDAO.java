package br.com.fiap.Bytecode.DAO;

import br.com.fiap.Bytecode.Model.Chatbot;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatbotDAO {

    private Connection connection;

    public ChatbotDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere uma pergunta do chatbot, deixando o banco gerar o ID e a data.
     * Atualiza o objeto 'chatbot' com os valores gerados.
     */
    public void inserir(Chatbot chatbot) throws SQLException {
        // O SQL só informa as colunas que o Java provê
        String sql = "INSERT INTO TB_BTC_CHATBOT (id_paciente, pergunta_usuario) VALUES (?, ?)";

        // Pedimos ao JDBC para retornar AMBAS as colunas geradas/default
        try (PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_chatbot", "data_pergunta"})) {

            ps.setInt(1, chatbot.getIdPaciente());
            ps.setString(2, chatbot.getPerguntaUsuario());

            ps.executeUpdate();

            // Recupera os valores gerados
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    // Coluna 1: id_chatbot
                    chatbot.setIdChatbot(rs.getInt(1));
                    // Coluna 2: data_pergunta (vem como Timestamp)
                    chatbot.setDataPergunta(rs.getTimestamp(2).toLocalDateTime());

                    System.out.println("Pergunta do Chatbot inserida. ID: " + chatbot.getIdChatbot());
                }
            }
        }
    }

    /**
     * Busca um registro de chatbot pelo seu ID (chave primária).
     */
    public Chatbot buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_CHATBOT WHERE id_chatbot = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarChatbot(rs);
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Lista todo o histórico de perguntas de um paciente específico.
     */
    public List<Chatbot> buscarPorIdPaciente(int idPaciente) throws SQLException {
        List<Chatbot> lista = new ArrayList<>();
        String sql = "SELECT * FROM TB_BTC_CHATBOT WHERE id_paciente = ? ORDER BY data_pergunta DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(instanciarChatbot(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Atualiza uma pergunta (embora seja incomum, pode ser útil).
     */
    public void atualizar(Chatbot chatbot) throws SQLException {
        String sql = "UPDATE TB_BTC_CHATBOT SET id_paciente = ?, pergunta_usuario = ?, data_pergunta = ? WHERE id_chatbot = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, chatbot.getIdPaciente());
            ps.setString(2, chatbot.getPerguntaUsuario());
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(chatbot.getDataPergunta()));
            ps.setInt(4, chatbot.getIdChatbot());

            ps.executeUpdate();
        }
    }

    /**
     * Exclui uma pergunta do histórico.
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM TB_BTC_CHATBOT WHERE id_chatbot = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Método utilitário para criar um objeto Chatbot a partir de um ResultSet.
     */
    private Chatbot instanciarChatbot(ResultSet rs) throws SQLException {
        Chatbot chatbot = new Chatbot();
        chatbot.setIdChatbot(rs.getInt("id_chatbot"));
        chatbot.setIdPaciente(rs.getInt("id_paciente"));
        chatbot.setPerguntaUsuario(rs.getString("pergunta_usuario"));
        chatbot.setDataPergunta(rs.getTimestamp("data_pergunta").toLocalDateTime());
        return chatbot;
    }
}