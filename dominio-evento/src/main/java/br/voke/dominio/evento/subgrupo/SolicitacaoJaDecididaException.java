package br.voke.dominio.evento.subgrupo;

public class SolicitacaoJaDecididaException extends RuntimeException {
    public SolicitacaoJaDecididaException() {
        super("Esta solicitação já foi decidida (aprovada ou rejeitada)");
    }
}
