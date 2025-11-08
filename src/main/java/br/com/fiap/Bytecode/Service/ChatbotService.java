package br.com.fiap.Bytecode.Service;

import br.com.fiap.Bytecode.DAO.ArmazenamentoChatbotDAO;
import br.com.fiap.Bytecode.DAO.ChatbotDAO;
import br.com.fiap.Bytecode.Model.ArmazenamentoChatbot;
import br.com.fiap.Bytecode.Model.Chatbot;

import java.sql.Connection;
import java.sql.SQLException;

public class ChatbotService {

    private ChatbotDAO chatbotDAO;
    private ArmazenamentoChatbotDAO armazenamentoDAO;
    private Connection connection;

    public ChatbotService(Connection connection) {
        this.connection = connection;
        this.chatbotDAO = new ChatbotDAO(connection);
        this.armazenamentoDAO = new ArmazenamentoChatbotDAO(connection);
    }

    /**
     * Regra de Negócio: Processa uma nova pergunta do usuário.
     * 1. Salva a pergunta do usuário.
     * 2. Gera uma resposta (aqui é onde uma IA ou lógica de regras entraria).
     * 3. Salva a resposta do bot.
     * 4. Retorna a resposta.
     */
    public ArmazenamentoChatbot processarPergunta(int idPaciente, String pergunta) throws Exception {

        if (pergunta == null || pergunta.trim().isEmpty()) {
            throw new Exception("A pergunta não pode estar vazia.");
        }

        try {
            // 1. Inicia o controle da transação
            connection.setAutoCommit(false);

            // 2. Salva a pergunta do usuário
            Chatbot novaPergunta = new Chatbot(idPaciente, pergunta);
            chatbotDAO.inserir(novaPergunta);

            int idPergunta = novaPergunta.getIdChatbot();
            if (idPergunta <= 0) {
                throw new Exception("Falha ao salvar a pergunta.");
            }

            // 3. Gera a resposta (Lógica de Negócio)
            String textoResposta = gerarRespostaSimples(pergunta);

            // 4. Salva a resposta do bot
            ArmazenamentoChatbot novaResposta = new ArmazenamentoChatbot(idPergunta, textoResposta);
            armazenamentoDAO.inserir(novaResposta);

            // 5. Sucesso: Confirma a transação
            connection.commit();

            return novaResposta;

        } catch (Exception e) {
            // 6. Falha: Desfaz tudo
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Erro ao reverter transação do chatbot: " + rollbackEx.getMessage());
            }
            throw e; // Lança a exceção original

        } finally {
            // 7. Limpeza: Devolve ao estado padrão
            try {
                connection.setAutoCommit(true);
            } catch (SQLException finalEx) {
                System.err.println("Erro ao resetar auto-commit: " + finalEx.getMessage());
            }
        }
    }

    /**
     * Método privado que simula a lógica de resposta do chatbot.
     */
    private String gerarRespostaSimples(String pergunta) {
        pergunta = pergunta.toLowerCase();

        if (pergunta.contains("remarcar") || pergunta.contains("cancelar")) {
            return "Para remarcações ou cancelamentos, por favor, entre em contato diretamente com a clínica.";
        }
        if (pergunta.contains("receita") || pergunta.contains("atestado")) {
            return "Seu médico disponibilizará receitas e atestados na seção 'Pós-Consulta' após o término do atendimento.";
        }
        if (pergunta.contains("olá") || pergunta.contains("oi") || pergunta.contains("bom dia")) {
            return "Olá! Sou seu assistente virtual. Como posso ajudar?";
        }

        return "Não consegui entender sua pergunta. Por favor, tente reformular ou aguarde sua consulta para falar com o médico.";
    }
}