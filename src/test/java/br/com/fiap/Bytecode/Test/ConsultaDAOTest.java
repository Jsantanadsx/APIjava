package br.com.fiap.Bytecode.Test;

import br.com.fiap.Bytecode.DAO.ConsultaDAO;
import br.com.fiap.Bytecode.DAO.PacienteDAO; // Dependência
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
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

public class ConsultaDAOTest {

    private Connection connection;
    private ConsultaDAO consultaDAO;
    private PacienteDAO pacienteDAO; // Dependência obrigatória

    // Gera um CPF aleatório e único
    private String gerarCpfUnico() {
        return UUID.randomUUID().toString().substring(0, 11);
    }

    // Método auxiliar para criar um paciente "pai" no banco
    private Paciente criarPacienteDeTeste() throws SQLException {
        Paciente paciente = new Paciente(
                "Paciente Teste Consulta",
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
        this.consultaDAO = new ConsultaDAO(connection);
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
    void deveInserirConsultaComSucesso() throws SQLException {
        // 1. Preparação (Cria o Paciente "pai")
        Paciente paciente = criarPacienteDeTeste();
        int idPacientePai = paciente.getIdPaciente();
        assertTrue(idPacientePai > 0, "Paciente pai não foi criado corretamente");

        // 2. Preparação (Cria a Consulta "filha" SEM ID)
        // Usamos .withNano(0) para remover nanossegundos, garantindo compatibilidade
        LocalDateTime data = LocalDateTime.now().plusDays(1).withNano(0);
        Consulta novaConsulta = new Consulta(
                idPacientePai,
                data,
                "https://meet.google.com/teste",
                "AGENDADA"
        );

        // 3. Ação
        consultaDAO.inserir(novaConsulta);

        // 4. Verificação (O DAO atualizou o objeto com o ID gerado?)
        assertTrue(novaConsulta.getIdConsulta() > 0, "O ID da consulta não foi gerado");

        // 5. Verificação extra (A consulta existe mesmo no banco?)
        Consulta consultaDoBanco = consultaDAO.buscarPorId(novaConsulta.getIdConsulta());
        assertNotNull(consultaDoBanco, "Consulta não foi encontrada no banco após inserção");
        assertEquals("AGENDADA", consultaDoBanco.getStatusConsulta());
        assertEquals(data, consultaDoBanco.getDataHoraConsulta());
    }

    @Test
    void deveBuscarConsultasPorIdPaciente() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        int idPacientePai = paciente.getIdPaciente();

        LocalDateTime data1 = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime data2 = LocalDateTime.now().plusDays(2).withNano(0);

        Consulta consulta1 = new Consulta(idPacientePai, data1, "link1", "AGENDADA");
        Consulta consulta2 = new Consulta(idPacientePai, data2, "link2", "FINALIZADA");

        consultaDAO.inserir(consulta1);
        consultaDAO.inserir(consulta2);

        // 2. Ação
        List<Consulta> consultas = consultaDAO.buscarPorIdPaciente(idPacientePai);

        // 3. Verificação
        assertNotNull(consultas);
        assertEquals(2, consultas.size(), "Deveria encontrar 2 consultas para este paciente");
    }

    @Test
    void deveAtualizarConsulta() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        LocalDateTime data = LocalDateTime.now().plusDays(5).withNano(0);
        Consulta consulta = new Consulta(paciente.getIdPaciente(), data, "link-antigo", "AGENDADA");
        consultaDAO.inserir(consulta);
        int idConsultaGerada = consulta.getIdConsulta();

        // 2. Ação
        consulta.setStatusConsulta("FINALIZADA");
        consulta.setLinkConsulta("link-novo");
        consultaDAO.atualizar(consulta);

        // 3. Verificação
        Consulta consultaAtualizada = consultaDAO.buscarPorId(idConsultaGerada);
        assertNotNull(consultaAtualizada);
        assertEquals("FINALIZADA", consultaAtualizada.getStatusConsulta());
        assertEquals("link-novo", consultaAtualizada.getLinkConsulta());
    }

    @Test
    void deveExcluirConsulta() throws SQLException {
        // 1. Preparação
        Paciente paciente = criarPacienteDeTeste();
        Consulta consulta = new Consulta(paciente.getIdPaciente(), LocalDateTime.now().plusDays(1).withNano(0), "link", "CANCELADA");
        consultaDAO.inserir(consulta);
        int idConsultaGerada = consulta.getIdConsulta();
        assertNotNull(consultaDAO.buscarPorId(idConsultaGerada), "Consulta não foi inserida antes de excluir");

        // 2. Ação
        consultaDAO.excluir(idConsultaGerada);

        // 3. Verificação
        Consulta consultaExcluida = consultaDAO.buscarPorId(idConsultaGerada);
        assertNull(consultaExcluida, "Consulta não foi excluída corretamente");
    }
}