package br.voke.dominio.fidelidade.excecao;

public class PontosInsuficientesException extends RuntimeException {
    public PontosInsuficientesException() {
        super("Saldo de pontos insuficiente para esta bonificação");
    }
}