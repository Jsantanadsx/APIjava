package br.com.fiap.Bytecode.DAO;

import br.com.fiap.Bytecode.Model.Lembrete;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LembreteDAO {

    private Connection connection;

    public LembreteDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere um lembrete no banco e atualiza o objeto 'lembrete' com o ID gerado.
     */
    public void inserir(Lembrete lembrete) throws SQLException {
        String sql = "INSERT INTO TB_BTC_LEMBRETE (id_consulta, data_agendamento, data_hora_envio, descricao_mensagem, status_envio) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_lembrete"})) {

            ps.setInt(1, lembrete.getIdConsulta());
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(lembrete.getDataAgendamento()));

            // Tratamento para data/hora nula
            if (lembrete.getDataHoraEnvio() != null) {
                ps.setTimestamp(3, java.sql.Timestamp.valueOf(lembrete.getDataHoraEnvio()));
            } else {
                ps.setNull(3, java.sql.Types.TIMESTAMP);
            }

            ps.setString(4, lembrete.getDescricaoMensagem());
            ps.setString(5, lembrete.getStatusEnvio());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    lembrete.setIdLembrete(idGerado);
                    System.out.println("Lembrete inserido com sucesso. ID: " + idGerado);
                }
            }
        }
    }

    /**
     * Busca um lembrete pelo seu ID (chave primária).
     */
    public Lembrete buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_LEMBRETE WHERE id_lembrete = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarLembrete(rs);
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Lista todos os lembretes de uma consulta específica.
     */
    public List<Lembrete> buscarPorIdConsulta(int idConsulta) throws SQLException {
        List<Lembrete> lista = new ArrayList<>();
        String sql = "SELECT * FROM TB_BTC_LEMBRETE WHERE id_consulta = ? ORDER BY data_agendamento";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(instanciarLembrete(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Atualiza os dados de um lembrete existente.
     */
    public void atualizar(Lembrete lembrete) throws SQLException {
        String sql = "UPDATE TB_BTC_LEMBRETE SET id_consulta = ?, data_agendamento = ?, data_hora_envio = ?, descricao_mensagem = ?, status_envio = ? WHERE id_lembrete = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, lembrete.getIdConsulta());
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(lembrete.getDataAgendamento()));

            // Tratamento para data/hora nula
            if (lembrete.getDataHoraEnvio() != null) {
                ps.setTimestamp(3, java.sql.Timestamp.valueOf(lembrete.getDataHoraEnvio()));
            } else {
                ps.setNull(3, java.sql.Types.TIMESTAMP);
            }

            ps.setString(4, lembrete.getDescricaoMensagem());
            ps.setString(5, lembrete.getStatusEnvio());
            ps.setInt(6, lembrete.getIdLembrete());

            ps.executeUpdate();
        }
    }

    /**
     * Exclui um lembrete do banco pelo seu ID.
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM TB_BTC_LEMBRETE WHERE id_lembrete = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Método utilitário para criar um objeto Lembrete a partir de um ResultSet.
     */
    private Lembrete instanciarLembrete(ResultSet rs) throws SQLException {
        Lembrete lembrete = new Lembrete();
        lembrete.setIdLembrete(rs.getInt("id_lembrete"));
        lembrete.setIdConsulta(rs.getInt("id_consulta"));
        lembrete.setDataAgendamento(rs.getTimestamp("data_agendamento").toLocalDateTime());

        // Tratamento para data/hora nula
        Timestamp tsEnvio = rs.getTimestamp("data_hora_envio");
        if (tsEnvio != null) {
            lembrete.setDataHoraEnvio(tsEnvio.toLocalDateTime());
        } else {
            lembrete.setDataHoraEnvio(null);
        }

        lembrete.setDescricaoMensagem(rs.getString("descricao_mensagem"));
        lembrete.setStatusEnvio(rs.getString("status_envio"));
        return lembrete;
    }
}