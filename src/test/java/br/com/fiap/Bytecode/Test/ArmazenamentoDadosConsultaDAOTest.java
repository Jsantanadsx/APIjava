package br.com.fiap.Bytecode.Test;

import br.com.fiap.Bytecode.DAO.ArmazenamentoDadosConsultaDAO; // O DAO que estamos testando
import br.com.fiap.Bytecode.DAO.ConsultaDAO;
import br.com.fiap.Bytecode.DAO.PacienteDAO;
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.ArmazenamentoDadosConsulta; // O Modelo que estamos testando
import br.com.fiap.Bytecode.Model.Consulta;
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

public class ArmazenamentoDadosConsultaDAOTest {

    private Connection connection;
    private PacienteDAO pacienteDAO;             // Dependência Nível 2
    private ConsultaDAO consultaDAO;             // Dependência Nível 1
    private ArmazenamentoDadosConsultaDAO armazenamentoDAO; // Alvo do Teste

    // Gera um CPF aleatório e único
    private String gerarCpfUnico() {
        return UUID.randomUUID().toString().substring(0, 11);
    }

    // Método auxiliar para criar uma "Consulta" pai no banco
    private Consulta criarConsultaDeTeste() throws SQLException {
        // 1. Criar Paciente
        Paciente paciente = new Paciente(
                "Paciente Teste Armaz. Dados",
                gerarCpfUnico(),
                LocalDate.of(1990, 1, 1),
                "Masculino"
        );
        pacienteDAO.inserir(paciente);
        assertTrue(paciente.getIdPaciente() > 0, "Paciente pai não foi criado");

        // 2. Criar Consulta
        Consulta consulta = new Consulta(
                paciente.getIdPaciente(),
                LocalDateTime.now().plusHours(1).withNano(0),
                "https://meet.google.com/teste-armazenamento",
                "AGENDADA"
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
        this.armazenamentoDAO = new ArmazenamentoDadosConsultaDAO(connection);
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
        // 1. Preparação (Cria a Consulta "pai")
        Consulta consultaPai = criarConsultaDeTeste();
        int idConsultaPai = consultaPai.getIdConsulta();

        // 2. Preparação (Cria o Armazenamento "filho" SEM ID)
        ArmazenamentoDadosConsulta novoArmazenamento = new ArmazenamentoDadosConsulta(
                idConsultaPai,
                "Anotação de teste."
        );

        // 3. Ação
        armazenamentoDAO.inserir(novoArmazenamento);

        // 4. Verificação (O DAO atualizou o objeto com o ID gerado?)
        assertTrue(novoArmazenamento.getIdArmazenamento() > 0, "O ID do armazenamento não foi gerado");

        // 5. Verificação extra (O armazenamento existe mesmo no banco?)
        ArmazenamentoDadosConsulta armazDoBanco = armazenamentoDAO.buscarPorId(novoArmazenamento.getIdArmazenamento());
        assertNotNull(armazDoBanco, "Armazenamento não foi encontrado no banco após inserção");
        assertEquals("Anotação de teste.", armazDoBanco.getDadoArmazenado());
        assertEquals(idConsultaPai, armazDoBanco.getIdConsulta());
    }

    @Test
    void deveBuscarPorIdConsulta() throws SQLException {
        // 1. Preparação
        Consulta consultaPai = criarConsultaDeTeste();
        int idConsultaPai = consultaPai.getIdConsulta();

        ArmazenamentoDadosConsulta dado1 = new ArmazenamentoDadosConsulta(idConsultaPai, "Dado 1");
        ArmazenamentoDadosConsulta dado2 = new ArmazenamentoDadosConsulta(idConsultaPai, "Dado 2");

        armazenamentoDAO.inserir(dado1);
        armazenamentoDAO.inserir(dado2);

        // 2. Ação
        List<ArmazenamentoDadosConsulta> dados = armazenamentoDAO.buscarPorIdConsulta(idConsultaPai);

        // 3. Verificação
        assertNotNull(dados);
        assertEquals(2, dados.size(), "Deveria encontrar 2 dados para esta consulta");
    }

    @Test
    void deveAtualizarArmazenamento() throws SQLException {
        // 1. Preparação
        Consulta consultaPai = criarConsultaDeTeste();
        ArmazenamentoDadosConsulta armazenamento = new ArmazenamentoDadosConsulta(consultaPai.getIdConsulta(), "Dado Original");
        armazenamentoDAO.inserir(armazenamento);
        int idGerado = armazenamento.getIdArmazenamento();

        // 2. Ação
        armazenamento.setDadoArmazenado("Dado Atualizado");
        armazenamentoDAO.atualizar(armazenamento);

        // 3. Verificação
        ArmazenamentoDadosConsulta armazAtualizado = armazenamentoDAO.buscarPorId(idGerado);
        assertNotNull(armazAtualizado);
        assertEquals("Dado Atualizado", armazAtualizado.getDadoArmazenado());
    }

    @Test
    void deveExcluirArmazenamento() throws SQLException {
        // 1. Preparação
        Consulta consultaPai = criarConsultaDeTeste();
        ArmazenamentoDadosConsulta armazenamento = new ArmazenamentoDadosConsulta(consultaPai.getIdConsulta(), "Dado para excluir");
        armazenamentoDAO.inserir(armazenamento);
        int idGerado = armazenamento.getIdArmazenamento();
        assertNotNull(armazenamentoDAO.buscarPorId(idGerado), "Armazenamento não foi inserido antes de excluir");

        // 2. Ação
        armazenamentoDAO.excluir(idGerado);

        // 3. Verificação
        ArmazenamentoDadosConsulta armazExcluido = armazenamentoDAO.buscarPorId(idGerado);
        assertNull(armazExcluido, "Armazenamento não foi excluído corretamente");
    }
}