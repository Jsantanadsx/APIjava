package br.com.fiap.Bytecode.Service;

import br.com.fiap.Bytecode.DAO.LoginDAO;
import br.com.fiap.Bytecode.DAO.PacienteDAO;
import br.com.fiap.Bytecode.Model.Login;
import br.com.fiap.Bytecode.Model.Paciente;

import java.sql.Connection;

public class LoginService {

    private LoginDAO loginDAO;
    private PacienteDAO pacienteDAO;

    public LoginService(Connection connection) {
        this.loginDAO = new LoginDAO(connection);
        this.pacienteDAO = new PacienteDAO(connection);
    }

    /**
     * Regra de Negócio: Autentica um usuário e retorna o Paciente completo.
     */
    public Paciente autenticar(String email, String senha) throws Exception {

        // 1. Regra: O login existe?
        Login login = loginDAO.buscarPorEmail(email);
        if (login == null) {
            throw new Exception("Usuário ou senha inválidos.");
        }

        // 2. Regra: A senha está correta?
        if (!login.getSenha().equals(senha)) {
            throw new Exception("Usuário ou senha inválidos.");
        }

        // 3. Sucesso: Busca e retorna o Paciente "pai"
        Paciente paciente = pacienteDAO.buscarPorId(login.getIdPaciente());
        if (paciente == null) {
            throw new Exception("Erro interno: Login encontrado, mas paciente associado não existe.");
        }

        return paciente;
    }
}