package br.com.fiap.Bytecode.Controller;

import br.com.fiap.Bytecode.Controller.dto.ChatbotRequest;
import br.com.fiap.Bytecode.Factory.ConnectionFactory;
import br.com.fiap.Bytecode.Model.ArmazenamentoChatbot;
import br.com.fiap.Bytecode.Service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;

@RestController
@RequestMapping("/chatbot")
@CrossOrigin(origins = "*") // Permite acesso do front-end
public class ChatbotController {

    @PostMapping("/perguntar")
    public ResponseEntity<?> perguntarAoChatbot(@RequestBody ChatbotRequest request) {

        try (Connection connection = ConnectionFactory.getConnection()) {
            ChatbotService chatbotService = new ChatbotService(connection);

            ArmazenamentoChatbot resposta = chatbotService.processarPergunta(
                    request.idPaciente(),
                    request.pergunta()
            );

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}