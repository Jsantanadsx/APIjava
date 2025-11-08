package br.com.fiap.Bytecode.Test;

import br.com.fiap.Bytecode.DAO.ItemChecklistDAO;
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.ItemChecklist;

// Imports do JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ItemChecklistDAOTest {

    private Connection connection;
    private ItemChecklistDAO itemChecklistDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        this.connection = ConnectionFactory.getConnection();
        this.connection.setAutoCommit(false); // Desliga auto-commit para rollback
        this.itemChecklistDAO = new ItemChecklistDAO(connection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (this.connection != null) {
            this.connection.rollback(); // Reverte todas as operações do teste
            this.connection.close();
        }
    }

    @Test
    void deveInserirItemChecklistComSucesso() throws SQLException {
        // 1. Preparação (Cria o objeto SEM ID)
        ItemChecklist novoItem = new ItemChecklist(
                "Item de Teste",
                "Sugestão de Teste"
        );

        // 2. Ação (Chama o DAO)
        itemChecklistDAO.inserir(novoItem);

        // 3. Verificação (O DAO atualizou o objeto com o ID gerado?)
        assertTrue(novoItem.getIdItemChecklist() > 0, "O ID do item não foi gerado");

        // 4. Verificação extra (O item existe mesmo no banco?)
        ItemChecklist itemDoBanco = itemChecklistDAO.buscarPorId(novoItem.getIdItemChecklist());
        assertNotNull(itemDoBanco, "Item não foi encontrado no banco após inserção");
        assertEquals("Item de Teste", itemDoBanco.getNomeItemChecklist());
    }

    @Test
    void deveBuscarItemChecklistPorId() throws SQLException {
        // 1. Preparação (Insere um item para poder buscar)
        ItemChecklist item = new ItemChecklist("Item Buscar", "Sugestão Buscar");
        itemChecklistDAO.inserir(item);
        int idGerado = item.getIdItemChecklist();

        // 2. Ação
        ItemChecklist itemBuscado = itemChecklistDAO.buscarPorId(idGerado);

        // 3. Verificação
        assertNotNull(itemBuscado);
        assertEquals(idGerado, itemBuscado.getIdItemChecklist());
        assertEquals("Item Buscar", itemBuscado.getNomeItemChecklist());
    }

    @Test
    void deveAtualizarItemChecklist() throws SQLException {
        // 1. Preparação (Insere um item)
        ItemChecklist item = new ItemChecklist("Nome Original", "Sugestão Original");
        itemChecklistDAO.inserir(item);
        int idGerado = item.getIdItemChecklist();

        // 2. Ação (Modifica o objeto e chama o DAO)
        item.setNomeItemChecklist("Nome Atualizado");
        item.setSugestaoChecklist("Sugestão Atualizada");
        itemChecklistDAO.atualizar(item);

        // 3. Verificação (Busca o item do banco e vê se mudou)
        ItemChecklist itemAtualizado = itemChecklistDAO.buscarPorId(idGerado);
        assertNotNull(itemAtualizado);
        assertEquals("Nome Atualizado", itemAtualizado.getNomeItemChecklist());
        assertEquals("Sugestão Atualizada", itemAtualizado.getSugestaoChecklist());
    }

    @Test
    void deveExcluirItemChecklist() throws SQLException {
        // 1. Preparação (Insere um item)
        ItemChecklist item = new ItemChecklist("Item para Excluir", "Sugestão Excluir");
        itemChecklistDAO.inserir(item);
        int idGerado = item.getIdItemChecklist();
        assertNotNull(itemChecklistDAO.buscarPorId(idGerado), "Item não foi inserido antes de excluir");

        // 2. Ação
        itemChecklistDAO.excluir(idGerado);

        // 3. Verificação
        ItemChecklist itemExcluido = itemChecklistDAO.buscarPorId(idGerado);
        assertNull(itemExcluido, "Item não foi excluído corretamente");
    }

    @Test
    void deveListarItensChecklist() throws SQLException {
        // 1. Preparação (Insere alguns itens)
        itemChecklistDAO.inserir(new ItemChecklist("Item A", "Sugestão A"));
        itemChecklistDAO.inserir(new ItemChecklist("Item B", "Sugestão B"));

        // 2. Ação
        List<ItemChecklist> lista = itemChecklistDAO.listar();

        // 3. Verificação
        assertNotNull(lista);
        // Verifica se a lista contém pelo menos os 2 que acabamos de inserir
        assertTrue(lista.size() >= 2);
    }
}