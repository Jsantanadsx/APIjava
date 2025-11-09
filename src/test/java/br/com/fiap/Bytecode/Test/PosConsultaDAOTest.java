package br.com.fiap.Bytecode.Test;

import br.com.fiap.Bytecode.DAO.ConsultaDAO;
import br.com.fiap.Bytecode.DAO.PacienteDAO;
import br.com.fiap.Bytecode.DAO.PosConsultaDAO; // O DAO que estamos testando
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.Consulta;
import br.com.fiap.Bytecode.Model.Paciente;
import br.com.fiap.Bytecode.Model.PosConsulta; // O Modelo que estamos testando

// Imports do JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID; // Para gerar dados únicos

public class PosConsultaDAOTest {

    private Connection connection;
    private PacienteDAO pacienteDAO;     // Dependência Nível 2
    private ConsultaDAO consultaDAO;     // Dependência Nível 1
    private PosConsultaDAO posConsultaDAO; // Alvo do Teste

    // Gera um CPF aleatório e único
    private String gerarCpfUnico() {
        return UUID.randomUUID().toString().substring(0, 11);
    }

    // Método auxiliar para criar uma "Consulta" pai no banco
    private Consulta criarConsultaDeTeste() throws SQLException {
        // 1. Criar Paciente
        Paciente paciente = new Paciente(
                "Paciente Teste PosConsulta",
                gerarCpfUnico(),
                LocalDate.of(1990, 1, 1),
                "Feminino"
        );
        pacienteDAO.inserir(paciente);

        // 2. Criar Consulta
        Consulta consulta = new Consulta(
                paciente.getIdPaciente(),
                LocalDateTime.now().minusDays(1).withNano(0), // Uma consulta finalizada (no passado)
                "https://meet.google.com/teste-pos",
                "FINALIZADA"
        );
        consultaDAO.inserir(consulta); // Insere e obtém o ID

        assertTrue(consulta.getIdConsulta() > 0, "Consulta pai não foi criada corretamente");
        return consulta;
    }

    @BeforeEach
    public void setUp() throws SQLException {
        this.connection = ConnectionFactory.getConnection();
        this.connection.setAutoCommit(false); // Desliga auto-commit para rollback

        // Inicializa todos os DAOs necessários
        this.pacienteDAO = new PacienteDAO(connection);
        this.consultaDAO = new ConsultaDAO(connection);
        this.posConsultaDAO = new PosConsultaDAO(connection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (this.connection != null) {
            this.connection.rollback(); // Reverte todas as operações do teste
            this.connection.close();
        }
    }

    @Test
    void deveInserirPosConsultaComSucesso() throws SQLException {
        // 1. Preparação (Cria a Consulta "pai")
        Consulta consultaPai = criarConsultaDeTeste();
        int idConsultaPai = consultaPai.getIdConsulta();

        // 2. Preparação (Cria a PosConsulta "filha" SEM ID)
        PosConsulta novaPosConsulta = new PosConsulta(
                idConsultaPai,
                "S", // Comparecimento
                "Diagnóstico de Teste",
                "Receita de Teste"
        );

        // 3. Ação
        posConsultaDAO.inserir(novaPosConsulta);

        // 4. Verificação (O DAO atualizou o objeto com o ID gerado?)
        assertTrue(novaPosConsulta.getIdPosConsulta() > 0, "O ID da PosConsulta não foi gerado");

        // 5. Verificação extra (A PosConsulta existe mesmo no banco?)
        PosConsulta posConsultaDoBanco = posConsultaDAO.buscarPorId(novaPosConsulta.getIdPosConsulta());
        assertNotNull(posConsultaDoBanco, "PosConsulta não foi encontrada no banco após inserção");
        assertEquals("Diagnóstico de Teste", posConsultaDoBanco.getDiagnostico());
        assertEquals(idConsultaPai, posConsultaDoBanco.getIdConsulta());
    }

    @Test
    void deveBuscarPosConsultaPorIdConsulta() throws SQLException {
        // 1. Preparação
        Consulta consultaPai = criarConsultaDeTeste();
        int idConsultaPai = consultaPai.getIdConsulta();

        PosConsulta posConsulta = new PosConsulta(idConsultaPai, "S", "Diagnóstico 2", "Receita 2");
        posConsultaDAO.inserir(posConsulta);
        int idPosConsultaGerado = posConsulta.getIdPosConsulta();

        // 2. Ação
        PosConsulta posConsultaBuscada = posConsultaDAO.buscarPorIdConsulta(idConsultaPai);

        // 3. Verificação
        assertNotNull(posConsultaBuscada);
        assertEquals(idPosConsultaGerado, posConsultaBuscada.getIdPosConsulta());
        assertEquals("Diagnóstico 2", posConsultaBuscada.getDiagnostico());
    }

    @Test
    void deveAtualizarPosConsulta() throws SQLException {
        // 1. Preparação
        Consulta consultaPai = criarConsultaDeTeste();
        PosConsulta posConsulta = new PosConsulta(consultaPai.getIdConsulta(), "S", "Diagnóstico Original", "Receita Original");
        posConsultaDAO.inserir(posConsulta);
        int idPosConsultaGerado = posConsulta.getIdPosConsulta();

        // 2. Ação
        posConsulta.setDiagnostico("Diagnóstico Atualizado");
        posConsulta.setComparecimento("N");
        posConsultaDAO.atualizar(posConsulta);

        // 3. Verificação
        PosConsulta posConsultaAtualizada = posConsultaDAO.buscarPorId(idPosConsultaGerado);
        assertNotNull(posConsultaAtualizada);
        assertEquals("Diagnóstico Atualizado", posConsultaAtualizada.getDiagnostico());
        assertEquals("N", posConsultaAtualizada.getComparecimento());
    }

    @Test
    void deveExcluirPosConsulta() throws SQLException {
        // 1. Preparação
        Consulta consultaPai = criarConsultaDeTeste();
        PosConsulta posConsulta = new PosConsulta(consultaPai.getIdConsulta(), "S", "Diagnóstico Excluir", "Receita Excluir");
        posConsultaDAO.inserir(posConsulta);
        int idPosConsultaGerado = posConsulta.getIdPosConsulta();
        assertNotNull(posConsultaDAO.buscarPorId(idPosConsultaGerado), "PosConsulta não foi inserida antes de excluir");

        // 2. Ação
        posConsultaDAO.excluir(idPosConsultaGerado);

        // 3. Verificação
        PosConsulta posConsultaExcluida = posConsultaDAO.buscarPorId(idPosConsultaGerado);
        assertNull(posConsultaExcluida, "PosConsulta não foi excluída corretamente");
    }
}