package br.com.fiap.Bytecode.DAO;

import br.com.fiap.Bytecode.Model.Contato;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContatoDAO {

    private Connection connection;

    public ContatoDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere um contato no banco e atualiza o objeto 'contato' com o ID gerado.
     */
    public void inserir(Contato contato) throws SQLException {
        // O SQL não informa o 'id_contato'
        String sql = "INSERT INTO TB_BTC_CONTATO (id_paciente, email, telefone) VALUES (?, ?, ?)";

        // Pede ao PreparedStatement para retornar a chave gerada (id_contato)
        try (PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_contato"})) {

            ps.setInt(1, contato.getIdPaciente());
            ps.setString(2, contato.getEmail());
            ps.setString(3, contato.getTelefone());

            ps.executeUpdate();

            // Recupera o ID gerado
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    // Atualiza o objeto Java com o ID
                    contato.setIdContato(idGerado);
                    System.out.println("Contato inserido com sucesso. ID: " + idGerado);
                }
            }
        }
    }

    /**
     * Busca um contato pelo seu ID (chave primária).
     */
    public Contato buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_CONTATO WHERE id_contato = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Contato(
                            rs.getInt("id_paciente"),
                            rs.getString("email"),
                            rs.getString("telefone")
                    );
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Lista todos os contatos de um paciente específico.
     */
    public List<Contato> buscarPorIdPaciente(int idPaciente) throws SQLException {
        List<Contato> lista = new ArrayList<>();
        String sql = "SELECT * FROM TB_BTC_CONTATO WHERE id_paciente = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contato c = new Contato();
                    c.setIdContato(rs.getInt("id_contato"));
                    c.setIdPaciente(rs.getInt("id_paciente"));
                    c.setEmail(rs.getString("email"));
                    c.setTelefone(rs.getString("telefone"));
                    lista.add(c);
                }
            }
        }
        return lista;
    }

    /**
     * Atualiza os dados de um contato existente.
     */
    public void atualizar(Contato contato) throws SQLException {
        String sql = "UPDATE TB_BTC_CONTATO SET id_paciente = ?, email = ?, telefone = ? WHERE id_contato = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contato.getIdPaciente());
            ps.setString(2, contato.getEmail());
            ps.setString(3, contato.getTelefone());
            ps.setInt(4, contato.getIdContato());

            ps.executeUpdate();
        }
    }

    /**
     * Exclui um contato do banco pelo seu ID.
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM TB_BTC_CONTATO WHERE id_contato = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}