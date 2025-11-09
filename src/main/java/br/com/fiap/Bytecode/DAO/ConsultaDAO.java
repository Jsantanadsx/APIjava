package br.com.fiap.Bytecode.DAO;

import br.com.fiap.Bytecode.Model.Consulta;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {

    private Connection connection;

    public ConsultaDAO(Connection connection) {
        this.connection = connection;
    }

    public void inserir(Consulta consulta) throws SQLException {
        String sql = "INSERT INTO TB_BTC_CONSULTA (id_paciente, data_hora_consulta, link_consulta, status_consulta) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_consulta"})) {

            ps.setInt(1, consulta.getIdPaciente());
            // Converte de java.time.LocalDateTime para java.sql.Timestamp
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(consulta.getDataHoraConsulta()));
            ps.setString(3, consulta.getLinkConsulta());
            ps.setString(4, consulta.getStatusConsulta());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    consulta.setIdConsulta(idGerado);
                    System.out.println("Consulta inserida com sucesso. ID: " + idGerado);
                }
            }
        }
    }

    /**
     * Busca uma consulta pelo seu ID (chave primária).
     */
    public Consulta buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_CONSULTA WHERE id_consulta = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarConsulta(rs);
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Lista todas as consultas de um paciente específico.
     */
    public List<Consulta> buscarPorIdPaciente(int idPaciente) throws SQLException {
        List<Consulta> lista = new ArrayList<>();
        String sql = "SELECT * FROM TB_BTC_CONSULTA WHERE id_paciente = ? ORDER BY data_hora_consulta DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(instanciarConsulta(rs));
                }
            }
        }
        return lista;
    }

    public List<Consulta> listar() throws SQLException {
        List<Consulta> lista = new ArrayList<>();
        String sql = "SELECT * FROM TB_BTC_CONSULTA ORDER BY data_hora_consulta DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(instanciarConsulta(rs));
            }
        }
        return lista;
    }


    public void atualizar(Consulta consulta) throws SQLException {
        String sql = "UPDATE TB_BTC_CONSULTA SET id_paciente = ?, data_hora_consulta = ?, link_consulta = ?, status_consulta = ? WHERE id_consulta = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, consulta.getIdPaciente());
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(consulta.getDataHoraConsulta()));
            ps.setString(3, consulta.getLinkConsulta());
            ps.setString(4, consulta.getStatusConsulta());
            ps.setInt(5, consulta.getIdConsulta());

            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM TB_BTC_CONSULTA WHERE id_consulta = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Consulta instanciarConsulta(ResultSet rs) throws SQLException {
        Consulta consulta = new Consulta();
        consulta.setIdConsulta(rs.getInt("id_consulta"));
        consulta.setIdPaciente(rs.getInt("id_paciente"));
        // Converte de java.sql.Timestamp para java.time.LocalDateTime
        consulta.setDataHoraConsulta(rs.getTimestamp("data_hora_consulta").toLocalDateTime());
        consulta.setLinkConsulta(rs.getString("link_consulta"));
        consulta.setStatusConsulta(rs.getString("status_consulta"));
        return consulta;
    }
}