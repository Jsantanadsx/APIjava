package br.com.fiap.Bytecode.Controller.dto;

//serve apenas para guardar dados.
//cria automaticamente os campos, construtor, getters,
// equals() e hashCode().
public record LoginRequest(String email, String senha) {
}