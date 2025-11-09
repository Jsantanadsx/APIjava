package br.com.fiap.Bytecode.Service;

import br.com.fiap.Bytecode.DAO.ConsultaDAO;
import br.com.fiap.Bytecode.DAO.PacienteDAO;
import br.com.fiap.Bytecode.Model.Consulta;
import br.com.fiap.Bytecode.Model.Paciente;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ConsultaService {
    private PacienteDAO pacienteDAO;
    private ConsultaDAO consultaDAO;
    public ConsultaService(Connection connection) {
        this.pacienteDAO = new PacienteDAO(connection);
        this.consultaDAO = new ConsultaDAO(connection);
    }

    /**
     * Método com as regras de negócio para agendar uma consulta.
     * Note que ele pode lançar Exceções (ou SQLException).
     */
    public Consulta agendarConsulta(int idPaciente, LocalDateTime dataHora) throws Exception {

        // Regra 1: A data é no passado?
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new Exception("Não é possível agendar consultas em datas passadas.");
        }

        // Regra 2: O paciente existe?
        // Esta é uma chamada REAL ao banco de dados
        Paciente paciente = pacienteDAO.buscarPorId(idPaciente);
        if (paciente == null) {
            throw new Exception("Paciente não encontrado.");
        }

        // Se tudo estiver OK, crie e insira a consulta
        Consulta novaConsulta = new Consulta(
                idPaciente,
                dataHora,
                "https://meet.google.com/link-gerado", // Link gerado pelo serviço
                "AGENDADA"
        );

        // Esta é uma chamada REAL ao banco de dados
        consultaDAO.inserir(novaConsulta);

        return novaConsulta;
    }
}