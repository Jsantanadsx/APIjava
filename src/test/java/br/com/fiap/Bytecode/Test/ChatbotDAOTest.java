package br.com.fiap.Bytecode.Test;

import br.com.fiap.Bytecode.DAO.ChatbotDAO; // O DAO que estamos testando
import br.com.fiap.Bytecode.DAO.PacienteDAO; // Dependência
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.Chatbot; // O Modelo que estamos testando
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

public class ChatbotDAOTest {

    private Connection connection;
    private ChatbotDAO chatbotDAO;
    private PacienteDAO pacienteDAO; // Dependência obrigatória

    // Gera um CPF aleatório e único
    private String gerarCpfUnico() {
        return UUID.randomUUID().toString().substring(0, 11);
    }

    // Método auxiliar para criar um paciente "pai" no banco
    private Paciente criarPacienteDeTeste() throws SQLException {
        Paciente paciente = new Paciente(
                "Paciente Teste Chatbot",
                gerarCpfUnico(),
                LocalDate.of(1990, 1, 1),
                "Feminino"
        );
        pacienteDAO.inserir(paciente); // Insere e obtém o ID
        assertTrue(paciente.getIdPaciente() > 0, "Paciente pai não foi criado");
        return paciente;
    }

    @BeforeEach
    public void setUp() throws SQLException {
        this.connection = ConnectionFactory.getConnection();
        this.connection.setAutoCommit(false); // Desliga auto-commit para rollback

        // Inicializa os dois DAOs
        this.chatbotDAO = new ChatbotDAO(connection);
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
    void deveInserirChatbotComSucesso() throws SQLException {
        // 1. Preparação (Cria o Paciente "pai")
        Paciente paciente = criarPacienteDeTeste();
        int idPacientePai = paciente.getIdPaciente();

        // 2. Preparação (Cria o Chatbot "filho" SEM ID e SEM DATA)
        Chatbot novoChatbot = new Chatbot(
                idPacientePai,
                "Isso é uma pergunta de teste?"
        );

        // 3. Ação
        chatbotDAO.inserir(novoChatbot);

        // 4. Verificação (O DAO atualizou o objeto com os IDs e a DATA gerados?)
        assertTrue(novoChatbot.getIdChatbot() > 0, "O ID do chatbot não foi gerado");
        assertNotNull(novoChatbot.getDataPergunta(), "A data da pergunta não foi recuperada");

        // 5. Verificação extra (O chatbot existe mesmo no banco?)
        Chatbot chatbotDoBanco = chatbotDAO.buscarPorId(novoChatbot.getIdChatbot());
        assertNotNull(chatbotDoBanco, "Chatbot não foi encontrado no banco após inserção");
        assertEquals("Isso é uma pergunta de teste?", chatbotDoBanco.getPerguntaUsuario());
        assertEquals(idPacientePai, chatbotDoBanco.getIdPaciente());
    }

    @Test
    void deveBuscarPorIdPaciente() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        int idPacientePai = paciente.getIdPaciente();

        Chatbot pergunta1 = new Chatbot(idPacientePai, "Pergunta 1");
        Chatbot pergunta2 = new Chatbot(idPacientePai, "Pergunta 2");

        chatbotDAO.inserir(pergunta1);
        chatbotDAO.inserir(pergunta2);

        // 2. Ação
        List<Chatbot> perguntas = chatbotDAO.buscarPorIdPaciente(idPacientePai);

        // 3. Verificação
        assertNotNull(perguntas);
        assertEquals(2, perguntas.size(), "Deveria encontrar 2 perguntas para este paciente");
    }

    @Test
    void deveAtualizarChatbot() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        Chatbot chatbot = new Chatbot(paciente.getIdPaciente(), "Pergunta Original");
        chatbotDAO.inserir(chatbot);
        int idChatbotGerado = chatbot.getIdChatbot();

        // 2. Ação
        chatbot.setPerguntaUsuario("Pergunta Atualizada");
        chatbotDAO.atualizar(chatbot);

        // 3. Verificação
        Chatbot chatbotAtualizado = chatbotDAO.buscarPorId(idChatbotGerado);
        assertNotNull(chatbotAtualizado);
        assertEquals("Pergunta Atualizada", chatbotAtualizado.getPerguntaUsuario());
    }

    @Test
    void deveExcluirChatbot() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        Chatbot chatbot = new Chatbot(paciente.getIdPaciente(), "Pergunta para excluir");
        chatbotDAO.inserir(chatbot);
        int idChatbotGerado = chatbot.getIdChatbot();
        assertNotNull(chatbotDAO.buscarPorId(idChatbotGerado), "Chatbot não foi inserido antes de excluir");

        // 2. Ação
        chatbotDAO.excluir(idChatbotGerado);

        // 3. Verificação
        Chatbot chatbotExcluido = chatbotDAO.buscarPorId(idChatbotGerado);
        assertNull(chatbotExcluido, "Chatbot não foi excluído corretamente");
    }
}