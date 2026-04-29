package br.voke.dominio.fidelidade.excecao;

public class LimiteRemocaoException extends RuntimeException {
    public LimiteRemocaoException() {
        super("Limite de remoção de saldo atingido");
    }
}