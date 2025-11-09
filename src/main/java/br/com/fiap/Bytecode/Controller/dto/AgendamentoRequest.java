package br.com.fiap.Bytecode.Controller.dto;

import java.time.LocalDateTime;

// * agendar uma nova consulta.
// * Ex: { "idPaciente": 1, "dataHora": "2025-11-10T14:30:00" }
public record AgendamentoRequest(
        int idPaciente,
        LocalDateTime dataHora
) {
}