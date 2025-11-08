package br.com.fiap.Bytecode.Test;

import br.com.fiap.Bytecode.DAO.ConsultaDAO;
import br.com.fiap.Bytecode.DAO.PacienteDAO;
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.Consulta;
import br.com.fiap.Bytecode.Model.Paciente;
import br.com.fiap.Bytecode.Service.ConsultaService;

// Imports do JUnit 5
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ConsultaServiceTest {

    private Connection connection;
    private ConsultaService consultaService;

    //DAOs para configurar o teste e verificar o resultado
    private PacienteDAO pacienteDAO;
    private ConsultaDAO consultaDAO;

    // Gera um CPF aleatório e único
    private String gerarCpfUnico() {
        return UUID.randomUUID().toString().substring(0, 11);
    }

    // Conecta ao banco ANTES de cada teste
    @BeforeEach
    public void setUp() throws SQLException {
        this.connection = ConnectionFactory.getConnection();
        this.connection.setAutoCommit(false); // Ligar modo de transação

        // Inicializa o Serviço com a conexão real
        this.consultaService = new ConsultaService(connection);

        // Inicializa os DAOs que vamos usar para preparar e verificar o teste
        this.pacienteDAO = new PacienteDAO(connection);
        this.consultaDAO = new ConsultaDAO(connection);
    }

    // Desconecta e dá rollback DEPOIS de cada teste
    @AfterEach
    public void tearDown() throws SQLException {
        if (this.connection != null) {
            this.connection.rollback(); // Desfaz qualquer INSERT/UPDATE
            this.connection.close();
        }
    }

    // --- Teste da Regra 1: Não agendar no passado ---
    @Test
    void deveLancarExcecaoAoAgendarNoPassado() {
        // 1. Preparação
        LocalDateTime dataPassada = LocalDateTime.now().minusDays(1);

        // 2. Ação e Verificação
        Exception exception = assertThrows(Exception.class, () -> {
            // Tenta agendar para um paciente qualquer (ID 1), mas numa data passada
            consultaService.agendarConsulta(1, dataPassada);
        });

        assertEquals("Não é possível agendar consultas em datas passadas.", exception.getMessage());
    }

    // --- Teste da Regra 2: Paciente deve existir ---
    @Test
    void deveLancarExcecaoAoAgendarParaPacienteInexistente() {
        // 1. Preparação
        LocalDateTime dataFutura = LocalDateTime.now().plusDays(1);
        int idPacienteInexistente = 9999; // Um ID que sabemos que não existe

        // 2. Ação e Verificação
        Exception exception = assertThrows(Exception.class, () -> {
            // Tenta agendar para um paciente que não existe
            consultaService.agendarConsulta(idPacienteInexistente, dataFutura);
        });

        assertEquals("Paciente não encontrado.", exception.getMessage());
    }

    // --- Teste de Sucesso ---
    @Test
    void deveAgendarConsultaComSucesso() throws Exception {
        // 1. Preparação (CRIAR UM PACIENTE REAL no banco)
        Paciente pacienteValido = new Paciente("Paciente Teste Service", gerarCpfUnico(), LocalDate.now(), "M");
        pacienteDAO.inserir(pacienteValido); // Salva no banco (mas sofrerá rollback depois)

        int idPacienteValido = pacienteValido.getIdPaciente();
        assertTrue(idPacienteValido > 0); // Confirma que o paciente foi criado

        LocalDateTime dataFutura = LocalDateTime.now().plusDays(1).withNano(0);

        // 2. Ação (Chamar o serviço)
        Consulta consultaAgendada = consultaService.agendarConsulta(idPacienteValido, dataFutura);

        // 3. Verificação (O serviço retornou o objeto correto?)
        assertNotNull(consultaAgendada);
        assertTrue(consultaAgendada.getIdConsulta() > 0);
        assertEquals("AGENDADA", consultaAgendada.getStatusConsulta());
        assertEquals(idPacienteValido, consultaAgendada.getIdPaciente());

        // 4. Verificação final (O objeto foi REALMENTE salvo no banco?)
        Consulta consultaDoBanco = consultaDAO.buscarPorId(consultaAgendada.getIdConsulta());
        assertNotNull(consultaDoBanco);
        assertEquals(idPacienteValido, consultaDoBanco.getIdPaciente());
    }
}