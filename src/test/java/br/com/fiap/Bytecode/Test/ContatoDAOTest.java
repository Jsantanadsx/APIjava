package br.com.fiap.Bytecode.Test;

import br.com.fiap.Bytecode.DAO.ContatoDAO;
import br.com.fiap.Bytecode.DAO.PacienteDAO; // Dependência
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.Contato;
import br.com.fiap.Bytecode.Model.Paciente;

// Imports do JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID; // Para gerar dados únicos

public class ContatoDAOTest {

    private Connection connection;
    private ContatoDAO contatoDAO;
    private PacienteDAO pacienteDAO; // Dependência obrigatória

    // Gera um email aleatório e único
    private String gerarEmailUnico() {
        return UUID.randomUUID().toString().substring(0, 10) + "@teste.com";
    }

    // Gera um CPF aleatório e único
    private String gerarCpfUnico() {
        return UUID.randomUUID().toString().substring(0, 11);
    }

    // Método auxiliar para criar um paciente "pai" no banco
    private Paciente criarPacienteDeTeste() throws SQLException {
        Paciente paciente = new Paciente(
                "Paciente Teste Contato",
                gerarCpfUnico(),
                LocalDate.of(1990, 1, 1),
                "Masculino"
        );
        pacienteDAO.inserir(paciente); // Insere e obtém o ID
        return paciente;
    }

    @BeforeEach
    public void setUp() throws SQLException {
        this.connection = ConnectionFactory.getConnection();
        this.connection.setAutoCommit(false); // Desliga auto-commit para rollback

        // Inicializa os dois DAOs
        this.contatoDAO = new ContatoDAO(connection);
        this.pacienteDAO = new PacienteDAO(connection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (this.connection != null) {
            this.connection.rollback(); // Reverte todas as operações do teste
            this.connection.close();
        }
    }

    @Test
    void deveInserirContatoComSucesso() throws SQLException {
        // 1. Preparação (Cria o Paciente "pai")
        Paciente paciente = criarPacienteDeTeste();
        int idPacientePai = paciente.getIdPaciente();
        assertTrue(idPacientePai > 0, "Paciente pai não foi criado corretamente");

        // 2. Preparação (Cria o Contato "filho" SEM ID)
        Contato novoContato = new Contato(
                idPacientePai,
                gerarEmailUnico(),
                "11999998888"
        );

        // 3. Ação
        contatoDAO.inserir(novoContato);

        // 4. Verificação (O DAO atualizou o objeto com o ID gerado?)
        assertTrue(novoContato.getIdContato() > 0, "O ID do contato não foi gerado");

        // 5. Verificação extra (O contato existe mesmo no banco?)
        Contato contatoDoBanco = contatoDAO.buscarPorId(novoContato.getIdContato());
        assertNotNull(contatoDoBanco, "Contato não foi encontrado no banco após inserção");
        assertEquals(novoContato.getEmail(), contatoDoBanco.getEmail());
        assertEquals(idPacientePai, contatoDoBanco.getIdPaciente());
    }

    @Test
    void deveBuscarContatosPorIdPaciente() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        int idPacientePai = paciente.getIdPaciente();

        Contato contato1 = new Contato(idPacientePai, gerarEmailUnico(), "11111111");
        Contato contato2 = new Contato(idPacientePai, gerarEmailUnico(), "22222222");

        contatoDAO.inserir(contato1);
        contatoDAO.inserir(contato2);

        // 2. Ação
        List<Contato> contatos = contatoDAO.buscarPorIdPaciente(idPacientePai);

        // 3. Verificação
        assertNotNull(contatos);
        assertEquals(2, contatos.size(), "Deveria encontrar 2 contatos para este paciente");
    }

    @Test
    void deveAtualizarContato() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        Contato contato = new Contato(paciente.getIdPaciente(), gerarEmailUnico(), "55555555");
        contatoDAO.inserir(contato);
        int idContatoGerado = contato.getIdContato();

        // 2. Ação
        String novoEmail = gerarEmailUnico();
        contato.setEmail(novoEmail);
        contato.setTelefone("66666666");
        contatoDAO.atualizar(contato);

        // 3. Verificação
        Contato contatoAtualizado = contatoDAO.buscarPorId(idContatoGerado);
        assertNotNull(contatoAtualizado);
        assertEquals(novoEmail, contatoAtualizado.getEmail());
        assertEquals("66666666", contatoAtualizado.getTelefone());
    }

    @Test
    void deveExcluirContato() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        Contato contato = new Contato(paciente.getIdPaciente(), gerarEmailUnico(), "77777777");
        contatoDAO.inserir(contato);
        int idContatoGerado = contato.getIdContato();
        assertNotNull(contatoDAO.buscarPorId(idContatoGerado), "Contato não foi inserido antes de excluir");

        // 2. Ação
        contatoDAO.excluir(idContatoGerado);

        // 3. Verificação
        Contato contatoExcluido = contatoDAO.buscarPorId(idContatoGerado);
        assertNull(contatoExcluido, "Contato não foi excluído corretamente");
    }
}