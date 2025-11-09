package br.com.fiap.Bytecode.Controller;
import br.com.fiap.Bytecode.Controller.dto.RegistroRequest;
import br.com.fiap.Bytecode.DAO.PacienteDAO;
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.Login;
import br.com.fiap.Bytecode.Model.Paciente;
import br.com.fiap.Bytecode.Service.PacienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Connection;

@RestController
@RequestMapping("/pacientes")
@CrossOrigin(origins = "*") // Permite acesso do front-end
public class PacienteController {

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarNovoPaciente(@RequestBody RegistroRequest request) {

        try (Connection connection = ConnectionFactory.getConnection()) {

            Paciente novoPaciente = new Paciente(
                    request.nome(),
                    request.cpf(),
                    request.dataNascimento(),
                    request.genero()
            );

            Login novoLogin = new Login(
                    0, // ID do paciente será preenchido pelo Service
                    request.email(),
                    request.senha()
            );

            PacienteService pacienteService = new PacienteService(connection);
            Paciente pacienteRegistrado = pacienteService.registrarNovoPaciente(novoPaciente, novoLogin);

            // Retorna 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(pacienteRegistrado);

        } catch (Exception e) {
            e.printStackTrace();
            // Retorna 400 Bad Request (ex: email/cpf já existe)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPacientePorId(@PathVariable int id) {
        try (Connection connection = ConnectionFactory.getConnection()) {

            PacienteDAO pacienteDAO = new PacienteDAO(connection);
            Paciente paciente = pacienteDAO.buscarPorId(id);

            if (paciente != null) {
                return ResponseEntity.ok(paciente); // Retorna 200 OK
            } else {
                return ResponseEntity.notFound().build(); // Retorna 404
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500
        }
    }
}