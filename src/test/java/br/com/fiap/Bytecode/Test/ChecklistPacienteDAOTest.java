package br.com.fiap.Bytecode.Test;

import br.com.fiap.Bytecode.DAO.ChecklistPacienteDAO; // O DAO que estamos testando
import br.com.fiap.Bytecode.DAO.ConsultaDAO;
import br.com.fiap.Bytecode.DAO.ItemChecklistDAO;
import br.com.fiap.Bytecode.DAO.PacienteDAO;
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.ChecklistPaciente; // O Modelo que estamos testando
import br.com.fiap.Bytecode.Model.Consulta;
import br.com.fiap.Bytecode.Model.ItemChecklist;
import br.com.fiap.Bytecode.Model.Paciente;

// Imports do JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID; // Para gerar dados únicos

public class ChecklistPacienteDAOTest {

    private Connection connection;
    private PacienteDAO pacienteDAO;
    private ConsultaDAO consultaDAO;
    private ItemChecklistDAO itemChecklistDAO;
    private ChecklistPacienteDAO checklistPacienteDAO; // Alvo do Teste

    // --- Métodos Auxiliares para criar dependências ---

    // Gera um CPF aleatório e único
    private String gerarCpfUnico() {
        return UUID.randomUUID().toString().substring(0, 11);
    }

    // Cria uma Consulta (que por sua vez cria um Paciente)
    private Consulta criarConsultaDeTeste() throws SQLException {
        Paciente paciente = new Paciente(
                "Paciente Teste Checklist",
                gerarCpfUnico(),
                LocalDate.of(1990, 1, 1),
                "Outro"
        );
        pacienteDAO.inserir(paciente);

        Consulta consulta = new Consulta(
                paciente.getIdPaciente(),
                LocalDateTime.now().plusDays(1).withNano(0),
                "https://meet.google.com/teste-checklist",
                "AGENDADA"
        );
        consultaDAO.inserir(consulta);
        assertTrue(consulta.getIdConsulta() > 0, "Consulta pai não foi criada");
        return consulta;
    }

    // Cria um ItemChecklist
    private ItemChecklist criarItemChecklistDeTeste(String nome) throws SQLException {
        ItemChecklist item = new ItemChecklist(nome, "Sugestão para " + nome);
        itemChecklistDAO.inserir(item);
        assertTrue(item.getIdItemChecklist() > 0, "ItemChecklist pai não foi criado");
        return item;
    }

    // --- Fim dos Métodos Auxiliares ---

    @BeforeEach
    public void setUp() throws SQLException {
        this.connection = ConnectionFactory.getConnection();
        this.connection.setAutoCommit(false); // Desliga auto-commit para rollback

        // Inicializa todos os DAOs necessários
        this.pacienteDAO = new PacienteDAO(connection);
        this.consultaDAO = new ConsultaDAO(connection);
        this.itemChecklistDAO = new ItemChecklistDAO(connection);
        this.checklistPacienteDAO = new ChecklistPacienteDAO(connection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (this.connection != null) {
            this.connection.rollback(); // Reverte todas as operações do teste
            this.connection.close();
        }
    }

    @Test
    void deveInserirChecklistPacienteComSucesso() throws SQLException {
        // 1. Preparação (Criar os "pais")
        Consulta consulta = criarConsultaDeTeste();
        ItemChecklist item = criarItemChecklistDeTeste("Item Teste Inserir");

        // 2. Preparação (Criar o objeto de associação)
        ChecklistPaciente novoChecklist = new ChecklistPaciente(
                consulta.getIdConsulta(),
                item.getIdItemChecklist(),
                "PENDENTE"
        );

        // 3. Ação
        checklistPacienteDAO.inserir(novoChecklist);

        // 4. Verificação (Buscar pela PK composta)
        ChecklistPaciente checklistDoBanco = checklistPacienteDAO.buscarPorId(
                consulta.getIdConsulta(),
                item.getIdItemChecklist()
        );

        assertNotNull(checklistDoBanco, "Associação não foi encontrada no banco");
        assertEquals("PENDENTE", checklistDoBanco.getStatusItem());
    }

    @Test
    void deveBuscarPorIdConsulta() throws SQLException {
        // 1. Preparação
        Consulta consulta = criarConsultaDeTeste(); // Apenas 1 consulta
        ItemChecklist item1 = criarItemChecklistDeTeste("Item 1");
        ItemChecklist item2 = criarItemChecklistDeTeste("Item 2");

        ChecklistPaciente checklist1 = new ChecklistPaciente(consulta.getIdConsulta(), item1.getIdItemChecklist(), "PENDENTE");
        ChecklistPaciente checklist2 = new ChecklistPaciente(consulta.getIdConsulta(), item2.getIdItemChecklist(), "CONCLUIDO");

        checklistPacienteDAO.inserir(checklist1);
        checklistPacienteDAO.inserir(checklist2);

        // 2. Ação
        List<ChecklistPaciente> lista = checklistPacienteDAO.buscarPorIdConsulta(consulta.getIdConsulta());

        // 3. Verificação
        assertNotNull(lista);
        assertEquals(2, lista.size(), "Deveria encontrar 2 itens para esta consulta");
    }

    @Test
    void deveAtualizarStatus() throws SQLException {
        // 1. Preparação
        Consulta consulta = criarConsultaDeTeste();
        ItemChecklist item = criarItemChecklistDeTeste("Item Teste Atualizar");

        ChecklistPaciente checklist = new ChecklistPaciente(consulta.getIdConsulta(), item.getIdItemChecklist(), "PENDENTE");
        checklistPacienteDAO.inserir(checklist);

        // 2. Ação
        checklist.setStatusItem("CONCLUIDO");
        checklistPacienteDAO.atualizarStatus(checklist);

        // 3. Verificação
        ChecklistPaciente checklistAtualizado = checklistPacienteDAO.buscarPorId(
                consulta.getIdConsulta(),
                item.getIdItemChecklist()
        );

        assertNotNull(checklistAtualizado);
        assertEquals("CONCLUIDO", checklistAtualizado.getStatusItem());
    }

    @Test
    void deveExcluir() throws SQLException {
        // 1. Preparação
        Consulta consulta = criarConsultaDeTeste();
        ItemChecklist item = criarItemChecklistDeTeste("Item Teste Excluir");

        ChecklistPaciente checklist = new ChecklistPaciente(consulta.getIdConsulta(), item.getIdItemChecklist(), "PENDENTE");
        checklistPacienteDAO.inserir(checklist);

        // Verifica se inseriu
        assertNotNull(checklistPacienteDAO.buscarPorId(consulta.getIdConsulta(), item.getIdItemChecklist()));

        // 2. Ação
        checklistPacienteDAO.excluir(consulta.getIdConsulta(), item.getIdItemChecklist());

        // 3. Verificação
        ChecklistPaciente checklistExcluido = checklistPacienteDAO.buscarPorId(
                consulta.getIdConsulta(),
                item.getIdItemChecklist()
        );

        assertNull(checklistExcluido, "A associação não foi excluída corretamente");
    }
}