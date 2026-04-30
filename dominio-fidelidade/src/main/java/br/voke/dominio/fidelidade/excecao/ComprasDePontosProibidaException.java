package br.voke.dominio.fidelidade.excecao;

public class ComprasDePontosProibidaException extends RuntimeException {
    public ComprasDePontosProibidaException() {
        super("Não é permitido adquirir pontos com saldo real");
    }
}