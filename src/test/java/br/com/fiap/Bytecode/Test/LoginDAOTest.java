package br.com.fiap.Bytecode.Test;

import br.com.fiap.Bytecode.DAO.LoginDAO;
import br.com.fiap.Bytecode.DAO.PacienteDAO; // Necessário para criar o "pai"
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.Login;
import br.com.fiap.Bytecode.Model.Paciente;

// Imports do JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID; // Para gerar dados únicos

public class LoginDAOTest {

    private Connection connection;
    private LoginDAO loginDAO;
    private PacienteDAO pacienteDAO; // Dependência obrigatória

    // Gera um email aleatório e único para cada teste
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
                "Paciente Teste Login",
                gerarCpfUnico(),
                LocalDate.of(1990, 1, 1),
                "Feminino"
        );
        pacienteDAO.inserir(paciente); // Insere e obtém o ID
        return paciente;
    }

    @BeforeEach
    public void setUp() throws SQLException {
        this.connection = ConnectionFactory.getConnection();
        this.connection.setAutoCommit(false); // Desliga auto-commit para rollback

        // Inicializa os dois DAOs
        this.loginDAO = new LoginDAO(connection);
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
    void deveInserirLoginComSucesso() throws SQLException {
        // 1. Preparação (Cria o Paciente "pai")
        Paciente paciente = criarPacienteDeTeste();
        int idPacientePai = paciente.getIdPaciente();
        assertTrue(idPacientePai > 0, "Paciente pai não foi criado corretamente");

        // 2. Preparação (Cria o Login "filho" SEM ID)
        Login novoLogin = new Login(
                idPacientePai,
                gerarEmailUnico(),
                "senha123"
        );

        // 3. Ação
        loginDAO.inserir(novoLogin);

        // 4. Verificação (O DAO atualizou o objeto com o ID gerado?)
        assertTrue(novoLogin.getIdLogin() > 0, "O ID do login não foi gerado");

        // 5. Verificação extra (O login existe mesmo no banco?)
        Login loginDoBanco = loginDAO.buscarPorId(novoLogin.getIdLogin());
        assertNotNull(loginDoBanco, "Login não foi encontrado no banco após inserção");
        assertEquals(novoLogin.getEmail(), loginDoBanco.getEmail());
        assertEquals(idPacientePai, loginDoBanco.getIdPaciente());
    }

    @Test
    void deveBuscarLoginPorEmail() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        String emailUnico = gerarEmailUnico();
        Login login = new Login(paciente.getIdPaciente(), emailUnico, "senha123");
        loginDAO.inserir(login);

        // 2. Ação
        Login loginBuscado = loginDAO.buscarPorEmail(emailUnico);

        // 3. Verificação
        assertNotNull(loginBuscado);
        assertEquals(login.getIdLogin(), loginBuscado.getIdLogin());
        assertEquals(emailUnico, loginBuscado.getEmail());
    }

    @Test
    void deveBuscarLoginPorIdPaciente() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        int idPacientePai = paciente.getIdPaciente();
        Login login = new Login(idPacientePai, gerarEmailUnico(), "senha123");
        loginDAO.inserir(login);

        // 2. Ação
        Login loginBuscado = loginDAO.buscarPorIdPaciente(idPacientePai);

        // 3. Verificação
        assertNotNull(loginBuscado);
        assertEquals(login.getIdLogin(), loginBuscado.getIdLogin());
    }

    @Test
    void deveAtualizarLogin() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        String emailOriginal = gerarEmailUnico();
        Login login = new Login(paciente.getIdPaciente(), emailOriginal, "senhaAntiga");
        loginDAO.inserir(login);
        int idLoginGerado = login.getIdLogin();

        // 2. Ação
        String novoEmail = gerarEmailUnico();
        login.setEmail(novoEmail);
        login.setSenha("senhaNova");
        loginDAO.atualizar(login);

        // 3. Verificação
        Login loginAtualizado = loginDAO.buscarPorId(idLoginGerado);
        assertNotNull(loginAtualizado);
        assertEquals(novoEmail, loginAtualizado.getEmail());
        assertEquals("senhaNova", loginAtualizado.getSenha());
    }

    @Test
    void deveExcluirLogin() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        Login login = new Login(paciente.getIdPaciente(), gerarEmailUnico(), "senha123");
        loginDAO.inserir(login);
        int idLoginGerado = login.getIdLogin();
        assertNotNull(loginDAO.buscarPorId(idLoginGerado), "Login não foi inserido antes de excluir");

        // 2. Ação
        loginDAO.excluir(idLoginGerado);

        // 3. Verificação
        Login loginExcluido = loginDAO.buscarPorId(idLoginGerado);
        assertNull(loginExcluido, "Login não foi excluído corretamente");
    }
}