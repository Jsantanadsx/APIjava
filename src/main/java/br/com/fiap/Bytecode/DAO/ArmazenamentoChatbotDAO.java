package br.com.fiap.Bytecode.DAO;

import br.com.fiap.Bytecode.Model.ArmazenamentoChatbot;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArmazenamentoChatbotDAO {

    private Connection connection;

    public ArmazenamentoChatbotDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere uma resposta do chatbot e atualiza o objeto 'armazenamento' com o ID gerado.
     */
    public void inserir(ArmazenamentoChatbot armazenamento) throws SQLException {
        String sql = "INSERT INTO TB_BTC_ARMAZENAMENTO_CHATBOT (id_chatbot, resposta_usuario) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_armazenamento_chatbot"})) {

            ps.setInt(1, armazenamento.getIdChatbot());
            ps.setString(2, armazenamento.getRespostaUsuario());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    armazenamento.setIdArmazenamentoChatbot(idGerado);
                    System.out.println("ArmazenamentoChatbot inserido com sucesso. ID: " + idGerado);
                }
            }
        }
    }

    /**
     * Busca um registro de armazenamento pelo seu ID (chave primária).
     */
    public ArmazenamentoChatbot buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_ARMAZENAMENTO_CHATBOT WHERE id_armazenamento_chatbot = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarArmazenamento(rs);
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Lista todas as respostas associadas a uma pergunta específica (id_chatbot).
     * No seu modelo, parece ser uma relação 1-para-1 ou 1-para-muitos,
     * então este método pode retornar um ou mais registros.
     */
    public List<ArmazenamentoChatbot> buscarPorIdChatbot(int idChatbot) throws SQLException {
        List<ArmazenamentoChatbot> lista = new ArrayList<>();
        String sql = "SELECT * FROM TB_BTC_ARMAZENAMENTO_CHATBOT WHERE id_chatbot = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idChatbot);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(instanciarArmazenamento(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Atualiza uma resposta de armazenamento.
     */
    public void atualizar(ArmazenamentoChatbot armazenamento) throws SQLException {
        String sql = "UPDATE TB_BTC_ARMAZENAMENTO_CHATBOT SET id_chatbot = ?, resposta_usuario = ? WHERE id_armazenamento_chatbot = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, armazenamento.getIdChatbot());
            ps.setString(2, armazenamento.getRespostaUsuario());
            ps.setInt(3, armazenamento.getIdArmazenamentoChatbot());

            ps.executeUpdate();
        }
    }

    /**
     * Exclui um registro de armazenamento pelo seu ID.
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM TB_BTC_ARMAZENAMENTO_CHATBOT WHERE id_armazenamento_chatbot = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Método utilitário para criar um objeto ArmazenamentoChatbot a partir de um ResultSet.
     */
    private ArmazenamentoChatbot instanciarArmazenamento(ResultSet rs) throws SQLException {
        ArmazenamentoChatbot arm = new ArmazenamentoChatbot();
        arm.setIdArmazenamentoChatbot(rs.getInt("id_armazenamento_chatbot"));
        arm.setIdChatbot(rs.getInt("id_chatbot"));
        arm.setRespostaUsuario(rs.getString("resposta_usuario"));
        return arm;
    }
}