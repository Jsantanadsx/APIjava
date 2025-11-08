package br.com.fiap.Bytecode.Controller.dto;

import java.time.LocalDate;
public record RegistroRequest(
        // Dados do Paciente
        String nome,
        String cpf,
        LocalDate dataNascimento,
        String genero,

        // Dados do Login
        String email,
        String senha
) {
}