package br.voke.dominio.fidelidade.excecao;

public class RecompensaEsgotadaException extends RuntimeException {
    public RecompensaEsgotadaException() {
        super("Recompensa esgotada");
    }
}
