package br.com.fiap.Bytecode.Controller.dto;

//Representa o objeto JSON que o front-end enviará para
//fazer uma pergunta ao chatbot.
//Ex: { "idPaciente": 1, "pergunta": "Como vejo meus exames?" }

public record ChatbotRequest(
        int idPaciente,
        String pergunta
) {
}