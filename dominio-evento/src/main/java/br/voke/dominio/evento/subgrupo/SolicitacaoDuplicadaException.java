package br.voke.dominio.evento.subgrupo;

public class SolicitacaoDuplicadaException extends RuntimeException {
    public SolicitacaoDuplicadaException() {
        super("Já existe uma solicitação pendente deste participante para este subgrupo");
    }
}
