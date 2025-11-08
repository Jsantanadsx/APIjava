package br.com.fiap.Bytecode.DAO;

import br.com.fiap.Bytecode.Model.PosConsulta;
import java.sql.*;

public class PosConsultaDAO {

    private Connection connection;

    public PosConsultaDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere os dados de pós-consulta e atualiza o objeto 'posConsulta' com o ID gerado.
     */
    public void inserir(PosConsulta posConsulta) throws SQLException {
        String sql = "INSERT INTO TB_BTC_POS_CONSULTA (id_consulta, comparecimento, diagnostico, receita_medica) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_pos_consulta"})) {

            ps.setInt(1, posConsulta.getIdConsulta());
            ps.setString(2, posConsulta.getComparecimento());
            ps.setString(3, posConsulta.getDiagnostico());
            ps.setString(4, posConsulta.getReceitaMedica());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    posConsulta.setIdPosConsulta(idGerado);
                    System.out.println("PosConsulta inserida com sucesso. ID: " + idGerado);
                }
            }
        }
    }

    /**
     * Busca os dados de pós-consulta pela sua ID (chave primária).
     */
    public PosConsulta buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_POS_CONSULTA WHERE id_pos_consulta = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarPosConsulta(rs);
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Busca os dados de pós-consulta usando o ID da Consulta (chave estrangeira).
     * Este é o método mais provável de ser usado.
     */
    public PosConsulta buscarPorIdConsulta(int idConsulta) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_POS_CONSULTA WHERE id_consulta = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarPosConsulta(rs);
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Atualiza os dados de uma pós-consulta existente.
     */
    public void atualizar(PosConsulta posConsulta) throws SQLException {
        String sql = "UPDATE TB_BTC_POS_CONSULTA SET id_consulta = ?, comparecimento = ?, diagnostico = ?, receita_medica = ? WHERE id_pos_consulta = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, posConsulta.getIdConsulta());
            ps.setString(2, posConsulta.getComparecimento());
            ps.setString(3, posConsulta.getDiagnostico());
            ps.setString(4, posConsulta.getReceitaMedica());
            ps.setInt(5, posConsulta.getIdPosConsulta());

            ps.executeUpdate();
        }
    }

    /**
     * Exclui os dados de pós-consulta pelo seu ID.
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM TB_BTC_POS_CONSULTA WHERE id_pos_consulta = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Método utilitário para criar um objeto PosConsulta a partir de um ResultSet.
     */
    private PosConsulta instanciarPosConsulta(ResultSet rs) throws SQLException {
        PosConsulta posConsulta = new PosConsulta();
        posConsulta.setIdPosConsulta(rs.getInt("id_pos_consulta"));
        posConsulta.setIdConsulta(rs.getInt("id_consulta"));
        posConsulta.setComparecimento(rs.getString("comparecimento"));
        posConsulta.setDiagnostico(rs.getString("diagnostico"));
        posConsulta.setReceitaMedica(rs.getString("receita_medica"));
        return posConsulta;
    }
}