package br.com.fiap.Bytecode.Test;

import br.com.fiap.Bytecode.DAO.PacienteDAO;
import br.com.fiap.Bytecode.Factory.ConnectionFactory; // Supondo que você tenha esta classe
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
import java.util.UUID; // Para gerar CPFs únicos para testes

public class PacienteDAOTest {

    private Connection connection;
    private PacienteDAO pacienteDAO;

    // Gera um CPF aleatório e único para cada teste, evitando falha de 'UNIQUE constraint'
    private String gerarCpfUnico() {
        return UUID.randomUUID().toString().substring(0, 11);
    }

    // Conecta ao banco ANTES de cada teste
    @BeforeEach
    public void setUp() throws SQLException {
        this.connection = ConnectionFactory.getConnection();
        // Desligar o auto-commit para poder reverter no final (rollback)
        // Isso impede que os testes "sujem" o banco permanentemente
        this.connection.setAutoCommit(false);
        this.pacienteDAO = new PacienteDAO(connection);
    }

    // Desconecta e reverte o que foi feito DEPOIS de cada teste
    @AfterEach
    public void tearDown() throws SQLException {
        if (this.connection != null) {
            // Reverte qualquer INSERT/UPDATE/DELETE feito durante o teste
            this.connection.rollback();
            this.connection.close();
        }
    }

    @Test
    void deveInserirPacienteComSucesso() throws SQLException {
        // 1. Preparação (Cria o objeto SEM ID)
        Paciente novoPaciente = new Paciente(
                "Paciente Teste Inserir",
                gerarCpfUnico(),
                LocalDate.of(1990, 1, 1),
                "Masculino"
        );

        // 2. Ação (Chama o DAO)
        pacienteDAO.inserir(novoPaciente);

        // 3. Verificação (O DAO atualizou o objeto com o ID gerado?)
        assertTrue(novoPaciente.getIdPaciente() > 0, "O ID do paciente não foi gerado");

        // 4. Verificação extra (O paciente existe mesmo no banco?)
        Paciente pacienteDoBanco = pacienteDAO.buscarPorId(novoPaciente.getIdPaciente());
        assertNotNull(pacienteDoBanco, "Paciente não foi encontrado no banco após inserção");
        assertEquals("Paciente Teste Inserir", pacienteDoBanco.getNome());
    }

    @Test
    void deveBuscarPacientePorId() throws SQLException {
        // 1. Preparação (Insere um paciente para poder buscar)
        Paciente paciente = new Paciente("Paciente Buscar", gerarCpfUnico(), LocalDate.of(1985, 5, 10), "Feminino");
        pacienteDAO.inserir(paciente);
        int idGerado = paciente.getIdPaciente();

        // 2. Ação
        Paciente pacienteBuscado = pacienteDAO.buscarPorId(idGerado);

        // 3. Verificação
        assertNotNull(pacienteBuscado);
        assertEquals(idGerado, pacienteBuscado.getIdPaciente());
        assertEquals("Paciente Buscar", pacienteBuscado.getNome());
    }

    @Test
    void deveAtualizarPaciente() throws SQLException {
        // 1. Preparação (Insere um paciente)
        Paciente paciente = new Paciente("Paciente Original", gerarCpfUnico(), LocalDate.of(2000, 3, 15), "Outro");
        pacienteDAO.inserir(paciente);
        int idGerado = paciente.getIdPaciente();

        // 2. Ação (Modifica o objeto e chama o DAO)
        paciente.setNome("Paciente Nome Atualizado");
        paciente.setGenero("Feminino");
        pacienteDAO.atualizar(paciente);

        // 3. Verificação (Busca o paciente do banco e vê se mudou)
        Paciente pacienteAtualizado = pacienteDAO.buscarPorId(idGerado);
        assertNotNull(pacienteAtualizado);
        assertEquals("Paciente Nome Atualizado", pacienteAtualizado.getNome());
        assertEquals("Feminino", pacienteAtualizado.getGenero());
    }

    @Test
    void deveExcluirPaciente() throws SQLException {
        // 1. Preparação (Insere um paciente)
        Paciente paciente = new Paciente("Paciente a Excluir", gerarCpfUnico(), LocalDate.of(1995, 12, 25), "Masculino");
        pacienteDAO.inserir(paciente);
        int idGerado = paciente.getIdPaciente();
        assertNotNull(pacienteDAO.buscarPorId(idGerado), "Paciente não foi inserido antes de excluir");

        // 2. Ação
        pacienteDAO.excluir(idGerado);

        // 3. Verificação
        Paciente pacienteExcluido = pacienteDAO.buscarPorId(idGerado);
        assertNull(pacienteExcluido, "Paciente não foi excluído corretamente");
    }

    @Test
    void deveListarPacientes() throws SQLException {
        // 1. Preparação (Insere alguns pacientes)
        pacienteDAO.inserir(new Paciente("Paciente Lista A", gerarCpfUnico(), LocalDate.of(1991, 1, 1), "Feminino"));
        pacienteDAO.inserir(new Paciente("Paciente Lista B", gerarCpfUnico(), LocalDate.of(1992, 2, 2), "Masculino"));

        // 2. Ação
        List<Paciente> lista = pacienteDAO.listar();

        // 3. Verificação
        assertNotNull(lista);
        // Verifica se a lista contém pelo menos os 2 que acabamos de inserir
        assertTrue(lista.size() >= 2);
    }
}