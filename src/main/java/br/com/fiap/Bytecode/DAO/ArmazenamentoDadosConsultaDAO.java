package br.com.fiap.Bytecode.DAO;

import br.com.fiap.Bytecode.Model.ArmazenamentoDadosConsulta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArmazenamentoDadosConsultaDAO {

    private Connection connection;

    public ArmazenamentoDadosConsultaDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere um dado armazenado e atualiza o objeto 'armazenamento' com o ID gerado.
     */
    public void inserir(ArmazenamentoDadosConsulta armazenamento) throws SQLException {
        String sql = "INSERT INTO TB_BTC_ARMAZENAMENTO_DADOS_CONSULTA (id_consulta, dado_armazenado) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_armazenamento"})) {

            ps.setInt(1, armazenamento.getIdConsulta());
            ps.setString(2, armazenamento.getDadoArmazenado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    armazenamento.setIdArmazenamento(idGerado);
                    System.out.println("ArmazenamentoDadosConsulta inserido com sucesso. ID: " + idGerado);
                }
            }
        }
    }

    /**
     * Busca um dado armazenado pelo seu ID (chave primária).
     */
    public ArmazenamentoDadosConsulta buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_ARMAZENAMENTO_DADOS_CONSULTA WHERE id_armazenamento = ?";

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
     * Lista todos os dados armazenados de uma consulta específica.
     */
    public List<ArmazenamentoDadosConsulta> buscarPorIdConsulta(int idConsulta) throws SQLException {
        List<ArmazenamentoDadosConsulta> lista = new ArrayList<>();
        String sql = "SELECT * FROM TB_BTC_ARMAZENAMENTO_DADOS_CONSULTA WHERE id_consulta = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(instanciarArmazenamento(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Atualiza um dado armazenado.
     */
    public void atualizar(ArmazenamentoDadosConsulta armazenamento) throws SQLException {
        String sql = "UPDATE TB_BTC_ARMAZENAMENTO_DADOS_CONSULTA SET id_consulta = ?, dado_armazenado = ? WHERE id_armazenamento = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, armazenamento.getIdConsulta());
            ps.setString(2, armazenamento.getDadoArmazenado());
            ps.setInt(3, armazenamento.getIdArmazenamento());

            ps.executeUpdate();
        }
    }

    /**
     * Exclui um dado armazenado pelo seu ID.
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM TB_BTC_ARMAZENAMENTO_DADOS_CONSULTA WHERE id_armazenamento = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Método utilitário para criar um objeto ArmazenamentoDadosConsulta a partir de um ResultSet.
     */
    private ArmazenamentoDadosConsulta instanciarArmazenamento(ResultSet rs) throws SQLException {
        ArmazenamentoDadosConsulta arm = new ArmazenamentoDadosConsulta();
        arm.setIdArmazenamento(rs.getInt("id_armazenamento"));
        arm.setIdConsulta(rs.getInt("id_consulta"));
        arm.setDadoArmazenado(rs.getString("dado_armazenado"));
        return arm;
    }
}