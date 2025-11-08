package br.com.fiap.Bytecode.Controller;

import br.com.fiap.Bytecode.Controller.dto.AgendamentoRequest;
import br.com.fiap.Bytecode.DAO.ChecklistPacienteDAO;
import br.com.fiap.Bytecode.DAO.ConsultaDAO;
import br.com.fiap.Bytecode.DAO.PosConsultaDAO;
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.ChecklistPaciente;
import br.com.fiap.Bytecode.Model.Consulta;
import br.com.fiap.Bytecode.Model.PosConsulta;
import br.com.fiap.Bytecode.Service.ConsultaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.util.List;

@RestController
@RequestMapping("/consultas")
@CrossOrigin(origins = "*") // Permite acesso do front-end
public class ConsultaController {

    @PostMapping("/agendar")
    public ResponseEntity<?> agendarConsulta(@RequestBody AgendamentoRequest request) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            ConsultaService service = new ConsultaService(connection);

            Consulta novaConsulta = service.agendarConsulta(
                    request.idPaciente(),
                    request.dataHora()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(novaConsulta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/por-paciente/{idPaciente}")
    public ResponseEntity<List<Consulta>> buscarConsultasPorPaciente(@PathVariable int idPaciente) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            ConsultaDAO consultaDAO = new ConsultaDAO(connection);
            List<Consulta> consultas = consultaDAO.buscarPorIdPaciente(idPaciente);

            return ResponseEntity.ok(consultas);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idConsulta}/pos-consulta")
    public ResponseEntity<PosConsulta> buscarPosConsulta(@PathVariable int idConsulta) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            PosConsultaDAO posConsultaDAO = new PosConsultaDAO(connection);
            PosConsulta posConsulta = posConsultaDAO.buscarPorIdConsulta(idConsulta);

            if (posConsulta != null) {
                return ResponseEntity.ok(posConsulta);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idConsulta}/checklist")
    public ResponseEntity<List<ChecklistPaciente>> buscarChecklistDaConsulta(@PathVariable int idConsulta) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            ChecklistPacienteDAO checklistDAO = new ChecklistPacienteDAO(connection);
            List<ChecklistPaciente> checklist = checklistDAO.buscarPorIdConsulta(idConsulta);

            return ResponseEntity.ok(checklist);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}