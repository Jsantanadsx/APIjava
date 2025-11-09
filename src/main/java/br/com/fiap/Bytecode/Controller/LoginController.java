package br.com.fiap.Bytecode.Controller;
import br.com.fiap.Bytecode.Controller.dto.LoginRequest;
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.Paciente;
import br.com.fiap.Bytecode.Service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Connection;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "*") // Permite acesso do front-end
public class LoginController {

    @PostMapping
    public ResponseEntity<?> autenticar(@RequestBody LoginRequest loginRequest) {

        try (Connection connection = ConnectionFactory.getConnection()) {
            LoginService loginService = new LoginService(connection);

            Paciente pacienteAutenticado = loginService.autenticar(
                    loginRequest.email(),
                    loginRequest.senha()
            );

            // Retorna 200 OK e o objeto Paciente
            return ResponseEntity.ok(pacienteAutenticado);

        } catch (Exception e) {
            e.printStackTrace();
            // Retorna 401 Unauthorized e a mensagem de erro
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}