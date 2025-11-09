package br.com.fiap.Bytecode.DAO;

import br.com.fiap.Bytecode.Model.ChecklistPaciente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChecklistPacienteDAO {

    private Connection connection;

    public ChecklistPacienteDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere uma nova associação entre consulta e item de checklist.
     * Não há ID gerado aqui.
     */
    public void inserir(ChecklistPaciente checklist) throws SQLException {
        String sql = "INSERT INTO TB_BTC_CHECKLIST_PACIENTE (id_consulta, id_item_checklist, status_item) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, checklist.getIdConsulta());
            ps.setInt(2, checklist.getIdItemChecklist());
            ps.setString(3, checklist.getStatusItem());

            ps.executeUpdate();
            System.out.println("Associação de checklist inserida com sucesso.");
        }
    }

    /**
     * Busca um registro específico pela chave primária composta.
     */
    public ChecklistPaciente buscarPorId(int idConsulta, int idItemChecklist) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_CHECKLIST_PACIENTE WHERE id_consulta = ? AND id_item_checklist = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ps.setInt(2, idItemChecklist);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarChecklistPaciente(rs);
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Lista todos os itens de checklist de uma consulta específica.
     * Este será um dos métodos mais usados.
     */
    public List<ChecklistPaciente> buscarPorIdConsulta(int idConsulta) throws SQLException {
        List<ChecklistPaciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM TB_BTC_CHECKLIST_PACIENTE WHERE id_consulta = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(instanciarChecklistPaciente(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Atualiza o status de um item de checklist para uma consulta.
     */
    public void atualizarStatus(ChecklistPaciente checklist) throws SQLException {
        String sql = "UPDATE TB_BTC_CHECKLIST_PACIENTE SET status_item = ? WHERE id_consulta = ? AND id_item_checklist = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, checklist.getStatusItem());
            ps.setInt(2, checklist.getIdConsulta());
            ps.setInt(3, checklist.getIdItemChecklist());

            ps.executeUpdate();
        }
    }

    /**
     * Exclui uma associação de checklist do banco.
     */
    public void excluir(int idConsulta, int idItemChecklist) throws SQLException {
        String sql = "DELETE FROM TB_BTC_CHECKLIST_PACIENTE WHERE id_consulta = ? AND id_item_checklist = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ps.setInt(2, idItemChecklist);
            ps.executeUpdate();
        }
    }

    /**
     * Método utilitário para criar um objeto ChecklistPaciente a partir de um ResultSet.
     */
    private ChecklistPaciente instanciarChecklistPaciente(ResultSet rs) throws SQLException {
        ChecklistPaciente cp = new ChecklistPaciente();
        cp.setIdConsulta(rs.getInt("id_consulta"));
        cp.setIdItemChecklist(rs.getInt("id_item_checklist"));
        cp.setStatusItem(rs.getString("status_item"));
        return cp;
    }
}