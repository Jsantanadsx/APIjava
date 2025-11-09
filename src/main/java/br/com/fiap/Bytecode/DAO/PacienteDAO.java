package br.com.fiap.Bytecode.DAO;

import br.com.fiap.Bytecode.Model.Paciente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    private Connection connection;

    public PacienteDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere um paciente no banco de dados e ATUALIZA o objeto 'paciente'
     * com o ID gerado pelo banco.
     */
    public void inserir(Paciente paciente) throws SQLException {
        // 1. O SQL não informa o 'id_paciente'
        String sql = "INSERT INTO TB_BTC_PACIENTE (nome, cpf, data_nascimento, genero) VALUES (?, ?, ?, ?)";

        // 2. Pedimos ao PreparedStatement que retorne a chave gerada (o ID)
        try (PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_paciente"})) {

            ps.setString(1, paciente.getNome());
            ps.setString(2, paciente.getCpf());
            // Converte de java.time.LocalDate para java.sql.Date
            ps.setDate(3, java.sql.Date.valueOf(paciente.getDataNascimento()));
            ps.setString(4, paciente.getGenero());

            ps.executeUpdate();

            // 3. Recuperamos o ID gerado
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    // 4. Atualizamos o objeto Java com o ID
                    paciente.setIdPaciente(idGerado);
                    System.out.println("Paciente inserido com sucesso. ID: " + idGerado);
                }
            }
        }
    }

    /**
     * Lista todos os pacientes do banco.
     */
    public List<Paciente> listar() throws SQLException {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM TB_BTC_PACIENTE ORDER BY nome";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Paciente p = new Paciente();
                p.setIdPaciente(rs.getInt("id_paciente"));
                p.setNome(rs.getString("nome"));
                p.setCpf(rs.getString("cpf"));
                // Converte de java.sql.Date para java.time.LocalDate
                p.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                p.setGenero(rs.getString("genero"));

                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * Busca um paciente pelo seu ID.
     */
    public Paciente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_PACIENTE WHERE id_paciente = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Paciente p = new Paciente();
                    p.setIdPaciente(rs.getInt("id_paciente"));
                    p.setNome(rs.getString("nome"));
                    p.setCpf(rs.getString("cpf"));
                    p.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                    p.setGenero(rs.getString("genero"));
                    return p;
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Atualiza os dados de um paciente existente.
     */
    public void atualizar(Paciente paciente) throws SQLException {
        String sql = "UPDATE TB_BTC_PACIENTE SET nome = ?, cpf = ?, data_nascimento = ?, genero = ? WHERE id_paciente = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, paciente.getNome());
            ps.setString(2, paciente.getCpf());
            ps.setDate(3, java.sql.Date.valueOf(paciente.getDataNascimento()));
            ps.setString(4, paciente.getGenero());
            ps.setInt(5, paciente.getIdPaciente());

            ps.executeUpdate();
        }
    }

    /**
     * Exclui um paciente do banco pelo ID.
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM TB_BTC_PACIENTE WHERE id_paciente = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}