package br.com.fiap.Bytecode.Service;

import br.com.fiap.Bytecode.DAO.LoginDAO;
import br.com.fiap.Bytecode.DAO.PacienteDAO;
import br.com.fiap.Bytecode.Model.Login;
import br.com.fiap.Bytecode.Model.Paciente;

import java.sql.Connection;
import java.sql.SQLException;

public class PacienteService {

    private PacienteDAO pacienteDAO;
    private LoginDAO loginDAO;
    private Connection connection;

    public PacienteService(Connection connection) {
        this.connection = connection;
        this.pacienteDAO = new PacienteDAO(connection);
        this.loginDAO = new LoginDAO(connection);
    }

    /**
     * Regra de Negócio: Registra um novo paciente e seu login em uma
     * transação única. Se o login falhar, o paciente também é desfeito.
     */
    public Paciente registrarNovoPaciente(Paciente paciente, Login login) throws Exception {

        try {
            // 1. Inicia o controle manual da transação
            connection.setAutoCommit(false);

            // 2. Regra de Negócio: O email já está em uso?
            if (loginDAO.buscarPorEmail(login.getEmail()) != null) {
                throw new Exception("O email '" + login.getEmail() + "' já está em uso.");
            }

            // 3. Regra de Negócio: O CPF já está em uso?
            // (Você precisaria adicionar um método "buscarPorCpf" no PacienteDAO)
            // if (pacienteDAO.buscarPorCpf(paciente.getCpf()) != null) {
            //     throw new Exception("O CPF '" + paciente.getCpf() + "' já está em uso.");
            // }

            // 4. Ação: Insere o "Pai" (Paciente)
            pacienteDAO.inserir(paciente);

            // 5. Ação: Pega o ID gerado e o usa para o "Filho" (Login)
            int idPacienteNovo = paciente.getIdPaciente();
            if (idPacienteNovo <= 0) {
                throw new Exception("Falha ao obter o ID do novo paciente.");
            }
            login.setIdPaciente(idPacienteNovo);

            // 6. Ação: Insere o "Filho" (Login)
            loginDAO.inserir(login);

            // 7. Sucesso: Se tudo deu certo, confirma as mudanças no banco
            connection.commit();

            return paciente;

        } catch (Exception e) {
            // 8. Falha: Desfaz qualquer mudança (o INSERT do paciente)
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Erro ao tentar reverter o rollback: " + rollbackEx.getMessage());
            }
            // Lança a exceção original (ex: "Email já em uso")
            throw e;

        } finally {
            // 9. Limpeza: Devolve a conexão ao estado padrão (auto-commit ligado)
            try {
                connection.setAutoCommit(true);
            } catch (SQLException finalEx) {
                System.err.println("Erro ao resetar auto-commit: " + finalEx.getMessage());
            }
        }
    }
}