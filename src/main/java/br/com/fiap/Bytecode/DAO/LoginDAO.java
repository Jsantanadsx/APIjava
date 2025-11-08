package br.com.fiap.Bytecode.DAO;

import br.com.fiap.Bytecode.Model.Login;
import java.sql.*;

public class LoginDAO {

    private Connection connection;

    public LoginDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere um login no banco e atualiza o objeto 'login' com o ID gerado.
     */
    public void inserir(Login login) throws SQLException {
        // O SQL não informa o 'id_login'
        String sql = "INSERT INTO TB_BTC_LOGIN (id_paciente, email, senha) VALUES (?, ?, ?)";

        // Pede ao PreparedStatement para retornar a chave gerada (id_login)
        try (PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_login"})) {

            ps.setInt(1, login.getIdPaciente());
            ps.setString(2, login.getEmail());
            ps.setString(3, login.getSenha()); // Idealmente, a senha já deve estar criptografada aqui

            ps.executeUpdate();

            // Recupera o ID gerado
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    // Atualiza o objeto Java com o ID
                    login.setIdLogin(idGerado);
                    System.out.println("Login inserido com sucesso. ID: " + idGerado);
                }
            }
        }
    }

    /**
     * Busca um login pelo seu ID (chave primária).
     */
    public Login buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_LOGIN WHERE id_login = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarLogin(rs);
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Busca um login pelo email (para autenticação).
     */
    public Login buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_LOGIN WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarLogin(rs);
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Busca o login associado a um ID de paciente.
     */
    public Login buscarPorIdPaciente(int idPaciente) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_LOGIN WHERE id_paciente = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarLogin(rs);
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Atualiza os dados de um login (ex: trocar senha).
     */
    public void atualizar(Login login) throws SQLException {
        String sql = "UPDATE TB_BTC_LOGIN SET id_paciente = ?, email = ?, senha = ? WHERE id_login = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, login.getIdPaciente());
            ps.setString(2, login.getEmail());
            ps.setString(3, login.getSenha());
            ps.setInt(4, login.getIdLogin());

            ps.executeUpdate();
        }
    }

    /**
     * Exclui um login do banco pelo seu ID.
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM TB_BTC_LOGIN WHERE id_login = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Método utilitário para criar um objeto Login a partir de um ResultSet.
     */
    private Login instanciarLogin(ResultSet rs) throws SQLException {
        Login login = new Login();
        login.setIdLogin(rs.getInt("id_login"));
        login.setIdPaciente(rs.getInt("id_paciente"));
        login.setEmail(rs.getString("email"));
        login.setSenha(rs.getString("senha"));
        return login;
    }
}