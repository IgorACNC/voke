package br.voke.dominio.evento.excecao;

public class AvaliacaoDuplicadaException extends RuntimeException {
    public AvaliacaoDuplicadaException() {
        super("Você já avaliou este evento");
    }
}
