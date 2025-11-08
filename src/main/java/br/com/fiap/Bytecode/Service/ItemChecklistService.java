package br.com.fiap.Bytecode.Service;

import br.com.fiap.Bytecode.DAO.ItemChecklistDAO;
import br.com.fiap.Bytecode.Model.ItemChecklist;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Serviço opcional para gerenciar a lista mestre de Itens de Checklist.
 * Útil apenas se houver uma área de "Admin" para criar/editar itens.
 */
public class ItemChecklistService {

    private ItemChecklistDAO itemChecklistDAO;

    public ItemChecklistService(Connection connection) {
        this.itemChecklistDAO = new ItemChecklistDAO(connection);
    }


    //Lista todos os itens de checklist mestres.
    public List<ItemChecklist> listarTodosItensMestres() throws SQLException {
        return itemChecklistDAO.listar();
    }

    //Cria um novo item de checklist mestre.
    public void criarNovoItemMestre(String nome, String sugestao) throws Exception {
        if (nome == null || nome.trim().isEmpty()) {
            throw new Exception("O nome do item não pode ser vazio.");
        }

        ItemChecklist novoItem = new ItemChecklist(nome, sugestao);
        itemChecklistDAO.inserir(novoItem);
    }
}