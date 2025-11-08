package br.com.fiap.Bytecode.Test;

import br.com.fiap.Bytecode.DAO.ConsultaDAO;
import br.com.fiap.Bytecode.DAO.LembreteDAO; // O DAO que estamos testando
import br.com.fiap.Bytecode.DAO.PacienteDAO;
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.Consulta;
import br.com.fiap.Bytecode.Model.Lembrete; // O Modelo que estamos testando
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

public class LembreteDAOTest {

    private Connection connection;
    private PacienteDAO pacienteDAO;     // Dependência Nível 2
    private ConsultaDAO consultaDAO;     // Dependência Nível 1
    private LembreteDAO lembreteDAO;     // Alvo do Teste

    // Gera um CPF aleatório e único
    private String gerarCpfUnico() {
        return UUID.randomUUID().toString().substring(0, 11);
    }

    // Método auxiliar para criar uma "Consulta" pai no banco
    private Consulta criarConsultaDeTeste(String status) throws SQLException {
        // 1. Criar Paciente
        Paciente paciente = new Paciente(
                "Paciente Teste Lembrete",
                gerarCpfUnico(),
                LocalDate.of(1990, 1, 1),
                "Masculino"
        );
        pacienteDAO.inserir(paciente);

        // 2. Criar Consulta
        Consulta consulta = new Consulta(
                paciente.getIdPaciente(),
                LocalDateTime.now().plusDays(5).withNano(0), // Consulta no futuro
                "https://meet.google.com/teste-lembrete",
                status // "AGENDADA" ou outro
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
        this.lembreteDAO = new LembreteDAO(connection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (this.connection != null) {
            this.connection.rollback(); // Reverte todas as operações do teste
            this.connection.close();
        }
    }

    @Test
    void deveInserirLembreteComSucesso() throws SQLException {
        // 1. Preparação (Cria a Consulta "pai")
        Consulta consultaPai = criarConsultaDeTeste("AGENDADA");
        int idConsultaPai = consultaPai.getIdConsulta();

        // 2. Preparação (Cria o Lembrete "filho" SEM ID e com envio PENDENTE)
        LocalDateTime dataAgendamento = LocalDateTime.now().plusDays(4).withNano(0);
        Lembrete novoLembrete = new Lembrete(
                idConsultaPai,
                dataAgendamento,
                null, // dataHoraEnvio é nulo, pois está PENDENTE
                "Lembrete de teste",
                "PENDENTE"
        );

        // 3. Ação
        lembreteDAO.inserir(novoLembrete);

        // 4. Verificação (O DAO atualizou o objeto com o ID gerado?)
        assertTrue(novoLembrete.getIdLembrete() > 0, "O ID do lembrete não foi gerado");

        // 5. Verificação extra (O lembrete existe mesmo no banco?)
        Lembrete lembreteDoBanco = lembreteDAO.buscarPorId(novoLembrete.getIdLembrete());
        assertNotNull(lembreteDoBanco, "Lembrete não foi encontrado no banco após inserção");
        assertEquals("PENDENTE", lembreteDoBanco.getStatusEnvio());
        assertEquals(dataAgendamento, lembreteDoBanco.getDataAgendamento());
        assertNull(lembreteDoBanco.getDataHoraEnvio(), "Data de envio deveria ser nula");
    }

    @Test
    void deveBuscarLembretesPorIdConsulta() throws SQLException {
        // 1. Preparação
        Consulta consultaPai = criarConsultaDeTeste("AGENDADA");
        int idConsultaPai = consultaPai.getIdConsulta();

        Lembrete lembrete1 = new Lembrete(idConsultaPai, LocalDateTime.now().plusDays(1).withNano(0), null, "Lembrete 1", "PENDENTE");
        Lembrete lembrete2 = new Lembrete(idConsultaPai, LocalDateTime.now().minusDays(1).withNano(0), LocalDateTime.now().minusDays(1).withNano(0), "Lembrete 2", "ENVIADO");

        lembreteDAO.inserir(lembrete1);
        lembreteDAO.inserir(lembrete2);

        // 2. Ação
        List<Lembrete> lembretes = lembreteDAO.buscarPorIdConsulta(idConsultaPai);

        // 3. Verificação
        assertNotNull(lembretes);
        assertEquals(2, lembretes.size(), "Deveria encontrar 2 lembretes para esta consulta");
    }

    @Test
    void deveAtualizarLembrete() throws SQLException {
        // 1. Preparação
        Consulta consultaPai = criarConsultaDeTeste("AGENDADA");
        Lembrete lembrete = new Lembrete(consultaPai.getIdConsulta(), LocalDateTime.now().plusDays(1).withNano(0), null, "Mensagem Original", "PENDENTE");
        lembreteDAO.inserir(lembrete);
        int idLembreteGerado = lembrete.getIdLembrete();

        // 2. Ação (Simulando o envio do lembrete)
        LocalDateTime dataEnvio = LocalDateTime.now().withNano(0);
        lembrete.setStatusEnvio("ENVIADO");
        lembrete.setDataHoraEnvio(dataEnvio);
        lembrete.setDescricaoMensagem("Mensagem Atualizada");
        lembreteDAO.atualizar(lembrete);

        // 3. Verificação
        Lembrete lembreteAtualizado = lembreteDAO.buscarPorId(idLembreteGerado);
        assertNotNull(lembreteAtualizado);
        assertEquals("ENVIADO", lembreteAtualizado.getStatusEnvio());
        assertEquals("Mensagem Atualizada", lembreteAtualizado.getDescricaoMensagem());
        assertEquals(dataEnvio, lembreteAtualizado.getDataHoraEnvio());
    }

    @Test
    void deveExcluirLembrete() throws SQLException {
        // 1. Preparação
        Consulta consultaPai = criarConsultaDeTeste("AGENDADA");
        Lembrete lembrete = new Lembrete(consultaPai.getIdConsulta(), LocalDateTime.now().plusDays(1).withNano(0), null, "Lembrete para excluir", "PENDENTE");
        lembreteDAO.inserir(lembrete);
        int idLembreteGerado = lembrete.getIdLembrete();
        assertNotNull(lembreteDAO.buscarPorId(idLembreteGerado), "Lembrete não foi inserido antes de excluir");

        // 2. Ação
        lembreteDAO.excluir(idLembreteGerado);

        // 3. Verificação
        Lembrete lembreteExcluido = lembreteDAO.buscarPorId(idLembreteGerado);
        assertNull(lembreteExcluido, "Lembrete não foi excluído corretamente");
    }
}