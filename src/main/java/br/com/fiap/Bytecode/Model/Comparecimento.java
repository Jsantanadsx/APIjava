package br.com.fiap.Bytecode.Model;

public enum Comparecimento {
    S("S"),
    N("N");
    private final String valor;

    Comparecimento(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}