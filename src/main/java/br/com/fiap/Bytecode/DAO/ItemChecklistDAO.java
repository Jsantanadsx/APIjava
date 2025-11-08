package br.com.fiap.Bytecode.DAO;

import br.com.fiap.Bytecode.Model.ItemChecklist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemChecklistDAO {

    private Connection connection;

    public ItemChecklistDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere um item de checklist no banco e atualiza o objeto 'item' com o ID gerado.
     */
    public void inserir(ItemChecklist item) throws SQLException {
        String sql = "INSERT INTO TB_BTC_ITEM_CHECKLIST (nome_item_checklist, sugestao_checklist) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_item_checklist"})) {

            ps.setString(1, item.getNomeItemChecklist());
            ps.setString(2, item.getSugestaoChecklist());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    item.setIdItemChecklist(idGerado);
                    System.out.println("ItemChecklist inserido com sucesso. ID: " + idGerado);
                }
            }
        }
    }

    /**
     * Busca um item de checklist pelo seu ID (chave primária).
     */
    public ItemChecklist buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TB_BTC_ITEM_CHECKLIST WHERE id_item_checklist = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarItemChecklist(rs);
                }
            }
        }
        return null; // Não encontrou
    }

    /**
     * Lista todos os itens de checklist do banco.
     */
    public List<ItemChecklist> listar() throws SQLException {
        List<ItemChecklist> lista = new ArrayList<>();
        String sql = "SELECT * FROM TB_BTC_ITEM_CHECKLIST ORDER BY nome_item_checklist";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(instanciarItemChecklist(rs));
            }
        }
        return lista;
    }

    /**
     * Atualiza os dados de um item de checklist existente.
     */
    public void atualizar(ItemChecklist item) throws SQLException {
        String sql = "UPDATE TB_BTC_ITEM_CHECKLIST SET nome_item_checklist = ?, sugestao_checklist = ? WHERE id_item_checklist = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, item.getNomeItemChecklist());
            ps.setString(2, item.getSugestaoChecklist());
            ps.setInt(3, item.getIdItemChecklist());

            ps.executeUpdate();
        }
    }

    /**
     * Exclui um item de checklist do banco pelo seu ID.
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM TB_BTC_ITEM_CHECKLIST WHERE id_item_checklist = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Método utilitário para criar um objeto ItemChecklist a partir de um ResultSet.
     */
    private ItemChecklist instanciarItemChecklist(ResultSet rs) throws SQLException {
        ItemChecklist item = new ItemChecklist();
        item.setIdItemChecklist(rs.getInt("id_item_checklist"));
        item.setNomeItemChecklist(rs.getString("nome_item_checklist"));
        item.setSugestaoChecklist(rs.getString("sugestao_checklist"));
        return item;
    }
}