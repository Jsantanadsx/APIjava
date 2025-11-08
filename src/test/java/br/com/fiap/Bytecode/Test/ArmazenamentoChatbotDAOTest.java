package br.com.fiap.Bytecode.Test;

import br.com.fiap.Bytecode.DAO.ArmazenamentoChatbotDAO; // O DAO que estamos testando
import br.com.fiap.Bytecode.DAO.ChatbotDAO;
import br.com.fiap.Bytecode.DAO.PacienteDAO;
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.ArmazenamentoChatbot; // O Modelo que estamos testando
import br.com.fiap.Bytecode.Model.Chatbot;
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

public class ArmazenamentoChatbotDAOTest {

    private Connection connection;
    private PacienteDAO pacienteDAO;             // Dependência Nível 2
    private ChatbotDAO chatbotDAO;               // Dependência Nível 1
    private ArmazenamentoChatbotDAO armazenamentoDAO; // Alvo do Teste

    // Gera um CPF aleatório e único
    private String gerarCpfUnico() {
        return UUID.randomUUID().toString().substring(0, 11);
    }

    // Método auxiliar para criar um "Chatbot" pai no banco
    private Chatbot criarChatbotDeTeste() throws SQLException {
        // 1. Criar Paciente
        Paciente paciente = new Paciente(
                "Paciente Teste Armaz. Chatbot",
                gerarCpfUnico(),
                LocalDate.of(1990, 1, 1),
                "Outro"
        );
        pacienteDAO.inserir(paciente);
        assertTrue(paciente.getIdPaciente() > 0, "Paciente pai não foi criado");

        // 2. Criar Chatbot
        Chatbot chatbot = new Chatbot(
                paciente.getIdPaciente(),
                "Pergunta de teste para Armazenamento"
        );
        chatbotDAO.inserir(chatbot); // Insere e obtém o ID e a Data

        assertTrue(chatbot.getIdChatbot() > 0, "Chatbot pai não foi criado corretamente");
        return chatbot;
    }

    @BeforeEach
    public void setUp() throws SQLException {
        this.connection = ConnectionFactory.getConnection();
        this.connection.setAutoCommit(false); // Desliga auto-commit para rollback

        // Inicializa todos os DAOs necessários
        this.pacienteDAO = new PacienteDAO(connection);
        this.chatbotDAO = new ChatbotDAO(connection);
        this.armazenamentoDAO = new ArmazenamentoChatbotDAO(connection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (this.connection != null) {
            this.connection.rollback(); // Reverte todas as operações do teste
            this.connection.close();
        }
    }

    @Test
    void deveInserirArmazenamentoComSucesso() throws SQLException {
        // 1. Preparação (Cria o Chatbot "pai")
        Chatbot chatbotPai = criarChatbotDeTeste();
        int idChatbotPai = chatbotPai.getIdChatbot();

        // 2. Preparação (Cria o Armazenamento "filho" SEM ID)
        ArmazenamentoChatbot novoArmazenamento = new ArmazenamentoChatbot(
                idChatbotPai,
                "Esta é a resposta de teste."
        );

        // 3. Ação
        armazenamentoDAO.inserir(novoArmazenamento);

        // 4. Verificação (O DAO atualizou o objeto com o ID gerado?)
        assertTrue(novoArmazenamento.getIdArmazenamentoChatbot() > 0, "O ID do armazenamento não foi gerado");

        // 5. Verificação extra (O armazenamento existe mesmo no banco?)
        ArmazenamentoChatbot armazDoBanco = armazenamentoDAO.buscarPorId(novoArmazenamento.getIdArmazenamentoChatbot());
        assertNotNull(armazDoBanco, "Armazenamento não foi encontrado no banco após inserção");
        assertEquals("Esta é a resposta de teste.", armazDoBanco.getRespostaUsuario());
        assertEquals(idChatbotPai, armazDoBanco.getIdChatbot());
    }

    @Test
    void deveBuscarPorIdChatbot() throws SQLException {
        // 1. Preparação
        Chatbot chatbotPai = criarChatbotDeTeste();
        int idChatbotPai = chatbotPai.getIdChatbot();

        ArmazenamentoChatbot resp1 = new ArmazenamentoChatbot(idChatbotPai, "Resposta 1");
        ArmazenamentoChatbot resp2 = new ArmazenamentoChatbot(idChatbotPai, "Resposta 2");

        armazenamentoDAO.inserir(resp1);
        armazenamentoDAO.inserir(resp2);

        // 2. Ação
        List<ArmazenamentoChatbot> respostas = armazenamentoDAO.buscarPorIdChatbot(idChatbotPai);

        // 3. Verificação
        assertNotNull(respostas);
        assertEquals(2, respostas.size(), "Deveria encontrar 2 respostas para este chatbot");
    }

    @Test
    void deveAtualizarArmazenamento() throws SQLException {
        // 1. Preparação
        Chatbot chatbotPai = criarChatbotDeTeste();
        ArmazenamentoChatbot armazenamento = new ArmazenamentoChatbot(chatbotPai.getIdChatbot(), "Resposta Original");
        armazenamentoDAO.inserir(armazenamento);
        int idGerado = armazenamento.getIdArmazenamentoChatbot();

        // 2. Ação
        armazenamento.setRespostaUsuario("Resposta Atualizada");
        armazenamentoDAO.atualizar(armazenamento);

        // 3. Verificação
        ArmazenamentoChatbot armazAtualizado = armazenamentoDAO.buscarPorId(idGerado);
        assertNotNull(armazAtualizado);
        assertEquals("Resposta Atualizada", armazAtualizado.getRespostaUsuario());
    }

    @Test
    void deveExcluirArmazenamento() throws SQLException {
        // 1. Preparação
        Chatbot chatbotPai = criarChatbotDeTeste();
        ArmazenamentoChatbot armazenamento = new ArmazenamentoChatbot(chatbotPai.getIdChatbot(), "Resposta para excluir");
        armazenamentoDAO.inserir(armazenamento);
        int idGerado = armazenamento.getIdArmazenamentoChatbot();
        assertNotNull(armazenamentoDAO.buscarPorId(idGerado), "Armazenamento não foi inserido antes de excluir");

        // 2. Ação
        armazenamentoDAO.excluir(idGerado);

        // 3. Verificação
        ArmazenamentoChatbot armazExcluido = armazenamentoDAO.buscarPorId(idGerado);
        assertNull(armazExcluido, "Armazenamento não foi excluído corretamente");
    }
}