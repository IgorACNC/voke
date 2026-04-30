package br.voke.dominio.fidelidade.excecao;

public class LimiteDiarioInsercaoException extends RuntimeException {
    public LimiteDiarioInsercaoException() {
        super("Limite diário de inserção atingido");
    }
}