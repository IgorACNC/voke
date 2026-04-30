package br.voke.dominio.evento.excecao;

public class InscricaoNaoConfirmadaException extends RuntimeException {
    public InscricaoNaoConfirmadaException() {
        super("Apenas participantes com inscrição confirmada podem avaliar");
    }
}
